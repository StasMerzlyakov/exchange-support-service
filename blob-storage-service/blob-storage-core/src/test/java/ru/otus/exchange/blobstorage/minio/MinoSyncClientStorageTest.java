package ru.otus.exchange.blobstorage.minio;

import static ru.otus.exchange.blobstorage.TestUtils.createObject;
import static ru.otus.exchange.blobstorage.minio.MinoSyncClientStorage.OBJECT_SHA256_DIGEST;
import static ru.otus.exchange.blobstorage.minio.MinoSyncClientStorage.OBJECT_SIZE_TAG;

import io.minio.GetObjectTagsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import java.time.Duration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MinIOContainer;
import ru.otus.exchange.blobstorage.*;

class MinoSyncClientStorageTest {

    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String BUCKET = "bucket";

    static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
            .withUserName(ACCESS_KEY)
            .withPassword(SECRET_KEY);

    private static MinioClient minioClient;

    private static MinioConfig minioConfig;

    @SneakyThrows
    @BeforeAll
    static void startContainers() {
        minio.start();

        String endpoint = minio.getS3URL();

        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
        minioClient.makeBucket(new MakeBucketArgs.Builder().bucket(BUCKET).build());

        minioConfig = new MinioConfig(BUCKET, Duration.ofSeconds(5));
    }

    @AfterAll
    static void stopContainers() {
        minio.stop();
    }

    @SneakyThrows
    @Test
    @DisplayName("write + readMetadata + delete")
    void test1() {
        String exchange = "exchange";
        String key = "key";
        StorageData storageData = createObject();

        StorageKey storageKey = new StorageKey(exchange, key);

        InternalSyncStorage syncClientStorage = new MinoSyncClientStorage(minioClient, minioConfig);

        Assertions.assertDoesNotThrow(
                () -> Assertions.assertTrue(syncClientStorage.writeObject(storageKey, storageData)));

        String objectPath = String.join("/", exchange, key);

        var tags = minioClient.getObjectTags(GetObjectTagsArgs.builder()
                .bucket(minioConfig.bucket())
                .object(objectPath)
                .build());

        Assertions.assertNotNull(tags);

        var size = Integer.parseInt(tags.get().get(OBJECT_SIZE_TAG));
        var sha256Digest = tags.get().get(OBJECT_SHA256_DIGEST);

        Assertions.assertEquals(storageData.metadata().size(), size);
        Assertions.assertEquals(storageData.metadata().sha256Digest(), sha256Digest);

        Assertions.assertDoesNotThrow(() -> {
            var metadata = syncClientStorage.readMetadata(storageKey);
            Assertions.assertEquals(storageData.metadata().size(), metadata.size());
            Assertions.assertEquals(storageData.metadata().sha256Digest(), metadata.sha256Digest());

            var obj = syncClientStorage.readObject(storageKey);
            Assertions.assertNotNull(obj);
            Assertions.assertEquals(size, obj.remaining());
        });

        Assertions.assertDoesNotThrow(() -> {
            var list = syncClientStorage.listExchange(exchange);
            list.forEach(sKey -> {
                syncClientStorage.removeObject(sKey);
                syncClientStorage.removeMetadata(sKey);
            });
        });

        var metadata = syncClientStorage.readMetadata(storageKey);
        Assertions.assertNull(metadata);

        var obj = syncClientStorage.readObject(storageKey);
        Assertions.assertNull(obj);
    }
}
