package ru.otus.exchange.sender.core.errors;

public class SenderException extends RuntimeException {
    public SenderException(String message) {
        super(message);
    }

    public SenderException(Throwable cause) {
        super(cause);
    }
}
