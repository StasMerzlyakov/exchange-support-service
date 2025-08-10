package ru.otus.exchange.receiver.info.jdbc;

import lombok.Data;

@Data
public class MessageInfoDB {
    long id;
    String messageID;
    String processGUID;
    int creatorID;
    String type;
}
