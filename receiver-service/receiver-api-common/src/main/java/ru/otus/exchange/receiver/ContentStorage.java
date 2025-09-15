package ru.otus.exchange.receiver;

import java.util.UUID;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.errors.ContentStorageException;

public interface ContentStorage {
    void storeMessage(UUID processGUID, MessageInfo messageInfo, byte[] content) throws ContentStorageException;
}
