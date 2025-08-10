package ru.otus.exchange.receiver.info.jdbc;

public interface JdbcOperations {

    DepartmentDB findDepartmentByCode(String code);

    void addMessageInfo(MessageInfoDB messageInfoDB);

    MessageInfoDB findMessageInfo(String messageID);
}
