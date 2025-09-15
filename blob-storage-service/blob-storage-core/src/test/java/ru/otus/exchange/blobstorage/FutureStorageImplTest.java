package ru.otus.exchange.blobstorage;

import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

class FutureStorageImplTest {

    @Test
    @DisplayName("read data")
    void test1() {
        InternalSyncStorage syncStorage = Mockito.mock(InternalSyncStorage.class);

        StorageKey storageKey = new StorageKey("exchange", "key");

        StorageData expectedStorageData = TestUtils.createObject();

        FutureStorageImpl futureStorage = new FutureStorageImpl(Duration.ofSeconds(5), syncStorage);

        AtomicReference<StorageData> storageDataRef = new AtomicReference<>();

        when(syncStorage.readObject(storageKey)).thenReturn(expectedStorageData.byteBuffer());
        when(syncStorage.readMetadata(storageKey)).thenReturn(expectedStorageData.metadata());

        Assertions.assertDoesNotThrow(
                () -> storageDataRef.set(futureStorage.readFuture(storageKey).get()));

        StorageData actualStorageData = storageDataRef.get();

        Assertions.assertNotNull(actualStorageData);
        Assertions.assertEquals(expectedStorageData.metadata(), actualStorageData.metadata());

        Assertions.assertEquals(actualStorageData.byteBuffer(), expectedStorageData.byteBuffer());
    }

    @Test
    @DisplayName("write data")
    void test2() {
        InternalSyncStorage syncStorage = Mockito.mock(InternalSyncStorage.class);

        StorageKey storageKey = new StorageKey("exchange", "key");

        StorageData expectedStorageData = TestUtils.createObject();

        FutureStorageImpl futureStorage = new FutureStorageImpl(Duration.ofSeconds(5), syncStorage);
        when(syncStorage.writeObject(storageKey, expectedStorageData)).thenReturn(true);

        AtomicReference<Boolean> resultRef = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> resultRef.set(
                futureStorage.writeFuture(storageKey, expectedStorageData).get()));
        Assertions.assertTrue(resultRef.get());

        verify(syncStorage, times(1)).writeObject(storageKey, expectedStorageData);
    }

    @Test
    @DisplayName("delete data")
    void test3() {
        InternalSyncStorage syncStorage = Mockito.mock(InternalSyncStorage.class);

        StorageKey storageKey = new StorageKey("exchange", "key");

        FutureStorageImpl futureStorage = new FutureStorageImpl(Duration.ofSeconds(5), syncStorage);

        List<StorageKey> storageKeyList = new ArrayList<>();
        storageKeyList.add(storageKey);
        storageKeyList.add(storageKey);
        when(syncStorage.listExchange(storageKey.exchange())).thenReturn(storageKeyList);

        when(syncStorage.removeObject(storageKey)).thenReturn(true);
        when(syncStorage.removeMetadata(storageKey)).thenReturn(true);

        AtomicReference<Boolean> resultRef = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> resultRef.set(
                futureStorage.deleteAllFuture(storageKey.exchange()).get()));
        Assertions.assertTrue(resultRef.get());

        verify(syncStorage, times(1)).listExchange(storageKey.exchange());
        verify(syncStorage, times(2)).removeMetadata(storageKey);
        verify(syncStorage, times(2)).removeObject(storageKey);
    }
}
