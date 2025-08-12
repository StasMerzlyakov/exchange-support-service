package ru.otus.exchange.sender.core.errors;

public class HttpException extends SenderException {
    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }
}
