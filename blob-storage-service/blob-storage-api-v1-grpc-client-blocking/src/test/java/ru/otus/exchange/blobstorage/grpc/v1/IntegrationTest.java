package ru.otus.exchange.blobstorage.grpc.v1;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.TestUtils;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientMapper;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientStorage;

@Disabled("требует развернутого приложения и правильней вынести в отдельный модуль, но пока здесь")
@Slf4j
class IntegrationTest {

    ManagedChannel channel =
            ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

    @Test
    @DisplayName("read + write + delete")
    void test1() {
        var mapper = Mappers.getMapper(GRPCBlobStorageClientMapper.class);
        var clientStorage = new GRPCBlobStorageClientStorage(channel, mapper);

        var storageKey = new StorageKey("exchange", "key");

        Assertions.assertDoesNotThrow(() -> {
            clientStorage.delete(storageKey);
        });

        Assertions.assertNull(clientStorage.read(storageKey));
        Assertions.assertNull(clientStorage.getMetadata(storageKey));

        var object = TestUtils.createObject();

        Assertions.assertTrue(clientStorage.write(storageKey, object));

        AtomicReference<StorageData> actualObjectRef = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            actualObjectRef.set(clientStorage.read(storageKey));
        });

        var actualObject = actualObjectRef.get();

        Assertions.assertEquals(object.metadata(), actualObject.metadata());
        Assertions.assertArrayEquals(
                object.byteBuffer().array(), actualObject.byteBuffer().array());

        Assertions.assertEquals(object.metadata(), clientStorage.getMetadata(storageKey));
        clientStorage.delete(storageKey);

        Assertions.assertNull(clientStorage.read(storageKey));
        Assertions.assertNull(clientStorage.getMetadata(storageKey));
    }

    @Test
    @DisplayName("read + write + deleteAll")
    void test2() {
        var mapper = Mappers.getMapper(GRPCBlobStorageClientMapper.class);
        var clientStorage = new GRPCBlobStorageClientStorage(channel, mapper);

        var storageKey = new StorageKey("exchange", "key");

        Assertions.assertDoesNotThrow(() -> {
            clientStorage.deleteAll(storageKey.exchange());
        });

        Assertions.assertNull(clientStorage.read(storageKey));
        Assertions.assertNull(clientStorage.getMetadata(storageKey));

        var object = TestUtils.createObject();

        Assertions.assertTrue(clientStorage.write(storageKey, object));

        AtomicReference<StorageData> actualObjectRef = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            actualObjectRef.set(clientStorage.read(storageKey));
        });

        var actualObject = actualObjectRef.get();

        Assertions.assertEquals(object.metadata(), actualObject.metadata());
        Assertions.assertArrayEquals(
                object.byteBuffer().array(), actualObject.byteBuffer().array());

        Assertions.assertEquals(object.metadata(), clientStorage.getMetadata(storageKey));
        clientStorage.deleteAll(storageKey.exchange());

        Assertions.assertNull(clientStorage.read(storageKey));
        Assertions.assertNull(clientStorage.getMetadata(storageKey));
    }
}
