package ru.otus.exchange.sender.lock;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import ru.otus.exchange.sender.core.api.DistributedLock;

@Slf4j
public class RedisDistributedLock implements DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean acquireLock(String lockKey, Duration duration) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", duration));
    }

    @Override
    public void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
