package ru.otus.exchange.blobstorage;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ChainStorage implements Storage {

    private final Storage current;
    public final Storage next;

    public ChainStorage(Storage current, Storage next) {
        this.current = current;
        this.next = next;
    }

    @Override
    public Mono<StorageData> read(StorageKey storageKey) {
        return current.read(storageKey)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("restore cache {}", storageKey);
                    return next.read(storageKey).doOnNext(storageData -> current.write(storageKey, storageData));
                }))
                .doOnError(Mono::error);
    }

    @Override
    public Mono<Boolean> write(StorageKey storageKey, StorageData storageData) {
        return next.write(storageKey, storageData).doOnSuccess(result -> current.write(storageKey, storageData));
    }

    @Override
    public Mono<Boolean> delete(StorageKey storageKey) {
        return current.delete(storageKey).flatMap(b -> next.delete(storageKey));
    }

    @Override
    public Mono<Boolean> deleteAll(String exchange) {
        return current.deleteAll(exchange).flatMap(b -> next.deleteAll(exchange));
    }

    @Override
    public Mono<Metadata> getMetadata(StorageKey storageKey) {
        return current.getMetadata(storageKey)
                .switchIfEmpty(Mono.defer(() -> {
                            log.info("readStorageKey {}", storageKey);
                            return next.read(storageKey);
                        })
                        .filter(Objects::nonNull)
                        .flatMap(storageData -> {
                            log.info("store object to cache");
                            return current.write(storageKey, storageData);
                        })
                        .flatMap(result -> {
                            log.info("restore object result: {}", result);
                            if (Boolean.TRUE.equals(result)) {
                                return current.getMetadata(storageKey);
                            }
                            return null;
                        })
                        .switchIfEmpty(Mono.empty()))
                .switchIfEmpty(Mono.empty())
                .doOnSuccess(metadata -> log.info("getMetadata by key result {} : {} ", storageKey, metadata != null));
    }
}
