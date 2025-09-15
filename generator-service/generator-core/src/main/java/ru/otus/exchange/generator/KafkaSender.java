package ru.otus.exchange.generator;

import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.generator.errors.KafkaException;

public interface KafkaSender {
    void send(KafkaMessage message) throws KafkaException;
}
