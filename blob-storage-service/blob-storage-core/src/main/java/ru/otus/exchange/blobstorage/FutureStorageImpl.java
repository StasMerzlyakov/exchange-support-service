package ru.otus.exchange.blobstorage;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureStorageImpl implements FutureStorage {

    private final InternalSyncStorage syncStorage;

    private final ExecutorService executor;

    private final Duration waitTimeout;

    public FutureStorageImpl(Duration waitTimeout, InternalSyncStorage syncStorage) {
        this.syncStorage = syncStorage;
        this.executor = Executors.newCachedThreadPool();
        this.waitTimeout = waitTimeout;
    }

    @Override
    public Future<StorageData> readFuture(StorageKey storageKey) {
        return executor.submit(() -> {
            CountDownLatch downLatch = new CountDownLatch(2);

            Map<String, Object> threadResult = new ConcurrentHashMap<>();

            new Thread(() -> {
                        var metadata = syncStorage.readMetadata(storageKey);
                        if (metadata != null) {
                            threadResult.put(META_KEY, metadata);
                        }
                        downLatch.countDown();
                    })
                    .start();

            new Thread(() -> {
                        var byteBuffer = syncStorage.readObject(storageKey);
                        if (byteBuffer != null) {
                            threadResult.put(OBJECT_KEY, byteBuffer);
                        }
                        downLatch.countDown();
                    })
                    .start();

            if (!downLatch.await(waitTimeout.getSeconds(), TimeUnit.SECONDS)) {
                return null;
            }

            ByteBuffer byteBuffer = (ByteBuffer) threadResult.get(OBJECT_KEY);
            Metadata metadata = (Metadata) threadResult.get(META_KEY);
            if (metadata == null || byteBuffer == null) {
                return null;
            }

            return new StorageData(metadata, byteBuffer);
        });
    }

    @Override
    public Future<Metadata> readMetadataFuture(StorageKey storageKey) {
        return executor.submit(() -> {
            log.info("readMetadata by key {} start", storageKey);

            CountDownLatch downLatch = new CountDownLatch(1);
            Map<String, Object> threadResult = new ConcurrentHashMap<>();
            new Thread(() -> {
                        var metadata = syncStorage.readMetadata(storageKey);
                        if (metadata != null) {
                            threadResult.put(META_KEY, metadata);
                        }
                        downLatch.countDown();
                    })
                    .start();

            if (!downLatch.await(waitTimeout.getSeconds(), TimeUnit.SECONDS)) {
                return null;
            }
            var result = (Metadata) threadResult.get(META_KEY);
            log.info("readMetadata by key {} result {} ", storageKey, result != null);
            return result;
        });
    }

    @Override
    public Future<Boolean> writeFuture(StorageKey storageKey, StorageData storageData) {
        return executor.submit(() -> {
            CountDownLatch downLatch = new CountDownLatch(1);
            Map<String, Boolean> threadResult = new ConcurrentHashMap<>();

            new Thread(() -> {
                        var result = syncStorage.writeObject(storageKey, storageData);
                        threadResult.put(WRITE_RESULT_KEY, result);
                        downLatch.countDown();
                    })
                    .start();

            if (!downLatch.await(waitTimeout.getSeconds(), TimeUnit.SECONDS)) {
                return null;
            }
            return threadResult.get(WRITE_RESULT_KEY);
        });
    }

    @Override
    public Future<Boolean> deleteFuture(StorageKey storageKey) {
        return executor.submit(() -> {
            CountDownLatch downLatch = new CountDownLatch(2);
            Map<String, Boolean> threadResult = new ConcurrentHashMap<>();

            new Thread(() -> {
                        var result = syncStorage.removeMetadata(storageKey);
                        threadResult.put(DELETE_META_RESULT_KEY, result);
                        downLatch.countDown();
                    })
                    .start();

            new Thread(() -> {
                        var result = syncStorage.removeObject(storageKey);
                        threadResult.put(DELETE_OBJECT_RESULT_KEY, result);
                        downLatch.countDown();
                    })
                    .start();

            if (!downLatch.await(waitTimeout.getSeconds(), TimeUnit.SECONDS)) {
                return null;
            }

            return threadResult.get(DELETE_META_RESULT_KEY) && threadResult.get(DELETE_OBJECT_RESULT_KEY);
        });
    }

    @Override
    public Future<Boolean> deleteAllFuture(String exchange) {
        return executor.submit(() -> {
            List<StorageKey> storageKeyList = syncStorage.listExchange(exchange);
            if (storageKeyList == null) {
                return false;
            }
            var allFuturesResult = new AtomicBoolean(true);
            CountDownLatch downLatch = new CountDownLatch(storageKeyList.size());

            storageKeyList.forEach(storageKey -> new Thread(() -> {
                        try {
                            var result = deleteFuture(storageKey).get();
                            allFuturesResult.compareAndSet(true, result);
                        } catch (ExecutionException ex) {
                            allFuturesResult.set(false);
                        } catch (InterruptedException ie) {
                            log.warn("thread interrupted");
                            Thread.currentThread().interrupt();
                        } finally {
                            downLatch.countDown();
                        }
                    })
                    .start());

            if (!downLatch.await(waitTimeout.getSeconds(), TimeUnit.SECONDS)) {
                return false;
            }
            return allFuturesResult.get();
        });
    }

    private static final String OBJECT_KEY = "object";
    private static final String META_KEY = "metadata";
    private static final String WRITE_RESULT_KEY = "write_result";
    private static final String DELETE_META_RESULT_KEY = "delete_meta_result";
    private static final String DELETE_OBJECT_RESULT_KEY = "delete_object_result";
}
