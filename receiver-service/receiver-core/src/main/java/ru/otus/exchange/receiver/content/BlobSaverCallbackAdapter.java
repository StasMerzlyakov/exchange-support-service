package ru.otus.exchange.receiver.content;

import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobstorage.Utils;
import ru.otus.exchange.blobutils.BlobSaverCallback;

@Slf4j
public class BlobSaverCallbackAdapter implements BlobSaverCallback {

    private final StorageSync syncStorage;

    public BlobSaverCallbackAdapter(StorageSync syncStorage) {
        this.syncStorage = syncStorage;
    }

    @Override
    public void saveObject(String exchange, String fileName, byte[] object) {
        var storageData = Utils.createStorageData(object);
        StorageKey storageKey = new StorageKey(exchange, fileName);
        var result = syncStorage.write(storageKey, storageData);
        if (!Boolean.TRUE.equals(result)) {
            log.warn("write by key {} operation return false", storageKey);
        }
    }
}
