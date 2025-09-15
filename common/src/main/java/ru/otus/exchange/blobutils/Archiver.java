package ru.otus.exchange.blobutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Archiver {

    private Archiver() {}

    //  1024..1000000(~1Mb)
    private static final Integer BUFFER_SIZE = 1024;

    // 1 - faster, 9 - denser
    private static final Integer COMPRESSION_LEVEL = 5;

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bArray = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];
        ZipOutputStream out = new ZipOutputStream(bArray);
        out.setLevel(COMPRESSION_LEVEL);
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        out.putNextEntry(new ZipEntry("entry"));
        int len;
        while ((len = bis.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        out.closeEntry();
        bis.close();
        out.close();
        return bArray.toByteArray();
    }

    public static byte[] decompress(byte[] archive) throws IOException {
        ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(archive));
        in.getNextEntry();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];

        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        byte[] result = out.toByteArray();
        out.close();
        in.close();
        return result;
    }
}
