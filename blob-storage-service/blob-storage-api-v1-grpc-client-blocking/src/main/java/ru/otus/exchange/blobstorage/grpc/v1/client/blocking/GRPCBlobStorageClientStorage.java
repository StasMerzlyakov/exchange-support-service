package ru.otus.exchange.blobstorage.grpc.v1.client.blocking;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobstorage.Metadata;
import ru.otus.exchange.blobstorage.StorageData;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobstorage.api.grpc.v1.BlobStorageServiceGrpc;

@Slf4j
public class GRPCBlobStorageClientStorage implements StorageSync {

    private final BlobStorageServiceGrpc.BlobStorageServiceBlockingStub blobStorageStub;

    private final GRPCBlobStorageClientMapper clientMapper;

    public GRPCBlobStorageClientStorage(ManagedChannel channel, GRPCBlobStorageClientMapper clientMapper) {
        blobStorageStub = BlobStorageServiceGrpc.newBlockingStub(channel);
        this.clientMapper = clientMapper;
    }

    @Override
    public StorageData read(StorageKey storageKey) {
        var storageKeyResp = clientMapper.map(storageKey);
        var storageDataResp = blobStorageStub.getObject(storageKeyResp);
        if (storageDataResp.getData().isEmpty()) {
            return null;
        }
        return clientMapper.map(storageDataResp);
    }

    @Override
    public Boolean write(StorageKey storageKey, StorageData storageData) {
        var putObjectReq = clientMapper.map(storageKey, storageData);
        var resultResp = blobStorageStub.putObject(putObjectReq);
        return clientMapper.map(resultResp);
    }

    @Override
    public Boolean delete(StorageKey storageKey) {
        var storageKeyReq = clientMapper.map(storageKey);
        var resultResp = blobStorageStub.removeObject(storageKeyReq);
        return clientMapper.map(resultResp);
    }

    @Override
    public Boolean deleteAll(String exchange) {
        var exchangeReq = clientMapper.map(exchange);
        var resultResp = blobStorageStub.removeObjects(exchangeReq);
        return clientMapper.map(resultResp);
    }

    @Override
    public Metadata getMetadata(StorageKey storageKey) {
        var storageKeyReq = clientMapper.map(storageKey);
        var metadataResp = blobStorageStub.getMetadata(storageKeyReq);
        if (metadataResp.getSize() == 0) {
            return null;
        }
        return clientMapper.map(metadataResp);
    }
}
