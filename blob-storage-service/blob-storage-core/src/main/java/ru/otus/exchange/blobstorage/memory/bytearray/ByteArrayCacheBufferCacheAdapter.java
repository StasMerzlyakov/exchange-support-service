package ru.otus.exchange.blobstorage.memory.bytearray;

import java.nio.ByteBuffer;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.memory.BufferCache;
import ru.otus.exchange.blobstorage.memory.KeyCreatedCallbackFn;
import ru.otus.exchange.blobstorage.memory.KeyRemovedCallbackFn;

public class ByteArrayCacheBufferCacheAdapter implements BufferCache {

    private final ByteArrayCache byteArrayCache;

    public ByteArrayCacheBufferCacheAdapter(ByteArrayCache byteArrayCache) {
        this.byteArrayCache = byteArrayCache;
    }

    @Override
    public void put(StorageKey key, ByteBuffer value, KeyCreatedCallbackFn<StorageKey> callbackFn) {
        byteArrayCache.put(key, value.array(), callbackFn);
    }

    @Override
    public ByteBuffer get(StorageKey key, KeyRemovedCallbackFn<StorageKey> keyRemovedFn) {
        byte[] array = byteArrayCache.get(key, keyRemovedFn);
        ByteBuffer byteBuffer = null;
        if (array != null) {
            byteBuffer = ByteBuffer.wrap(array);
        }
        return byteBuffer;
    }

    @Override
    public void remove(StorageKey key, KeyRemovedCallbackFn<StorageKey> keyRemovedFn) {
        byteArrayCache.remove(key, keyRemovedFn);
    }
}
