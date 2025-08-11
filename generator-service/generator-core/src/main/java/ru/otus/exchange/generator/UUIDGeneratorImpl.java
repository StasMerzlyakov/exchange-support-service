package ru.otus.exchange.generator;

import java.util.UUID;

public class UUIDGeneratorImpl implements UUIDGenerator {
    @Override
    public UUID nextJsonUUID() {
        return UUID.randomUUID();
    }
}
