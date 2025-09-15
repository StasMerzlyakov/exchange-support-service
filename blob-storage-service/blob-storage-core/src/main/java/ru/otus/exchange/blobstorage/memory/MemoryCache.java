package ru.otus.exchange.blobstorage.memory;

public interface MemoryCache<K, V> {
    void put(K key, V value, KeyCreatedCallbackFn<K> callbackFn);

    V get(K key, KeyRemovedCallbackFn<K> keyRemovedFn);

    void remove(K key, KeyRemovedCallbackFn<K> keyRemovedFn);
}
