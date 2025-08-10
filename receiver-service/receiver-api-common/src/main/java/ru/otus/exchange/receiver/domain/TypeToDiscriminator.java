package ru.otus.exchange.receiver.domain;

import jakarta.validation.constraints.NotEmpty;
import javax.xml.namespace.QName;
import lombok.Builder;
import lombok.Data;
import ru.otus.exchange.common.Discriminator;

@Data
@Builder
public class TypeToDiscriminator {
    @NotEmpty(message = "Body content is empty")
    private QName bodyQName;

    @NotEmpty
    private Discriminator discriminator;
}
