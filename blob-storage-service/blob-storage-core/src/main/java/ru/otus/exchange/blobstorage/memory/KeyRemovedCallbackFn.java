package ru.otus.exchange.blobstorage.memory;

@FunctionalInterface
public interface KeyRemovedCallbackFn<K> {
    void remove(K key);
}
