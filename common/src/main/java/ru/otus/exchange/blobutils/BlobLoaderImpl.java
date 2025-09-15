package ru.otus.exchange.blobutils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BlobLoaderImpl implements BlobLoader {

    private final BlobLoaderCallback blobLoaderCallback;

    public BlobLoaderImpl(BlobLoaderCallback blobLoaderCallback) {
        this.blobLoaderCallback = blobLoaderCallback;
    }

    @Override
    public byte[] loadBlob(String exchange, String fileName) throws IOException {
        ByteBuffer byteBuffer = blobLoaderCallback.loadObject(exchange, fileName);
        return Archiver.decompress(byteBuffer.array());
    }
}
