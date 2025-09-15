package ru.otus.exchange.gateway.filters.validation;

import static ru.otus.exchange.common.Constants.*;

import io.micrometer.observation.annotation.Observed;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.otus.exchange.fxml.NotXmlException;
import ru.otus.exchange.fxml.XPathSearcher;

@Component
public class MessageInfoExtractor {

    @Observed(name = "extractInfo")
    public MessageInfo extractInfo(byte[] message) throws NotXmlException, IOException {
        XPathSearcher searcher = new XPathSearcher(new ByteArrayInputStream(message));
        List<String> qnamePathList = List.of(MESSAGE_ID_PATH, FROM_PATH, TO_PATH, REPLY_TO_PATH, BODY_CONTENT_PATH);

        Map<String, String> result = searcher.getValuesByPath(qnamePathList);

        return MessageInfo.builder()
                .messageID(result.get(MESSAGE_ID_PATH))
                .from(result.get(FROM_PATH))
                .to(result.get(TO_PATH))
                .replyTo(result.get(REPLY_TO_PATH))
                .bodyQName(result.get(BODY_CONTENT_PATH))
                .build();
    }
}
