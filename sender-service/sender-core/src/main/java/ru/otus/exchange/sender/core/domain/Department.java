package ru.otus.exchange.sender.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private int id;
    private String code;
    private String endpoint;
    private boolean active;
}
