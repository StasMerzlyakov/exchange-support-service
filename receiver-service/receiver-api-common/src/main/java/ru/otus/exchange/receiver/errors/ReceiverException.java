package ru.otus.exchange.receiver.errors;

public class ReceiverException extends RuntimeException {

    public ReceiverException(String message) {
        super(message);
    }

    public ReceiverException(Throwable throwable) {
        super(throwable);
    }
}
