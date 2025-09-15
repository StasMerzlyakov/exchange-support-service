package ru.otus.exchange.blobstorage;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Mono;

public class FutureStorageAdapter implements Storage {

    private final FutureStorage futureStorage;
    private final Duration waitTimeout;

    public FutureStorageAdapter(Duration waitTimeout, FutureStorage futureStorage) {
        this.futureStorage = futureStorage;
        this.waitTimeout = waitTimeout;
    }

    @Override
    public Mono<StorageData> read(StorageKey storageKey) {
        return Mono.fromCallable(
                () -> futureStorage.readFuture(storageKey).get(waitTimeout.getSeconds(), TimeUnit.SECONDS));
    }

    @Override
    public Mono<Boolean> write(StorageKey storageKey, StorageData storageData) {
        return Mono.fromCallable(() ->
                futureStorage.writeFuture(storageKey, storageData).get(waitTimeout.getSeconds(), TimeUnit.SECONDS));
    }

    @Override
    public Mono<Boolean> delete(StorageKey storageKey) {
        return Mono.fromCallable(
                () -> futureStorage.deleteFuture(storageKey).get(waitTimeout.getSeconds(), TimeUnit.SECONDS));
    }

    @Override
    public Mono<Boolean> deleteAll(String exchange) {
        return Mono.fromCallable(
                () -> futureStorage.deleteAllFuture(exchange).get(waitTimeout.getSeconds(), TimeUnit.SECONDS));
    }

    @Override
    public Mono<Metadata> getMetadata(StorageKey storageKey) {
        return Mono.fromCallable(
                () -> futureStorage.readMetadataFuture(storageKey).get(waitTimeout.getSeconds(), TimeUnit.SECONDS));
    }
}
