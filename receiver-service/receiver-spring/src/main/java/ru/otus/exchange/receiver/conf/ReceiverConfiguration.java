package ru.otus.exchange.receiver.conf;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobutils.*;
import ru.otus.exchange.receiver.ContentStorage;
import ru.otus.exchange.receiver.InfoStorage;
import ru.otus.exchange.receiver.KafkaSender;
import ru.otus.exchange.receiver.ReceiverController;
import ru.otus.exchange.receiver.content.BlobSaverCallbackAdapter;
import ru.otus.exchange.receiver.content.ConstantBlobXmlPathHolderImpl;
import ru.otus.exchange.receiver.content.ContentStorageImpl;
import ru.otus.exchange.receiver.core.MessageInfoExtractor;
import ru.otus.exchange.receiver.core.MessageInfoExtractorImpl;
import ru.otus.exchange.receiver.core.ReceiverControllerImpl;
import ru.otus.exchange.receiver.info.InfoStorageImpl;
import ru.otus.exchange.receiver.info.LowLeverStorage;

@Configuration
public class ReceiverConfiguration {

    @Bean
    public BlobSaverCallbackAdapter blobSaverCallbackAdapter(StorageSync syncStorage) {
        return new BlobSaverCallbackAdapter(syncStorage);
    }

    @Bean
    public BlobXmlPathHolder blobXmlPathHolder() {
        return new ConstantBlobXmlPathHolderImpl();
    }

    @Bean
    public BlobSaver blobSaver(BlobSaverCallback blobSaverCallback) {
        return new BlobSaverImpl(blobSaverCallback);
    }

    @Bean
    public XmlProcessor xmlProcessor(BlobSaver blobSaver) {
        return new XmlProcessorImpl(blobSaver);
    }

    @Bean
    public ContentStorage contentStorage(
            BlobXmlPathHolder blobXmlPathHolder, ReceiverProperties receiverProperties, XmlProcessor xmlProcessor) {
        return new ContentStorageImpl(blobXmlPathHolder, xmlProcessor);
    }

    @Bean
    public InfoStorage infoStorage(LowLeverStorage lowLeverStorage) {
        return new InfoStorageImpl(lowLeverStorage);
    }

    @Bean
    public MessageInfoExtractor messageInfoExtractor(Validator validator) {
        return new MessageInfoExtractorImpl(validator);
    }

    @Bean
    public ReceiverController receiverController(
            MessageInfoExtractor extractor,
            InfoStorage infoStorage,
            ContentStorage contentStorage,
            KafkaSender kafkaSender) {
        return new ReceiverControllerImpl(extractor, infoStorage, contentStorage, kafkaSender);
    }
}
