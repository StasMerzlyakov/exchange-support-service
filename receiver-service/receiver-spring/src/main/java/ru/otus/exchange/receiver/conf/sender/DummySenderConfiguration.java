package ru.otus.exchange.receiver.conf.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.receiver.SagaSender;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "kafkaSenderType", havingValue = "dummy")
public class DummySenderConfiguration {

    @Bean
    public SagaSender dummySender() {
        return message -> log.info("message {} sent success", message);
    }
}
