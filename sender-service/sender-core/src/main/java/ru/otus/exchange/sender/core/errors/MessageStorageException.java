package ru.otus.exchange.sender.core.errors;

public class MessageStorageException extends SenderException {
    public MessageStorageException(String message) {
        super(message);
    }

    public MessageStorageException(Throwable throwable) {
        super(throwable);
    }
}
