package ru.otus.exchange.blobstorage.memory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobstorage.InternalSyncStorage;
import ru.otus.exchange.blobstorage.Metadata;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;

@Slf4j
public class MemorySyncStorage implements InternalSyncStorage {

    private final Map<StorageKey, Metadata> metadataMap = new ConcurrentHashMap<>();

    private final BufferCache bufferCache;

    public MemorySyncStorage(BufferCache bufferCache) {
        this.bufferCache = bufferCache;
    }

    @Override
    public List<StorageKey> listExchange(String exchange) {
        return metadataMap.keySet().stream()
                .filter(storageKey -> exchange.equals(storageKey.exchange()))
                .toList();
    }

    @Override
    public boolean writeObject(StorageKey storageKey, StorageData storageData) {
        bufferCache.put(storageKey, storageData.byteBuffer(), sKey -> metadataMap.put(sKey, storageData.metadata()));
        return true;
    }

    @Override
    public Metadata readMetadata(StorageKey storageKey) {
        Metadata metadata = metadataMap.get(storageKey);
        if (metadata == null) {
            log.info("metadata by key {} not found", storageKey);
        } else {
            log.info("metadata by key {} exists", storageKey);
        }
        return metadata;
    }

    @Override
    public ByteBuffer readObject(StorageKey storageKey) {
        var byteBuffer = bufferCache.get(storageKey, metadataMap::remove);
        if (byteBuffer == null) {
            log.info("byteBuffer by key {} not found", storageKey);
        } else {
            log.info("byteBuffer by key {} exists", storageKey);
        }
        return byteBuffer;
    }

    @Override
    public boolean removeObject(StorageKey storageKey) {
        bufferCache.remove(storageKey, metadataMap::remove);
        log.info("removeObject {}", storageKey);
        return true;
    }

    @Override
    public boolean removeMetadata(StorageKey storageKey) {
        return true; // not supported
    }
}
