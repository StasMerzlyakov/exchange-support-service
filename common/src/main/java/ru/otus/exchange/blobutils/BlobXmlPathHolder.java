package ru.otus.exchange.blobutils;

import java.util.Set;

@FunctionalInterface
public interface BlobXmlPathHolder {
    Set<String> getBlobPath(String messageType);
}
