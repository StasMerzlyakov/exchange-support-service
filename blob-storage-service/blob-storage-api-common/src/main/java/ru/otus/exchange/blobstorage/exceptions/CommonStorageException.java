package ru.otus.exchange.blobstorage.exceptions;

public class CommonStorageException extends RuntimeException {

    public CommonStorageException(String message) {
        super(message);
    }

    public CommonStorageException(Throwable throwable) {
        super(throwable);
    }
}
