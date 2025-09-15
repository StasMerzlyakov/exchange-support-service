package ru.otus.exchange.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.InputStream;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.exchange.fxml.NotXmlException;
import ru.otus.exchange.gateway.filters.validation.MessageInfo;
import ru.otus.exchange.gateway.filters.validation.MessageInfoExtractor;

class MessageInfoExtractorTest {

    @Test
    void test1() throws Exception {
        try (InputStream in = MessageInfoExtractorTest.class.getResourceAsStream("/soapenv-exchange-1.xsd.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));

            Assertions.assertDoesNotThrow(() -> {
                MessageInfo messageInfo = new MessageInfoExtractor().extractInfo(xml);
                assertEquals("2222222222222222222222222", messageInfo.getMessageID());
                assertEquals("123456789", messageInfo.getFrom());
                assertEquals("333444555", messageInfo.getTo());
                assertEquals("3333333333333333333333333", messageInfo.getReplyTo());
                assertEquals("{http://exchange.support/messages/exchange}ExchangeMessage", messageInfo.getBodyQName());
            });
        }
    }

    @Test
    void test2() throws Exception {
        try (InputStream in = MessageInfoExtractorTest.class.getResourceAsStream("/soapenv-exchange-2.xsd.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));

            Assertions.assertDoesNotThrow(() -> {
                MessageInfo messageInfo = new MessageInfoExtractor().extractInfo(xml);
                assertEquals("2222222222222222222222222", messageInfo.getMessageID());
                assertEquals("123456789", messageInfo.getFrom());
                assertEquals("333444555", messageInfo.getTo());
                assertNull(messageInfo.getReplyTo());
                assertEquals("{http://exchange.support/messages/exchange}ExchangeMessage", messageInfo.getBodyQName());
            });
        }
    }

    @Test
    void test3() throws Exception {
        try (InputStream in = MessageInfoExtractorTest.class.getResourceAsStream("/soapenv-exchange-3.xsd.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));

            Assertions.assertThrows(NotXmlException.class, () -> {
                new MessageInfoExtractor().extractInfo(xml);
            });
        }
    }
}
