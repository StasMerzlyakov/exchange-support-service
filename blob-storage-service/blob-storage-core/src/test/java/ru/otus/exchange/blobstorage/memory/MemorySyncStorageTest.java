package ru.otus.exchange.blobstorage.memory;

import static ru.otus.exchange.blobstorage.TestUtils.createObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.exchange.blobstorage.InternalSyncStorage;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.memory.bytearray.ByteArrayCache;
import ru.otus.exchange.blobstorage.memory.bytearray.ByteArrayCacheBufferCacheAdapter;

class MemorySyncStorageTest {

    @Test
    @DisplayName("write + readMetadata + delete")
    void test1() {
        String exchange = "exchange";
        String key = "key";
        StorageData expectedStorageData = createObject();

        StorageKey storageKey = new StorageKey(exchange, key);

        var byteArrayCache = new ByteArrayCache();
        var byteArrayCacheAdapter = new ByteArrayCacheBufferCacheAdapter(byteArrayCache);

        InternalSyncStorage syncStorage = new MemorySyncStorage(byteArrayCacheAdapter);

        Assertions.assertDoesNotThrow(
                () -> Assertions.assertTrue(syncStorage.writeObject(storageKey, expectedStorageData)));

        Assertions.assertDoesNotThrow(() -> {
            var metadata = syncStorage.readMetadata(storageKey);
            Assertions.assertEquals(expectedStorageData.metadata().size(), metadata.size());
            Assertions.assertEquals(expectedStorageData.metadata().sha256Digest(), metadata.sha256Digest());

            var obj = syncStorage.readObject(storageKey);
            Assertions.assertNotNull(obj);
            Assertions.assertEquals(expectedStorageData.metadata().size(), obj.remaining());
        });

        Assertions.assertDoesNotThrow(() -> {
            var list = syncStorage.listExchange(exchange);
            list.forEach(sKey -> {
                syncStorage.removeObject(sKey);
                syncStorage.removeMetadata(sKey);
            });
        });

        var metadata = syncStorage.readMetadata(storageKey);
        Assertions.assertNull(metadata);

        var obj = syncStorage.readObject(storageKey);
        Assertions.assertNull(obj);
    }
}
