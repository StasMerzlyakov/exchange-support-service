package ru.otus.exchange.receiver;

import java.util.UUID;
import javax.xml.namespace.QName;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.errors.InfoStorageException;

public interface InfoStorage {
    boolean isAcceptable(String code) throws InfoStorageException;

    UUID putIfNotExistsAndGetProcessGUID(MessageInfo messageInfo) throws InfoStorageException;

    Discriminator findDiscriminator(QName bodyQName) throws InfoStorageException;
}
