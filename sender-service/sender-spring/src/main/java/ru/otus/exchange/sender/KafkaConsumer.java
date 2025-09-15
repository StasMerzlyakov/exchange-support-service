package ru.otus.exchange.sender;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.sender.core.MessageUploader;
import ru.otus.exchange.sender.core.errors.SenderException;

@Slf4j
@Service
public class KafkaConsumer {

    private final MessageUploader uploader;

    @PostConstruct
    void init() {
        uploader.startSendProcess();
    }

    @PreDestroy
    void finish() {
        uploader.stopSendProcess();
    }

    public KafkaConsumer(MessageUploader uploader) {
        this.uploader = uploader;
    }

    @KafkaListener(topics = "${sender.from-topic}")
    void uploadMessage(ConsumerRecord<UUID, KafkaMessage> message, Acknowledgment acknowledgment) {
        try {
            log.info("start processing {}", message);
            uploader.upload(message.value());
            acknowledgment.acknowledge();
        } catch (SenderException e) {
            log.error("uploadMessage error", e);
        }
    }
}
