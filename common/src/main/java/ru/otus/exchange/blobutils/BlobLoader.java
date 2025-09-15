package ru.otus.exchange.blobutils;

import java.io.IOException;

@FunctionalInterface
public interface BlobLoader {
    byte[] loadBlob(String exchange, String fileName) throws IOException;
}
