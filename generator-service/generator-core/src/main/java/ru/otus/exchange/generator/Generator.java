package ru.otus.exchange.generator;

import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.generator.errors.GeneratorException;

public interface Generator {
    void processMessage(KafkaMessage kafkaMessage) throws GeneratorException;
}
