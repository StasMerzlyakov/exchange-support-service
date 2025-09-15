package ru.otus.exchange.receiver.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Department {

    @NotNull(message = "Code is null")
    @Size(max = 9, min = 9, message = "Code have wrong size")
    @Pattern(regexp = "\\d+", message = "Field Code does not match regexp pattern")
    String code;

    @NotNull(message = "Accepted is null")
    Boolean isAccepted;
}
