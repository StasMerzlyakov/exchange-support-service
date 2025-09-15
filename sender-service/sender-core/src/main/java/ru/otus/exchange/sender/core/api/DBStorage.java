package ru.otus.exchange.sender.core.api;

import java.util.List;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.sender.core.domain.Department;
import ru.otus.exchange.sender.core.domain.Message;

public interface DBStorage {
    List<Department> getDepartments();

    List<Department> getDepartmentToSend(Discriminator discriminator);

    void addMessage(Message message);

    void setMessageSent(Message message);

    List<Message> findMessageToSend(Department department);
}
