package ru.otus.exchange.generator.kafka;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.generator.KafkaSender;
import ru.otus.exchange.generator.conf.GeneratorProperties;
import ru.otus.exchange.generator.errors.KafkaException;

@Slf4j
public class KafkaSenderImpl implements KafkaSender {

    private final KafkaTemplate<UUID, KafkaMessage> template;

    private final String nextTopic;
    private final long waitDurationMls;

    public KafkaSenderImpl(GeneratorProperties generatorProperties, KafkaTemplate<UUID, KafkaMessage> template) {
        this.nextTopic = generatorProperties.getNextTopic();
        this.waitDurationMls = generatorProperties.getKafkaWaitTimeout().toMillis();
        this.template = template;
    }

    @Override
    public void send(KafkaMessage message) throws KafkaException {
        try {
            template.send(nextTopic, message.getExchange(), message).get(waitDurationMls, TimeUnit.MILLISECONDS);
            log.info("message {} sent success", message);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("send to kafka error", e);
            throw new KafkaException(e);
        }
    }
}
