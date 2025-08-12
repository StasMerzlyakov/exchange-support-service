package ru.otus.exchange.sender.conf;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sender")
public class SenderProperties {
    String fromTopic;
    String blobStorageHost;
    int blobStoragePort;
    String lockPrefix;
    Duration lockDuration;
    int sendLimit;
    Duration cacheRefreshTimeout;
    int maxConnTotal;
    int maxConnPerRoute;
    long connectRequestTimeoutMls;
    long keepAliveMls;
}
