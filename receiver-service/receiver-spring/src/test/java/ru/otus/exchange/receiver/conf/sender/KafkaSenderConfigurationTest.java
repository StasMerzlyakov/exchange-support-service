package ru.otus.exchange.receiver.conf.sender;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.common.SagaMessage;

class KafkaSenderConfigurationTest {

    @Test
    void test1() throws Exception {
        KafkaSenderConfiguration configuration = new KafkaSenderConfiguration();
        ObjectMapper objectMapper = configuration.kafkaObjectMapper();

        UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String key = "123456";

        Discriminator discriminator = Discriminator.EXCHANGE_MESSAGE;

        SagaMessage sagaMessage = new SagaMessage(uuid, key, discriminator);

        String expectedJson =
                """
                {
                    "exchange":"550e8400-e29b-41d4-a716-446655440000",
                    "key":"123456",
                    "discriminator":"exchangeMessage"
                }
                """;

        String actualJson = objectMapper.writeValueAsString(sagaMessage);

        assertThatJson(actualJson).isEqualTo(expectedJson);

        SagaMessage actualMessage = objectMapper.readValue(expectedJson, SagaMessage.class);
        assertThat(actualMessage).isEqualTo(sagaMessage);
    }
}
