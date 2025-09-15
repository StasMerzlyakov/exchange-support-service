package ru.otus.exchange.blobutils;

import java.io.IOException;
import java.util.Set;

@FunctionalInterface
public interface XmlProcessor {
    void storeXml(String exchange, byte[] xml, Set<String> blobPaths, String fileName) throws IOException;
}
