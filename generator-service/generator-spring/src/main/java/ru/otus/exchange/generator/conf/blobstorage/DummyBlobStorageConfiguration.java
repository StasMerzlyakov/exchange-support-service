package ru.otus.exchange.generator.conf.blobstorage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.Metadata;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.StorageSync;

@Configuration
@ConditionalOnProperty(name = "blobStorageType", havingValue = "dummy")
public class DummyBlobStorageConfiguration {

    @Bean
    public StorageSync dummyStorage() {
        return new StorageSync() {
            @Override
            public StorageData read(StorageKey storageKey) {
                return null;
            }

            @Override
            public Boolean write(StorageKey storageKey, StorageData storageData) {
                return true;
            }

            @Override
            public Boolean delete(StorageKey storageKey) {
                return true;
            }

            @Override
            public Boolean deleteAll(String exchange) {
                return true;
            }

            @Override
            public Metadata getMetadata(StorageKey storageKey) {
                return null;
            }
        };
    }
}
