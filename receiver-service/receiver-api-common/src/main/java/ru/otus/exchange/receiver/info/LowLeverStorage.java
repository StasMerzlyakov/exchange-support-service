package ru.otus.exchange.receiver.info;

import ru.otus.exchange.receiver.domain.MessageInfo;

public interface LowLeverStorage {
    boolean isAcceptable(String code);

    void insertIgnore(MessageInfo messageInfo);

    String getProcessGUID(String processGUID, String messageID);

    String findDiscriminator(String code);
}
