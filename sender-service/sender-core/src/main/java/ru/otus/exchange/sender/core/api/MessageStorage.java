package ru.otus.exchange.sender.core.api;

import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.sender.core.errors.MessageStorageException;

public interface MessageStorage {
    byte[] getFullMessage(StorageKey storageKey) throws MessageStorageException;
}
