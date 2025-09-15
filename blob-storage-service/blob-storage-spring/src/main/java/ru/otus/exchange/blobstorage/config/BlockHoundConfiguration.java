package ru.otus.exchange.blobstorage.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import reactor.blockhound.BlockHound;

@Configuration
@ConditionalOnProperty(name = "blockHound", havingValue = "on")
public class BlockHoundConfiguration {

    @PostConstruct
    void init() {
        BlockHound.install();
    }
}
