package ru.otus.exchange.sender.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.otus.exchange.sender.core.api.DBStorage;
import ru.otus.exchange.sender.dbstorage.DBStorageImpl;

@Configuration
public class DBStorageConfiguration {

    @Bean
    public DBStorage dbStorage(JdbcTemplate jdbcTemplate, SenderProperties senderProperties) {
        return new DBStorageImpl(jdbcTemplate, senderProperties.sendLimit);
    }
}
