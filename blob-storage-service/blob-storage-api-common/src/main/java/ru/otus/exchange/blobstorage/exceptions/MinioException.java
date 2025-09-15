package ru.otus.exchange.blobstorage.exceptions;

public class MinioException extends CommonStorageException {
    public MinioException(String message) {
        super(message);
    }

    public MinioException(Throwable throwable) {
        super(throwable);
    }
}
