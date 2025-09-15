package ru.otus.exchange.sender.core;

import java.io.IOException;
import ru.otus.exchange.blobutils.JsonProcessor;
import ru.otus.exchange.sender.core.api.HttpSender;
import ru.otus.exchange.sender.core.api.MessageSender;
import ru.otus.exchange.sender.core.errors.MessageStorageException;

public class MessageSenderImpl implements MessageSender {

    private final JsonProcessor jsonProcessor;
    private final HttpSender httpSender;

    public MessageSenderImpl(JsonProcessor jsonProcessor, HttpSender httpSender) {
        this.jsonProcessor = jsonProcessor;
        this.httpSender = httpSender;
    }

    @Override
    public void send(String destination, String exchange, String name) {
        byte[] message;
        try {
            message = jsonProcessor.restoreJson(exchange, name);
        } catch (IOException e) {
            throw new MessageStorageException(e);
        }
        httpSender.send(destination, message);
    }
}
