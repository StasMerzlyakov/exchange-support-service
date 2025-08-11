package ru.otus.exchange.generator;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.generator.errors.GeneratorException;

@Slf4j
@Service
public class KafkaConsumer {

    private final Generator generator;

    public KafkaConsumer(Generator generator) {
        this.generator = generator;
    }

    @KafkaListener(topics = "${generator.from-topic}")
    void processMessage(ConsumerRecord<UUID, KafkaMessage> message, Acknowledgment acknowledgment) {
        try {
            log.info("start processing {}", message);
            generator.processMessage(message.value());
            acknowledgment.acknowledge();
        } catch (GeneratorException e) {
            log.error("processMessage error", e);
        }
    }
}
