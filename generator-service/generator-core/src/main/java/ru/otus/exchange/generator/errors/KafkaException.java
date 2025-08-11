package ru.otus.exchange.generator.errors;

public class KafkaException extends GeneratorException {
    public KafkaException(String message) {
        super(message);
    }

    public KafkaException(Throwable throwable) {
        super(throwable);
    }
}
