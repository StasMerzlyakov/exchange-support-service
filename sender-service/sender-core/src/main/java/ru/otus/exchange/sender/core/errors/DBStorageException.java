package ru.otus.exchange.sender.core.errors;

public class DBStorageException extends SenderException {
    public DBStorageException(String message) {
        super(message);
    }

    public DBStorageException(Throwable throwable) {
        super(throwable);
    }
}
