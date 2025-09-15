package ru.otus.exchange.blobstorage;

public interface StorageSync {
    StorageData read(StorageKey storageKey);

    Boolean write(StorageKey storageKey, StorageData storageData);

    Boolean delete(StorageKey storageKey);

    Boolean deleteAll(String exchange);

    Metadata getMetadata(StorageKey storageKey);
}
