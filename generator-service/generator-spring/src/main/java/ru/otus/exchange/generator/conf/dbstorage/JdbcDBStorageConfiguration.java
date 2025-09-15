package ru.otus.exchange.generator.conf.dbstorage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.otus.exchange.generator.DBStorage;
import ru.otus.exchange.generator.db.JdbcDBStorage;

@Configuration
@ConditionalOnProperty(name = "dbStorageType", havingValue = "jdbc")
public class JdbcDBStorageConfiguration {

    @Bean
    public DBStorage dbStorage(JdbcTemplate jdbcTemplate) {
        return new JdbcDBStorage(jdbcTemplate);
    }
}
