package ru.otus.exchange.receiver.conf.info;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.otus.exchange.receiver.info.JdbcLowLevelStorage;
import ru.otus.exchange.receiver.info.LowLeverStorage;
import ru.otus.exchange.receiver.info.jdbc.JdbcOperations;
import ru.otus.exchange.receiver.info.jdbc.JdbcOperationsImpl;

@Configuration
@ConditionalOnProperty(name = "infoStorageType", havingValue = "jdbc")
@EnableScheduling
@EnableCaching
public class JdbcLowLevelStorageConfiguration {

    @Bean
    public JdbcOperations jdbcOperations(JdbcTemplate jdbcTemplate) {
        return new JdbcOperationsImpl(jdbcTemplate);
    }

    @Bean
    public LowLeverStorage lowLevelStorage(JdbcOperations jdbcOperations) {
        return new JdbcLowLevelStorage(jdbcOperations);
    }
}
