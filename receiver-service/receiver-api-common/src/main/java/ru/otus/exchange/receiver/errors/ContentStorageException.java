package ru.otus.exchange.receiver.errors;

public class ContentStorageException extends ReceiverException {
    public ContentStorageException(String message) {
        super(message);
    }

    public ContentStorageException(Throwable throwable) {
        super(throwable);
    }
}
