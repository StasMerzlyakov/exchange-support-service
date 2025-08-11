package ru.otus.exchange.generator;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.generator.errors.GeneratorException;

@Slf4j
public class GeneratorImpl implements Generator {

    private final DBStorage dbStorage;
    private final DataStorage dataStorage;
    private final UUIDGenerator uuidGenerator;
    private final ExchangeMessageGenerator exchangeMessageGenerator;
    private final KafkaSender kafkaSender;

    public GeneratorImpl(
            DBStorage dbStorage,
            DataStorage dataStorage,
            UUIDGenerator uuidGenerator,
            ExchangeMessageGenerator exchangeMessageGenerator,
            KafkaSender kafkaSender) {
        this.dataStorage = dataStorage;
        this.dbStorage = dbStorage;
        this.uuidGenerator = uuidGenerator;
        this.exchangeMessageGenerator = exchangeMessageGenerator;
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void processMessage(KafkaMessage kafkaMessage) throws GeneratorException {

        String exchange = kafkaMessage.getExchange().toString();
        String messageID = kafkaMessage.getKey();

        StorageKey storageKey = new StorageKey(exchange, kafkaMessage.getKey());

        UUID jsonID = uuidGenerator.nextJsonUUID();

        UUID actualID = dbStorage.getOrStoreUUID(messageID, jsonID);

        StorageKey jsonStorageKey = new StorageKey(exchange, jsonID.toString());

        if (jsonID.equals(actualID) || !dataStorage.isExists(jsonStorageKey)) {
            // либо совсем новая генерация либо номер был выделен, но тело сохранить не успели
            exchangeMessageGenerator.generate(actualID, storageKey);
        }

        KafkaMessage jsonKafkaMessage =
                new KafkaMessage(kafkaMessage.getExchange(), jsonID.toString(), Discriminator.EXCHANGE_JSON);

        kafkaSender.send(jsonKafkaMessage);
    }
}
