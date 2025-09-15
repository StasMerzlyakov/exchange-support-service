package ru.otus.exchange.blobstorage.config.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.memory.BufferCache;
import ru.otus.exchange.blobstorage.memory.bytearray.ByteArrayCache;
import ru.otus.exchange.blobstorage.memory.bytearray.ByteArrayCacheBufferCacheAdapter;

@ConditionalOnProperty(name = "memoryStorageType", havingValue = "byteArray")
@Configuration
public class ByteArrayStorageConfiguration {

    @Bean
    public ByteArrayCache byteArrayCache() {
        return new ByteArrayCache();
    }

    @Bean
    public BufferCache bufferCache(ByteArrayCache byteArrayCache) {
        return new ByteArrayCacheBufferCacheAdapter(byteArrayCache);
    }
}
