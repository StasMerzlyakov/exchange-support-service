package ru.otus.exchange.blobstorage.config.storage;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.otus.exchange.blobstorage.*;
import ru.otus.exchange.blobstorage.memory.BufferCache;
import ru.otus.exchange.blobstorage.memory.MemorySyncStorage;
import ru.otus.exchange.blobstorage.minio.MinioConfig;
import ru.otus.exchange.blobstorage.minio.MinoSyncClientStorage;

@Configuration
public class StorageConfiguration {

    @Bean
    public InternalSyncStorage memorySyncStorage(BufferCache bufferCache) {
        return new MemorySyncStorage(bufferCache);
    }

    @Bean
    public FutureStorage memoryFutureStorage(MinioProperties minioProperties, InternalSyncStorage memorySyncStorage) {
        return new FutureStorageImpl(minioProperties.getOpTimeout(), memorySyncStorage);
    }

    @Bean
    public Storage memoryStorage(MinioProperties minioProperties, FutureStorage memoryFutureStorage) {
        return new FutureStorageAdapter(minioProperties.getOpTimeout(), memoryFutureStorage);
    }

    @Bean
    public MinioClient minoClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public InternalSyncStorage minioSyncStorage(MinioClient minioClient, MinioProperties minioProperties) {
        return new MinoSyncClientStorage(
                minioClient, new MinioConfig(minioProperties.getBucket(), minioProperties.getOpTimeout()));
    }

    @Bean
    public FutureStorage minioFutureStorage(MinioProperties minioProperties, InternalSyncStorage minioSyncStorage) {
        return new FutureStorageImpl(minioProperties.getOpTimeout(), minioSyncStorage);
    }

    @Bean
    public Storage minioStorage(MinioProperties minioProperties, FutureStorage minioFutureStorage) {
        return new FutureStorageAdapter(minioProperties.getOpTimeout(), minioFutureStorage);
    }

    @Bean
    @Primary
    public Storage chainStorage(Storage minioStorage, Storage memoryStorage) {
        return new ChainStorage(memoryStorage, minioStorage);
    }
}
