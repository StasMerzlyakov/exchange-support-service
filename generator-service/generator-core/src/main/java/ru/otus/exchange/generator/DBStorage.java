package ru.otus.exchange.generator;

import java.util.UUID;
import ru.otus.exchange.generator.errors.DBException;

public interface DBStorage {
    UUID getOrStoreUUID(String messageID, UUID jsonID) throws DBException;
}
