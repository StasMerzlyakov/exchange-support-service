package ru.otus.exchange.receiver.errors;

public class MessageFormatException extends ReceiverException {
    public MessageFormatException(String message) {
        super(message);
    }

    public MessageFormatException(Throwable throwable) {
        super(throwable);
    }
}
