package ru.otus.exchange.blobstorage.memory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.otus.exchange.blobstorage.exceptions.OverrideForbiddenException;

public class GenericCache<K, V> implements MemoryCache<K, V> {

    private final Map<K, ValueReference<K, V>> map;
    private final ReferenceQueue<V> referenceQueue;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public GenericCache() {
        this.map = new HashMap<>();
        this.referenceQueue = new ReferenceQueue<>();
    }

    private static class ValueReference<K, V> extends SoftReference<V> {
        private final K key;

        ValueReference(K key, V value, ReferenceQueue<V> queue) {
            super(value, queue);
            this.key = key;
        }
    }

    public void put(K key, V value, KeyCreatedCallbackFn<K> callbackFn) {
        clear();
        Lock wLock = rwLock.writeLock();
        wLock.lock();
        try {
            var valueRef = new ValueReference<>(key, value, referenceQueue);
            if (map.containsKey(key)) {
                var ref = map.get(key);
                if (ref.get() != null) {
                    throw new OverrideForbiddenException("object already exists");
                }
            }
            map.put(key, valueRef);
            callbackFn.invoke(key);
        } finally {
            wLock.unlock();
        }
    }

    public V get(K key, KeyRemovedCallbackFn<K> keyRemovedFn) {
        clear();
        Lock rLock = rwLock.readLock();
        rLock.lock();
        try {
            if (map.containsKey(key)) {
                var ref = map.get(key);
                var value = ref.get();
                if (value != null) {
                    return value;
                } else {
                    Lock wLock = rwLock.writeLock();
                    wLock.lock();
                    try {
                        map.remove(key);
                        keyRemovedFn.remove(key);
                    } finally {
                        wLock.unlock();
                    }
                }
            }
            return null;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void remove(K key, KeyRemovedCallbackFn<K> keyRemovedFn) {
        clear();
        Lock wlock = rwLock.writeLock();
        wlock.lock();
        try {
            map.remove(key);
            keyRemovedFn.remove(key);
        } finally {
            wlock.unlock();
        }
    }

    private void clear() {
        Reference<? extends V> ref;
        while ((ref = referenceQueue.poll()) != null) {
            synchronized (referenceQueue) {
                if (ref instanceof ValueReference<?, ?> vr) {
                    K key = (K) vr.key;
                    map.remove(key);
                } else {
                    throw new IllegalStateException("Unknown reference type");
                }
            }
        }
    }
}
