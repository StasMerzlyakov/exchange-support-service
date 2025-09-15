package ru.otus.exchange.generator.errors;

public class BlobStorageException extends GeneratorException {
    public BlobStorageException(String message) {
        super(message);
    }

    public BlobStorageException(Throwable throwable) {
        super(throwable);
    }
}
