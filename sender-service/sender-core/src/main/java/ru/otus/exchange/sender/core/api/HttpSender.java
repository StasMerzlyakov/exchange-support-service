package ru.otus.exchange.sender.core.api;

import ru.otus.exchange.sender.core.errors.HttpException;

public interface HttpSender {
    void send(String endpoint, byte[] message) throws HttpException;
}
