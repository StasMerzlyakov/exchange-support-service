package ru.otus.exchange.receiver.info.jdbc;

import lombok.Data;

@Data
public class DepartmentDB {
    int id;
    String code;
    boolean isAcceptable;
}
