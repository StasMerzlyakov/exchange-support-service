package ru.otus.exchange.generator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.exchange.common.Discriminator;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage {

    @NotNull(message = "Field exchange is null")
    UUID exchange;

    @NotNull(message = "Field key is null")
    @Size(min = 3, message = "Field key have wrong size")
    String key;

    @NotNull(message = "Field discriminator is null")
    Discriminator discriminator;
}
