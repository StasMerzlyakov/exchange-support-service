package ru.otus.exchange.blobstorage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.exchange.blobstorage.exceptions.RedisException;

@ExtendWith(MockitoExtension.class)
class ChainStorageTest {

    @SneakyThrows
    @Test
    void doTest1() {
        var byteBuffer1 = ByteBuffer.allocate(10);
        var storageData1 = new StorageData(new Metadata(10, "abc"), byteBuffer1);

        var currentStorage = Mockito.mock(Storage.class);
        var nextStorage = Mockito.mock(Storage.class);

        when(currentStorage.read(any())).thenReturn(Mono.just(storageData1));

        var chainStorage = new ChainStorage(currentStorage, nextStorage);
        var bufferMono = chainStorage.read(new StorageKey("exchange", "key"));

        var actual = bufferMono.block();

        Assertions.assertEquals(storageData1, actual);
        verify(nextStorage, times(0)).read(any());
    }

    @SneakyThrows
    @Test
    void doTest2() {
        var byteBuffer2 = ByteBuffer.allocate(20);

        var storageData2 = new StorageData(new Metadata(20, "abc"), byteBuffer2);

        var currentStorage = Mockito.mock(Storage.class);
        var nextStorage = Mockito.mock(Storage.class);

        when(currentStorage.read(any())).thenReturn(Mono.empty());
        when(nextStorage.read(any())).thenReturn(Mono.just(storageData2));

        var chainStorage = new ChainStorage(currentStorage, nextStorage);
        var storageKey = new StorageKey("exchange", "key");
        var bufferMono = chainStorage.read(storageKey);

        var actual = bufferMono.block();

        Assertions.assertEquals(storageData2, actual);
        verify(currentStorage, times(1)).read(any());
        verify(nextStorage, times(1)).read(any());
        verify(currentStorage, times(1)).write(storageKey, storageData2);
    }

    @SneakyThrows
    @Test
    void doTest3() {

        var currentStorage = Mockito.mock(Storage.class);
        var nextStorage = Mockito.mock(Storage.class);

        when(currentStorage.read(any())).thenReturn(Mono.empty());
        when(nextStorage.read(any())).thenReturn(Mono.empty());

        var chainStorage = new ChainStorage(currentStorage, nextStorage);
        var storageKey = new StorageKey("exchange", "key");
        var bufferMono = chainStorage.read(storageKey);

        StepVerifier.create(bufferMono).expectNextCount(0).verifyComplete();

        verify(currentStorage, times(1)).read(any());
        verify(nextStorage, times(1)).read(any());
        verify(currentStorage, times(0)).write(eq(storageKey), any());
    }

    @SneakyThrows
    @Test
    void doTest4() {
        var currentStorage = Mockito.mock(Storage.class);
        var nextStorage = Mockito.mock(Storage.class);

        when(currentStorage.read(any())).thenReturn(Mono.error(new RedisException("redis is down")));

        var chainStorage = new ChainStorage(currentStorage, nextStorage);
        var storageKey = new StorageKey("exchange", "key");

        var readResult = chainStorage.read(storageKey);
        StepVerifier.create(readResult).expectError(RedisException.class).verify();

        verify(currentStorage, times(1)).read(any());
        verify(nextStorage, times(0)).read(any());
    }

    @SneakyThrows
    @Test
    void doTest5() {
        var currentStorage = Mockito.mock(Storage.class);
        var nextStorage = Mockito.mock(Storage.class);

        when(currentStorage.read(any())).thenReturn(Mono.empty());
        when(nextStorage.read(any())).thenReturn(Mono.error(new RedisException("redis is down")));

        var chainStorage = new ChainStorage(currentStorage, nextStorage);
        var storageKey = new StorageKey("exchange", "key");

        var readResult = chainStorage.read(storageKey);

        StepVerifier.create(readResult).expectError(RedisException.class).verify();

        verify(currentStorage, times(1)).read(any());
        verify(nextStorage, times(1)).read(any());
    }
}
