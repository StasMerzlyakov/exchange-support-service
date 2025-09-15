package ru.otus.exchange.sender.core.api;

import java.time.Duration;

public interface DistributedLock {
    boolean acquireLock(String lockKey, Duration duration);

    void releaseLock(String lockKey);
}
