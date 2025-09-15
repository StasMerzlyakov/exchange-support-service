package ru.otus.exchange.json;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExchangeMessage {

    @NotNull
    UUID messageID;

    @NotNull
    String firstName;

    @NotNull
    String lastName;

    String patronymic;

    @NotNull
    String birthDate;

    @NotNull
    String gender;

    @NotNull
    byte[] photo;
}
