package ru.otus.exchange.receiver.info;

import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.receiver.domain.MessageInfo;
import ru.otus.exchange.receiver.info.jdbc.DepartmentDB;
import ru.otus.exchange.receiver.info.jdbc.JdbcOperations;
import ru.otus.exchange.receiver.info.jdbc.MessageInfoDB;

@Slf4j
public class JdbcLowLevelStorage implements LowLeverStorage {

    private final JdbcOperations jdbcOperations;

    public JdbcLowLevelStorage(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public boolean isAcceptable(String code) {
        DepartmentDB departmentDB = jdbcOperations.findDepartmentByCode(code);
        if (departmentDB == null) {
            log.warn("department by code {} not found", code);
            return false;
        }
        return departmentDB.isAcceptable();
    }

    @Override
    public void insertIgnore(MessageInfo messageInfo) {
        DepartmentDB departmentDB = jdbcOperations.findDepartmentByCode(messageInfo.getCreator());
        MessageInfoDB messageInfoDB = new MessageInfoDB();
        messageInfoDB.setMessageID(messageInfo.getMessageID());
        messageInfoDB.setCreatorID(departmentDB.getId());
        messageInfoDB.setType(messageInfo.getBodyQName().toString());
        messageInfoDB.setProcessGUID(messageInfo.getProcessGUID().toString());
        jdbcOperations.addMessageInfo(messageInfoDB);
    }

    @Override
    public String getProcessGUID(String processGUID, String messageID) {
        MessageInfoDB messageInfoDB = jdbcOperations.findMessageInfo(messageID);
        if (messageInfoDB == null) {
            log.warn("messageInfoDB by messageID {} not found", messageID);
            return processGUID;
        }
        return messageInfoDB.getProcessGUID();
    }

    @Override
    public String findDiscriminator(String code) {
        return Discriminator.EXCHANGE_MESSAGE.name();
    }
}
