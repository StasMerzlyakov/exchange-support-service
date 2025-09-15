package ru.otus.exchange.blobstorage.grpc.mapping;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.exchange.blobstorage.Metadata;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.api.grpc.v1.BlobStorageApiV1;

@Mapper(componentModel = "spring")
public interface BlobStorageMapper {
    default ByteString map(ByteBuffer value) {
        return ByteString.copyFrom(value.array());
    }

    default ByteBuffer map(ByteString value) {
        return ByteBuffer.wrap(value.toByteArray());
    }

    @Mapping(target = "size", source = "metadata.size")
    @Mapping(target = "sha256Digest", source = "metadata.sha256Digest")
    BlobStorageApiV1.Metadata map(Metadata metadata);

    @Mapping(target = "size", source = "metadata.size")
    @Mapping(target = "sha256Digest", source = "metadata.sha256Digest")
    Metadata map(BlobStorageApiV1.Metadata metadata);

    default Boolean map(BlobStorageApiV1.OpResult req) {
        return req.getResult();
    }

    default BlobStorageApiV1.OpResult map(Boolean value) {
        return BlobStorageApiV1.OpResult.newBuilder().setResult(value).build();
    }

    default BlobStorageApiV1.StorageData map(StorageData storageData) {
        BlobStorageApiV1.Metadata metadata = map(storageData.metadata());
        ByteString data = map(storageData.byteBuffer());
        return BlobStorageApiV1.StorageData.newBuilder()
                .setMetadata(metadata)
                .setData(data)
                .build();
    }

    default StorageData map(BlobStorageApiV1.StorageData storageData) {
        Metadata metadata = map(storageData.getMetadata());
        ByteBuffer byteBuffer = map(storageData.getData());
        return new StorageData(metadata, byteBuffer);
    }

    @Mapping(target = "key", source = "key")
    @Mapping(target = "exchange", source = "exchange")
    StorageKey map(BlobStorageApiV1.StorageKey request);

    default String map(BlobStorageApiV1.Exchange exchange) {
        return exchange.getExchange();
    }

    default BlobStorageApiV1.Exchange map(String exchange) {
        return BlobStorageApiV1.Exchange.newBuilder().setExchange(exchange).build();
    }
}
