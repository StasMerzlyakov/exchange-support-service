package ru.otus.exchange.blobstorage.config.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.memory.BufferCache;
import ru.otus.exchange.blobstorage.memory.bytebuffer.ByteBufferCache;

@ConditionalOnProperty(name = "memoryStorageType", havingValue = "byteBuffer")
@Configuration
public class ByteBufferStorageConfiguration {

    @Bean
    public BufferCache bufferCache() {
        return new ByteBufferCache();
    }
}
