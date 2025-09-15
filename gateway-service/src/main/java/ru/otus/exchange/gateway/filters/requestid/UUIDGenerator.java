package ru.otus.exchange.gateway.filters.requestid;

import java.util.UUID;

public interface UUIDGenerator {
    UUID next();
}
