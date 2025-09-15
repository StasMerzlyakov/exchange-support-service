package ru.otus.exchange.generator;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobstorage.Utils;
import ru.otus.exchange.blobutils.Archiver;
import ru.otus.exchange.generator.errors.BlobStorageException;

@Slf4j
public class SyncStorageAdapter implements DataStorage {

    private final StorageSync storageSync;

    public SyncStorageAdapter(StorageSync storageSync) {
        this.storageSync = storageSync;
    }

    @Override
    public boolean isExists(StorageKey storageKey) throws BlobStorageException {
        try {
            return storageSync.getMetadata(storageKey) != null;
        } catch (Exception e) {
            throw new BlobStorageException(e);
        }
    }

    @Override
    public byte[] readData(StorageKey storageKey) throws BlobStorageException {
        StorageData storageData = storageSync.read(storageKey);
        if (storageData == null) {
            return new byte[0];
        }

        byte[] arch = storageData.byteBuffer().array();
        try {
            return Archiver.decompress(arch);
        } catch (IOException e) {
            throw new BlobStorageException(e);
        }
    }

    @Override
    public void writeData(StorageKey storageKey, byte[] message) {
        try {
            byte[] arch = Archiver.compress(message);
            StorageData storageData = Utils.createStorageData(arch);
            storageSync.write(storageKey, storageData);
        } catch (IOException e) {
            throw new BlobStorageException(e);
        }
    }
}
