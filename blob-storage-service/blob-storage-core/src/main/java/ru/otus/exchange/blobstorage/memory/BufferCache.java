package ru.otus.exchange.blobstorage.memory;

import java.nio.ByteBuffer;
import ru.otus.exchange.blobstorage.StorageKey;

public interface BufferCache extends MemoryCache<StorageKey, ByteBuffer> {}
