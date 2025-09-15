package ru.otus.exchange.blobutils;

import java.io.IOException;

@FunctionalInterface
public interface BlobSaver {

    String NO_NAME = "no_name";

    String saveBlob(String exchange, String fileName, byte[] blob) throws IOException;

    default String saveBlob(String exchange, byte[] blob) throws IOException {
        return saveBlob(exchange, NO_NAME, blob);
    }
}
