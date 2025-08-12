package ru.otus.exchange.sender.core;

import java.nio.ByteBuffer;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobutils.BlobLoaderCallback;

public class BlobLoaderCallbackImpl implements BlobLoaderCallback {

    private final StorageSync storageSync;

    public BlobLoaderCallbackImpl(StorageSync storageSync) {
        this.storageSync = storageSync;
    }

    @Override
    public ByteBuffer loadObject(String exchange, String name) {
        StorageKey storageKey = new StorageKey(exchange, name);
        return storageSync.read(storageKey).byteBuffer();
    }
}
