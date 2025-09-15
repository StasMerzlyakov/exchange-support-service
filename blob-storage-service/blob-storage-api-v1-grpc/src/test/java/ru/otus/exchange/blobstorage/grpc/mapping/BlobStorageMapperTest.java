package ru.otus.exchange.blobstorage.grpc.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.otus.exchange.blobstorage.Metadata;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.TestUtils;

class BlobStorageMapperTest {

    @Test
    void test1() {
        BlobStorageMapper mapper = Mappers.getMapper(BlobStorageMapper.class);

        StorageData storageData = TestUtils.createObject();

        var grpcStorageData = mapper.map(storageData);

        Assertions.assertNotNull(grpcStorageData);

        var metadata = grpcStorageData.getMetadata();
        Assertions.assertNotNull(metadata);

        Assertions.assertEquals(storageData.metadata().size(), metadata.getSize());
        Assertions.assertEquals(storageData.metadata().sha256Digest(), metadata.getSha256Digest());

        Assertions.assertEquals(
                storageData.metadata().size(), storageData.byteBuffer().remaining());
        Assertions.assertEquals(
                storageData.metadata().size(), grpcStorageData.getData().size());
    }

    @Test
    void test2() {
        BlobStorageMapper mapper = Mappers.getMapper(BlobStorageMapper.class);

        Metadata metadata = TestUtils.createMetadata();
        var grpcMetadata = mapper.map(metadata);

        Assertions.assertNotNull(grpcMetadata);

        Assertions.assertEquals(metadata.size(), grpcMetadata.getSize());
        Assertions.assertEquals(metadata.sha256Digest(), grpcMetadata.getSha256Digest());
    }
}
