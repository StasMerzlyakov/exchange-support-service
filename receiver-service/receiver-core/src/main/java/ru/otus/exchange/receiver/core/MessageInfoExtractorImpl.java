package ru.otus.exchange.receiver.core;

import static ru.otus.exchange.common.Constants.*;
import static ru.otus.exchange.common.Constants.BODY_CONTENT_PATH;

import jakarta.validation.Validator;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.fxml.XPathSearcher;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.errors.MessageFormatException;

@Slf4j
public class MessageInfoExtractorImpl implements MessageInfoExtractor {

    private final Validator validator;

    public MessageInfoExtractorImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public MessageInfo parse(String requestID, byte[] message) throws MessageFormatException {
        UUID processGUID = null;

        try {
            processGUID = UUID.fromString(requestID);
        } catch (IllegalArgumentException ie) {
            log.error("❌ requestId validation failed - wrong uuid format");
            throw new MessageFormatException(ie);
        }

        MessageInfo messageInfo;

        try {
            XPathSearcher searcher = new XPathSearcher(new ByteArrayInputStream(message));
            List<String> qnamePathList = List.of(MESSAGE_ID_PATH, FROM_PATH, TO_PATH, REPLY_TO_PATH, BODY_CONTENT_PATH);

            Map<String, String> result = searcher.getValuesByPath(qnamePathList);

            QName messgeBodyQName = QName.valueOf(result.get(BODY_CONTENT_PATH));

            messageInfo = MessageInfo.builder()
                    .processGUID(processGUID)
                    .messageID(result.get(MESSAGE_ID_PATH))
                    .creator(result.get(FROM_PATH))
                    .bodyQName(messgeBodyQName)
                    .build();

        } catch (Exception e) {
            log.error("❌ parse message error", e);
            throw new MessageFormatException(e);
        }

        var validations = validator.validate(messageInfo);
        if (!validations.isEmpty()) {
            String errorDesc = String.join(
                    ", ", validations.stream().map(it -> it.getMessage()).toList());
            log.error("❌ message validation error: {}", errorDesc);
            throw new MessageFormatException(errorDesc);
        }

        return messageInfo;
    }
}
