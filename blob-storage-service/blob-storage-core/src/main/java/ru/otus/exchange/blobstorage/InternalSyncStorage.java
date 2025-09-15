package ru.otus.exchange.blobstorage;

import java.nio.ByteBuffer;
import java.util.List;

public interface InternalSyncStorage {
    List<StorageKey> listExchange(String exchange);

    boolean writeObject(StorageKey storageKey, StorageData storageData);

    Metadata readMetadata(StorageKey storageKey);

    ByteBuffer readObject(StorageKey storageKey);

    boolean removeObject(StorageKey storageKey);

    boolean removeMetadata(StorageKey storageKey);
}
