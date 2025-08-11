package ru.otus.exchange.generator.conf.dbstorage;

import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.generator.DBStorage;
import ru.otus.exchange.generator.errors.DBException;

@Configuration
@ConditionalOnProperty(name = "dbStorageType", havingValue = "dummy")
@SuppressWarnings("java:S1186")
public class DummyDBStorageConfiguration {

    @Bean
    public DBStorage dummyStorage() {
        return new DBStorage() {

            @Override
            public UUID getOrStoreUUID(String messageID, UUID jsonID) throws DBException {
                return jsonID;
            }
        };
    }
}
