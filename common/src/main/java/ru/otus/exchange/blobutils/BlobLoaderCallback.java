package ru.otus.exchange.blobutils;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface BlobLoaderCallback {
    ByteBuffer loadObject(String exchange, String name);
}
