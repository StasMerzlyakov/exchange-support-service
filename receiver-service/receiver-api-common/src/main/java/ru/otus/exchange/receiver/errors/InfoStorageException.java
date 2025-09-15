package ru.otus.exchange.receiver.errors;

public class InfoStorageException extends ReceiverException {
    public InfoStorageException(String message) {
        super(message);
    }

    public InfoStorageException(Throwable throwable) {
        super(throwable);
    }
}
