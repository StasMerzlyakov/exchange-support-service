package ru.otus.exchange.receiver.core;

import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.errors.MessageFormatException;

public interface MessageInfoExtractor {
    MessageInfo parse(String requestID, byte[] message) throws MessageFormatException;
}
