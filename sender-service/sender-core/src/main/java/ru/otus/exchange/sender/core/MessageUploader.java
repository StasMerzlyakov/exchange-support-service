package ru.otus.exchange.sender.core;

import ru.otus.exchange.common.KafkaMessage;

public interface MessageUploader {
    void upload(KafkaMessage kafkaMessage);

    void startSendProcess();

    void stopSendProcess();
}
