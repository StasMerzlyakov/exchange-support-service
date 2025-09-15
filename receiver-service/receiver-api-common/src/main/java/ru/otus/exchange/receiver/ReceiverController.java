package ru.otus.exchange.receiver;

import ru.otus.exchange.receiver.errors.ReceiverException;

public interface ReceiverController {
    void process(String requestID, byte[] message) throws ReceiverException;
}
