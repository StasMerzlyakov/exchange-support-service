package ru.otus.exchange.blobutils;

import com.github.eprst.murmur3.MurmurHash3;
import java.io.IOException;

public class BlobSaverImpl implements BlobSaver {

    private final BlobSaverCallback blobSaverCallback;

    public BlobSaverImpl(BlobSaverCallback blobSaverCallback) {
        this.blobSaverCallback = blobSaverCallback;
    }

    @Override
    public String saveBlob(String exchange, String fileName, byte[] blob) throws IOException {
        if (fileName == null || fileName.isEmpty() || NO_NAME.equals(fileName)) {
            var result = MurmurHash3.murmurhash3_x86_32(blob, 0, blob.length, 0);
            fileName = String.format("%04x", result);
        }

        byte[] arch = Archiver.compress(blob);
        blobSaverCallback.saveObject(exchange, fileName, arch);
        return fileName;
    }
}
