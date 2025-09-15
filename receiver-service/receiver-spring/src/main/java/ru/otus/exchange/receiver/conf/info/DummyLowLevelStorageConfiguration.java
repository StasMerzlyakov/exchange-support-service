package ru.otus.exchange.receiver.conf.info;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.info.LowLeverStorage;

@Configuration
@ConditionalOnProperty(name = "infoStorageType", havingValue = "dummy")
@SuppressWarnings("java:S1186")
public class DummyLowLevelStorageConfiguration {

    @Bean
    public LowLeverStorage dummyStorage() {
        return new LowLeverStorage() {
            @Override
            public boolean isAcceptable(String code) {
                return true;
            }

            @Override
            public void insertIgnore(MessageInfo messageInfo) {}

            @Override
            public String getProcessGUID(String processGUID, String messageID) {
                return processGUID;
            }

            @Override
            public String findDiscriminator(String code) {
                return "";
            }
        };
    }
}
