package ru.otus.exchange.blobstorage.exceptions;

public class RedisException extends CommonStorageException {
    public RedisException(String message) {
        super(message);
    }
}
