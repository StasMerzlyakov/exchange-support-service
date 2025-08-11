package ru.otus.exchange.generator.conf;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "generator")
public class GeneratorProperties {
    Duration kafkaWaitTimeout;
    String fromTopic;
    String nextTopic;
    String blobStorageHost;
    int blobStoragePort;
}
