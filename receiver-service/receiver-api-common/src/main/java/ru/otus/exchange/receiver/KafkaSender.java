package ru.otus.exchange.receiver;

import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.receiver.errors.SenderException;

public interface KafkaSender {
    void send(KafkaMessage message) throws SenderException;
}
