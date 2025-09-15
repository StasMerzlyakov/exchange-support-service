package ru.otus.exchange.sender.core.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.exchange.common.Discriminator;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private int id;
    private UUID exchange;
    private String key;
    private Discriminator discriminator;
    private int departmentId;
    private boolean sent;
}
