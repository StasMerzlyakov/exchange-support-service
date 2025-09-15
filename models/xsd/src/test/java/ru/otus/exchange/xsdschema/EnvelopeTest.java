package ru.otus.exchange.xsdschema;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.exchange.xsdschema.messages.GenderType;

class EnvelopeTest {

    @Test
    void doUnmarshalling() throws Exception {
        try (InputStream is = EnvelopeTest.class.getResourceAsStream("/soapenv-exchange.xsd.xml")) {
            Assertions.assertNotNull(is);

            Source ss = new StreamSource(is);

            JAXBContext context = JAXBContext.newInstance("ru.otus.exchange.xsdschema");

            Unmarshaller unmarshaller = context.createUnmarshaller();

            EnvelopeType envelope =
                    unmarshaller.unmarshal(ss, EnvelopeType.class).getValue();

            assertEquals(
                    GenderType.MALE,
                    envelope.body.exchangeMessage.getPersonDataType().getGender());
        }
    }

    @Test
    void doMarshalling() throws Exception {
        try (InputStream is = EnvelopeTest.class.getResourceAsStream("/soapenv-exchange.xsd.xml")) {
            Assertions.assertNotNull(is);

            Source ss = new StreamSource(is);

            JAXBContext context = JAXBContext.newInstance("ru.otus.exchange.xsdschema");

            Unmarshaller unmarshaller = context.createUnmarshaller();

            EnvelopeType envelope =
                    unmarshaller.unmarshal(ss, EnvelopeType.class).getValue();

            assertDoesNotThrow(() -> {
                Marshaller marshaller = context.createMarshaller();
                marshaller.marshal(envelope, OutputStream.nullOutputStream());
            });
        }
    }
}
