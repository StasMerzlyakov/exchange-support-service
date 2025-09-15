package ru.otus.exchange.blobstorage.exceptions;

public class MemoryException extends CommonStorageException {
    public MemoryException(String message) {
        super(message);
    }

    public MemoryException(Throwable throwable) {
        super(throwable);
    }
}
