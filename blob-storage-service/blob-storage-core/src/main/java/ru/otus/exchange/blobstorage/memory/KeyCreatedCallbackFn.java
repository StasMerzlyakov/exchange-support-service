package ru.otus.exchange.blobstorage.memory;

@FunctionalInterface
public interface KeyCreatedCallbackFn<K> {
    void invoke(K storageKey);
}
