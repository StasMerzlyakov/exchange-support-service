package ru.otus.exchange.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import ru.otus.exchange.blobstorage.StorageKey;
import ru.otus.exchange.generator.errors.GeneratorException;
import ru.otus.exchange.json.ExchangeMessage;
import ru.otus.exchange.xsdschema.EnvelopeType;
import ru.otus.exchange.xsdschema.messages.BirthDateType;
import ru.otus.exchange.xsdschema.messages.ExchangeMessageType;
import ru.otus.exchange.xsdschema.messages.PersonDataType;

public class ExchangeMessageGeneratorImpl implements ExchangeMessageGenerator {

    private final DataStorage dataStorage;
    private final ObjectMapper objectMapper;
    private final JAXBContext jaxbContextEnvelope;

    public ExchangeMessageGeneratorImpl(DataStorage dataStorage) throws JAXBException {
        this.dataStorage = dataStorage;
        this.objectMapper = new ObjectMapper();
        this.jaxbContextEnvelope = JAXBContext.newInstance(EnvelopeType.class);
    }

    @Override
    public void generate(UUID jsonMessageID, StorageKey xmlStorageKey) throws GeneratorException {

        EnvelopeType envelopeType = restoreMessage(xmlStorageKey);
        ExchangeMessage exchangeMessage = generateMessage(jsonMessageID, envelopeType);

        StorageKey storageKey = new StorageKey(xmlStorageKey.exchange(), jsonMessageID.toString());

        storeMessage(storageKey, exchangeMessage);
    }

    private void storeMessage(StorageKey jsonStorageKey, ExchangeMessage exchangeMessage) {
        try {
            var jsonBA = objectMapper.writeValueAsBytes(exchangeMessage);
            dataStorage.writeData(jsonStorageKey, jsonBA);
        } catch (JsonProcessingException e) {
            throw new GeneratorException(e);
        }
    }

    private EnvelopeType restoreMessage(StorageKey xmlStorageKey) {
        // Проверки на нули и прочее это на будущее
        try {
            byte[] xmlMessage = dataStorage.readData(xmlStorageKey);

            Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();

            InputStream is = new ByteArrayInputStream(xmlMessage);
            Source ss = new StreamSource(is);

            return unmarshaller.unmarshal(ss, EnvelopeType.class).getValue();
        } catch (JAXBException ex) {
            throw new GeneratorException(ex);
        }
    }

    private ExchangeMessage generateMessage(UUID jsonMessageID, EnvelopeType envelopeType) throws GeneratorException {

        ExchangeMessage exchangeMessage = new ExchangeMessage();
        exchangeMessage.setMessageID(jsonMessageID);
        ExchangeMessageType exchangeMessageType = envelopeType.getBody().getExchangeMessage();
        exchangeMessage.setPhoto(exchangeMessageType.getPhoto());
        PersonDataType personDataType = exchangeMessageType.getPersonDataType();
        exchangeMessage.setGender(personDataType.getGender().name());
        exchangeMessage.setFirstName(personDataType.getFirstName());
        exchangeMessage.setLastName(personDataType.getLastName());
        exchangeMessage.setPatronymic(personDataType.getPatronymic());
        exchangeMessage.setBirthDate(getBirthDate(personDataType.getBirthDate()));
        return exchangeMessage;
    }

    public String getBirthDate(BirthDateType birthDateType) {
        return String.format(
                "%04d-%02d-%02d",
                birthDateType.getYear(),
                birthDateType.getMonth() == null ? 0 : birthDateType.getMonth(),
                birthDateType.getDay() == null ? 0 : birthDateType.getDay());
    }
}
