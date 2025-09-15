package ru.otus.exchange.blobstorage.grpc;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import ru.otus.exchange.blobstorage.Storage;
import ru.otus.exchange.blobstorage.api.grpc.v1.BlobStorageApiV1;
import ru.otus.exchange.blobstorage.api.grpc.v1.ReactorBlobStorageServiceGrpc;
import ru.otus.exchange.blobstorage.grpc.mapping.BlobStorageMapper;

@Slf4j
public class GRPCBlobStorageService extends ReactorBlobStorageServiceGrpc.BlobStorageServiceImplBase {

    private final Storage storage;
    private final BlobStorageMapper mapper;

    public GRPCBlobStorageService(Storage storage, BlobStorageMapper mapper) {
        this.mapper = mapper;
        this.storage = storage;
    }

    @Override
    public Mono<BlobStorageApiV1.StorageData> getObject(Mono<BlobStorageApiV1.StorageKey> request) {
        return request.map(mapper::map)
                .flatMap(storage::read)
                .map(mapper::map)
                .switchIfEmpty(
                        Mono.just(BlobStorageApiV1.StorageData.newBuilder().build()));
    }

    @Override
    public Mono<BlobStorageApiV1.OpResult> putObject(Mono<BlobStorageApiV1.PutObjectReq> request) {
        return request.flatMap(putObjectReq -> {
                    var storageData = mapper.map(putObjectReq.getStorageData());
                    log.info("storageData.metadata {}", storageData.metadata());
                    var storageKey = mapper.map(putObjectReq.getStorageKey());
                    log.info("storageKey {}", storageKey);
                    return storage.write(storageKey, storageData);
                })
                .map(mapper::map);
    }

    @Override
    public Mono<BlobStorageApiV1.OpResult> removeObject(Mono<BlobStorageApiV1.StorageKey> request) {
        return request.map(mapper::map).flatMap(storage::delete).map(mapper::map);
    }

    @Override
    public Mono<BlobStorageApiV1.Metadata> getMetadata(Mono<BlobStorageApiV1.StorageKey> request) {
        return request.map(mapper::map)
                .flatMap(storage::getMetadata)
                .map(mapper::map)
                .switchIfEmpty(Mono.just(BlobStorageApiV1.Metadata.newBuilder().build()));
    }

    @Override
    public Mono<BlobStorageApiV1.OpResult> removeObjects(Mono<BlobStorageApiV1.Exchange> request) {
        return request.map(mapper::map).flatMap(storage::deleteAll).map(mapper::map);
    }
}
