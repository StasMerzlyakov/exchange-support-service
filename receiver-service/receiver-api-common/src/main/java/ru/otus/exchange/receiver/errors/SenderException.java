package ru.otus.exchange.receiver.errors;

public class SenderException extends ReceiverException {
    public SenderException(String message) {
        super(message);
    }

    public SenderException(Throwable throwable) {
        super(throwable);
    }
}
