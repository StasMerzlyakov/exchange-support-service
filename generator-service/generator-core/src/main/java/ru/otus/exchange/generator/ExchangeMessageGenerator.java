package ru.otus.exchange.generator;

import java.util.UUID;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.generator.errors.GeneratorException;

public interface ExchangeMessageGenerator {
    void generate(UUID jsonMessageID, StorageKey xmlStorageKey) throws GeneratorException;
}
