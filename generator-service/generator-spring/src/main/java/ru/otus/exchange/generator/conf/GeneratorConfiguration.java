package ru.otus.exchange.generator.conf;

import jakarta.xml.bind.JAXBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.generator.*;

@Configuration
public class GeneratorConfiguration {

    @Bean
    public DataStorage dataStorage(StorageSync storageSync) {
        return new SyncStorageAdapter(storageSync);
    }

    @Bean
    public UUIDGenerator uuidGenerator() {
        return new UUIDGeneratorImpl();
    }

    @Bean
    public Generator generator(
            DBStorage dbStorage,
            DataStorage dataStorage,
            UUIDGenerator uuidGenerator,
            ExchangeMessageGenerator exchangeMessageGenerator,
            KafkaSender kafkaSender) {
        return new GeneratorImpl(dbStorage, dataStorage, uuidGenerator, exchangeMessageGenerator, kafkaSender);
    }

    @Bean
    public ExchangeMessageGenerator exchangeMessageGenerator(DataStorage dataStorage) throws JAXBException {
        return new ExchangeMessageGeneratorImpl(dataStorage);
    }
}
