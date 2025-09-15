package ru.otus.exchange.blobstorage;

import java.util.concurrent.Future;

public interface FutureStorage {
    Future<StorageData> readFuture(StorageKey storageKey);

    Future<Metadata> readMetadataFuture(StorageKey storageKey);

    Future<Boolean> writeFuture(StorageKey storageKey, StorageData storageData);

    Future<Boolean> deleteFuture(StorageKey storageKey);

    Future<Boolean> deleteAllFuture(String exchange);
}
