package ru.otus.exchange.sender.core.api;

import ru.otus.exchange.sender.core.errors.SenderException;

public interface MessageSender {
    void send(String destination, String exchange, String name) throws SenderException;
}
