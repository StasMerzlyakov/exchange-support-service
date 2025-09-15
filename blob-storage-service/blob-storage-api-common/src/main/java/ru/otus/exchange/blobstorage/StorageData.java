package ru.otus.exchange.blobstorage;

import java.nio.ByteBuffer;

public record StorageData(Metadata metadata, ByteBuffer byteBuffer) {}
