package ru.otus.exchange.generator.errors;

public class DBException extends GeneratorException {
    public DBException(String message) {
        super(message);
    }

    public DBException(Throwable throwable) {
        super(throwable);
    }
}
