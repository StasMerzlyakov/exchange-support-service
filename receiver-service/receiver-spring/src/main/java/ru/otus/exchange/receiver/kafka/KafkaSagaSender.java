package ru.otus.exchange.receiver.kafka;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import ru.otus.exchange.common.SagaMessage;
import ru.otus.exchange.receiver.SagaSender;
import ru.otus.exchange.receiver.conf.ReceiverProperties;
import ru.otus.exchange.receiver.errors.SenderException;

@Slf4j
public class KafkaSagaSender implements SagaSender {

    private final KafkaTemplate<UUID, SagaMessage> template;

    private final String nextTopic;
    private final long waitDurationMls;

    public KafkaSagaSender(ReceiverProperties receiverProperties, KafkaTemplate<UUID, SagaMessage> template) {
        this.nextTopic = receiverProperties.getNextTopic();
        this.waitDurationMls = receiverProperties.getKafkaWaitTimeout().toMillis();
        this.template = template;
    }

    @Override
    public void sent(SagaMessage message) throws SenderException {
        try {
            template.send(nextTopic, message.getExchange(), message).get(waitDurationMls, TimeUnit.MILLISECONDS);
            log.info("message {} sent success", message);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("send to kafka error", e);
            throw new SenderException(e);
        }
    }
}
