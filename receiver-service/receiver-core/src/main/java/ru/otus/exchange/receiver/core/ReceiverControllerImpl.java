package ru.otus.exchange.receiver.core;

import java.util.UUID;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.receiver.ContentStorage;
import ru.otus.exchange.receiver.InfoStorage;
import ru.otus.exchange.receiver.KafkaSender;
import ru.otus.exchange.receiver.ReceiverController;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.errors.NotAcceptableException;
import ru.otus.exchange.receiver.errors.ReceiverException;

@Slf4j
public class ReceiverControllerImpl implements ReceiverController {

    private final MessageInfoExtractor extractor;
    private final InfoStorage infoStorage;
    private final ContentStorage contentStorage;
    private final KafkaSender kafkaSender;

    public ReceiverControllerImpl(
            MessageInfoExtractor extractor,
            InfoStorage infoStorage,
            ContentStorage contentStorage,
            KafkaSender kafkaSender) {
        this.extractor = extractor;
        this.infoStorage = infoStorage;
        this.contentStorage = contentStorage;
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void process(String requestID, byte[] message) throws ReceiverException {
        MessageInfo messageInfo = extractor.parse(requestID, message);

        String depCode = messageInfo.getCreator();

        if (!infoStorage.isAcceptable(depCode)) {
            String errDesc = String.format("department %s is not acceptable", depCode);
            log.warn("requestID {} - {}", requestID, errDesc);
            throw new NotAcceptableException(errDesc);
        }

        QName messageBodyQName = messageInfo.getBodyQName();

        Discriminator discriminator = infoStorage.findDiscriminator(messageBodyQName);
        if (discriminator == null) {
            String errDesc =
                    String.format("messageBody %s processing error - discriminator not found", messageBodyQName);
            log.warn("requestID {} - {}", requestID, errDesc);
            throw new NotAcceptableException(errDesc);
        }

        UUID processGUID = infoStorage.putIfNotExistsAndGetProcessGUID(messageInfo);
        if (!processGUID.equals(messageInfo.getProcessGUID())) {
            log.warn("requestID {} - messageInfo exists, processGUID is changed to {}", requestID, processGUID);
        }

        // HINT считаем что хранилище идемпотентное, то есть данные не будут перезаписываться при наличии
        contentStorage.storeMessage(processGUID, messageInfo, message);

        KafkaMessage kafkaMessage = new KafkaMessage(processGUID, messageInfo.getMessageID(), discriminator);
        kafkaSender.send(kafkaMessage);
    }
}
