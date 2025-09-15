package ru.otus.exchange.generator;

import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.generator.errors.BlobStorageException;

public interface DataStorage {
    boolean isExists(StorageKey storageKey) throws BlobStorageException;

    byte[] readData(StorageKey storageKey) throws BlobStorageException;

    void writeData(StorageKey storageKey, byte[] message) throws BlobStorageException;
}
