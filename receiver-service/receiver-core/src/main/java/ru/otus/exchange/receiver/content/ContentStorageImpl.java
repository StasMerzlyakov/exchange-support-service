package ru.otus.exchange.receiver.content;

import java.util.Set;
import java.util.UUID;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobutils.BlobXmlPathHolder;
import ru.otus.exchange.blobutils.XmlProcessor;
import ru.otus.exchange.receiver.ContentStorage;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.errors.ContentStorageException;

@Slf4j
public class ContentStorageImpl implements ContentStorage {

    private final XmlProcessor xmlProcessor;

    private final String xmlFileName;

    private final BlobXmlPathHolder blobXmlPathHolder;

    public ContentStorageImpl(BlobXmlPathHolder blobXmlPathHolder, String xmlFileName, XmlProcessor xmlProcessor) {
        this.blobXmlPathHolder = blobXmlPathHolder;
        this.xmlFileName = xmlFileName;
        this.xmlProcessor = xmlProcessor;
    }

    @Override
    public void storeMessage(UUID processGUID, MessageInfo messageInfo, byte[] content) throws ContentStorageException {
        try {
            String exchange = processGUID.toString();
            QName messageBodyQName = messageInfo.getBodyQName();
            Set<String> blobPaths = blobXmlPathHolder.getBlobPath(messageBodyQName.toString());
            xmlProcessor.storeXml(exchange, content, blobPaths, xmlFileName);
        } catch (Exception e) {
            log.error("storeMessage error");
            throw new ContentStorageException(e);
        }
    }
}
