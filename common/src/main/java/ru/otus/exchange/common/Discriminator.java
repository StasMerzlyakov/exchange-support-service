package ru.otus.exchange.common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Discriminator {
    EXCHANGE_MESSAGE("exchangeMessage");

    @JsonValue
    private final String value;

    Discriminator(String value) {
        this.value = value;
    }
}
