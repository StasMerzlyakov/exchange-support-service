package ru.otus.exchange.receiver.errors;

public class NotAcceptableException extends ReceiverException {
    public NotAcceptableException(String message) {
        super(message);
    }

    public NotAcceptableException(Throwable throwable) {
        super(throwable);
    }
}
