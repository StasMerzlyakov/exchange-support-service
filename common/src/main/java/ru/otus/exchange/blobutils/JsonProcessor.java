package ru.otus.exchange.blobutils;

import java.io.IOException;

@FunctionalInterface
public interface JsonProcessor {
    byte[] restoreJson(String exchange, String fileName) throws IOException;
}
