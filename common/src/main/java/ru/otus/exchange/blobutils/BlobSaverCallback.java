package ru.otus.exchange.blobutils;

@FunctionalInterface
public interface BlobSaverCallback {
    void saveObject(String exchange, String fileName, byte[] object);
}
