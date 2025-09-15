package ru.otus.exchange.generator.errors;

public class GeneratorException extends RuntimeException {

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(Throwable throwable) {
        super(throwable);
    }
}
