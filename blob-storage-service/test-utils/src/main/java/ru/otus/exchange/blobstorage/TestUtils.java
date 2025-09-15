package ru.otus.exchange.blobstorage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;

public class TestUtils {
    private TestUtils() {}

    @SneakyThrows
    public static StorageData createObject() {
        int size = 2000;

        byte[] byteArray = RandomStringUtils.secureStrong()
                .nextAlphabetic(size)
                .toUpperCase()
                .getBytes(StandardCharsets.UTF_8);
        String sha256Digest = Utils.hexDigest(byteArray);

        return new StorageData(new Metadata(size, sha256Digest), ByteBuffer.wrap(byteArray));
    }

    public static Metadata createMetadata() {
        int size = 2000;
        byte[] byteArray = new byte[size];
        SecureRandom random = new SecureRandom();
        random.nextBytes(byteArray);

        String sha256Digest = Utils.hexDigest(byteArray);
        return new Metadata(size, sha256Digest);
    }
}
