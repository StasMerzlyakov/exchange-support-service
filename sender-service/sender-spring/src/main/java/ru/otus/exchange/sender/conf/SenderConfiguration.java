package ru.otus.exchange.sender.conf;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.otus.exchange.blobutils.*;
import ru.otus.exchange.sender.AsyncMessageUploaderStarter;
import ru.otus.exchange.sender.core.MessageSenderImpl;
import ru.otus.exchange.sender.core.MessageUploader;
import ru.otus.exchange.sender.core.MessageUploaderImpl;
import ru.otus.exchange.sender.core.api.DBStorage;
import ru.otus.exchange.sender.core.api.DistributedLock;
import ru.otus.exchange.sender.core.api.HttpSender;
import ru.otus.exchange.sender.core.api.MessageSender;

@Configuration
@EnableCaching
@EnableAsync
@EnableKafka
@EnableScheduling
public class SenderConfiguration {

    @Bean
    public MessageUploader messageUploader(
            DBStorage dbStorage,
            MessageSender messageSender,
            DistributedLock distributedLock,
            SenderProperties properties) {
        var uploader = new MessageUploaderImpl(
                dbStorage, messageSender, distributedLock, properties.lockDuration, properties.lockPrefix);
        return new AsyncMessageUploaderStarter(uploader);
    }

    @Bean
    public MessageSender messageSender(JsonProcessor jsonProcessor, HttpSender httpSender) {
        return new MessageSenderImpl(jsonProcessor, httpSender);
    }
}
