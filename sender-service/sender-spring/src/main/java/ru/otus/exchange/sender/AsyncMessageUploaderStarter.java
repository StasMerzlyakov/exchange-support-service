package ru.otus.exchange.sender;

import org.springframework.scheduling.annotation.Async;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.sender.core.MessageUploader;

public class AsyncMessageUploaderStarter implements MessageUploader {

    private final MessageUploader messageUploader;

    public AsyncMessageUploaderStarter(MessageUploader messageUploader) {
        this.messageUploader = messageUploader;
    }

    @Override
    public void upload(KafkaMessage kafkaMessage) {
        messageUploader.upload(kafkaMessage);
    }

    @Override
    @Async
    public void startSendProcess() {
        messageUploader.startSendProcess();
    }

    @Override
    @Async
    public void stopSendProcess() {
        messageUploader.stopSendProcess();
    }
}
