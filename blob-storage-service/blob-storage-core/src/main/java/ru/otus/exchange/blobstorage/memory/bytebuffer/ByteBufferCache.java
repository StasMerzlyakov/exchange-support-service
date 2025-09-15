package ru.otus.exchange.blobstorage.memory.bytebuffer;

import java.nio.ByteBuffer;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.blobstorage.memory.BufferCache;
import ru.otus.exchange.blobstorage.memory.GenericCache;

public class ByteBufferCache extends GenericCache<StorageKey, ByteBuffer> implements BufferCache {}
