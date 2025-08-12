package ru.otus.exchange.blobstorage.minio;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.otus.exchange.blobstorage.*;

@Slf4j
public class MinoSyncClientStorage implements InternalSyncStorage {

    private final MinioClient minioClient;

    private final MinioConfig minioConfig;

    public MinoSyncClientStorage(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    @Override
    @SuppressWarnings("java:S1168")
    public List<StorageKey> listExchange(String exchange) {
        try {
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(minioConfig.bucket())
                    .prefix(exchange)
                    .recursive(true)
                    .includeVersions(true)
                    .build();

            var listObjectsIterator = minioClient.listObjects(args).iterator();
            List<StorageKey> resultList = new ArrayList<>();

            while (listObjectsIterator.hasNext()) {
                var item = listObjectsIterator.next();
                var objectPath = item.get().objectName();
                var storageKey = fromObjectPath(objectPath);
                resultList.add(storageKey);
            }
            return resultList;
        } catch (Exception e) {
            log.error("listExchange error", e);
            return null;
        }
    }

    @Override
    public boolean writeObject(StorageKey storageKey, StorageData storageData) {

        var objectPath = toObjectPath(storageKey);
        log.info("writeObject {}", objectPath);

        var byteBuffer = storageData.byteBuffer();

        var size = byteBuffer.remaining();
        var sha256Digest = Utils.hexDigest(byteBuffer.array());

        var metadata = storageData.metadata();
        if (size != metadata.size()) {
            log.warn("expected metadata.size {}, actual {}", metadata.size(), size);

            log.warn("{}", new String(byteBuffer.array(), StandardCharsets.UTF_8));

            return false;
        }

        if (!sha256Digest.equals(metadata.sha256Digest())) {
            log.warn("expected metadata.sha256Digest {}, actual {}", metadata.sha256Digest(), sha256Digest);
            return false;
        }

        Map<String, String> tags = new HashMap<>();
        tags.put(OBJECT_SIZE_TAG, String.valueOf(size));
        tags.put(OBJECT_SHA256_DIGEST, sha256Digest);

        try (var is = new ByteBufferBackedInputStream(byteBuffer)) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioConfig.bucket()).object(objectPath).stream(is, size, -1)
                            .tags(tags)
                            .build());
            return true;
        } catch (Exception e) {
            log.error("write error", e);
            return false;
        }
    }

    @Override
    public Metadata readMetadata(StorageKey storageKey) {
        try {
            var objectPath = toObjectPath(storageKey);
            var tags = minioClient.getObjectTags(GetObjectTagsArgs.builder()
                    .bucket(minioConfig.bucket())
                    .object(objectPath)
                    .build());
            Metadata metadata = null;
            if (tags == null) {
                log.info("object by path {} not found", objectPath);
            } else {
                var size = Integer.parseInt(tags.get().get(OBJECT_SIZE_TAG));
                var sha256Digest = tags.get().get(OBJECT_SHA256_DIGEST);
                metadata = new Metadata(size, sha256Digest);
            }
            return metadata;
        } catch (ErrorResponseException re) {
            if (!NO_SUCH_KEY.equals(re.errorResponse().code())) {
                log.error("minio readMetadata error", re);
            }
        } catch (Exception e) {
            log.error("readMetadata error", e);
        }
        return null;
    }

    @Override
    public ByteBuffer readObject(StorageKey storageKey) {
        var objectPath = toObjectPath(storageKey);
        try {
            ByteBuffer byteBuffer;
            try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.bucket())
                    .object(objectPath)
                    .build())) {
                byte[] bytes = IOUtils.toByteArray(stream);
                byteBuffer = ByteBuffer.allocate(bytes.length);
                byteBuffer.put(bytes);
                byteBuffer.flip();
            }
            log.info("object by {} found", objectPath);
            return byteBuffer;
        } catch (ErrorResponseException re) {
            if (!NO_SUCH_KEY.equals(re.errorResponse().code())) {
                log.error("minio readObject error", re);
            }
        } catch (Exception e) {
            log.error("readObject error", e);
        }
        log.warn("object by {} not found", objectPath);
        return null;
    }

    @Override
    public boolean removeObject(StorageKey storageKey) {

        String objectPath = toObjectPath(storageKey);
        log.info("removeObject {}", objectPath);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.bucket())
                    .object(objectPath)
                    .build());
            return true;
        } catch (ErrorResponseException re) {
            if (!NO_SUCH_KEY.equals(re.errorResponse().code())) {
                log.error("minio removeObject error", re);
            } else {
                log.warn("", re);
            }

        } catch (Exception e) {
            log.error("removeObject error", e);
        }
        return false;
    }

    @Override
    public boolean removeMetadata(StorageKey storageKey) {
        var objectPath = toObjectPath(storageKey);
        log.info("removeMetadata {}", objectPath);
        try {
            minioClient.deleteObjectTags(DeleteObjectTagsArgs.builder()
                    .bucket(minioConfig.bucket())
                    .object(objectPath)
                    .build());
            return true;
        } catch (ErrorResponseException re) {
            if (!NO_SUCH_KEY.equals(re.errorResponse().code())) {
                log.error("minio removeMetadata error", re);
            }
        } catch (Exception e) {
            log.error("removeMetadata error", e);
        }
        return false;
    }

    private String toObjectPath(StorageKey storageKey) {
        return String.join("/", storageKey.exchange(), storageKey.key());
    }

    private StorageKey fromObjectPath(String objectPath) {
        var args = objectPath.split("/");
        return new StorageKey(args[0], args[1]);
    }

    static final String OBJECT_SIZE_TAG = "Size";
    static final String OBJECT_SHA256_DIGEST = "Digest";

    static final String NO_SUCH_KEY = "NoSuchKey";
}
