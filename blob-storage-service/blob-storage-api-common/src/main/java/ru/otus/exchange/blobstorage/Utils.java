package ru.otus.exchange.blobstorage;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.SneakyThrows;

public class Utils {
    private Utils() {}

    @SneakyThrows
    public static String hexDigest(byte[] byteArray) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(byteArray);

        HexFormat hex = HexFormat.of();
        return hex.formatHex(encodedHash);
    }

    @SneakyThrows
    public static StorageData createStorageData(byte[] byteArray) {
        String hexDigest = hexDigest(byteArray);
        Metadata metadata = new Metadata(byteArray.length, hexDigest);
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        return new StorageData(metadata, byteBuffer);
    }
}
