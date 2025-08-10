package ru.otus.exchange.receiver.info.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.otus.exchange.receiver.errors.NotAcceptableException;
import ru.otus.exchange.receiver.rest.ReceiverSwaggerApi;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test-jdbc")
class JdbcLowLevelStorageTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    private ReceiverSwaggerApi receiver;

    @Test
    void test1() throws IOException {
        try (InputStream in = JdbcLowLevelStorageTest.class.getResourceAsStream("/files/soapenv-exchange-test1.xml")) {
            Assertions.assertNotNull(in);
            byte[] message = in.readAllBytes();
            String requestID = UUID.randomUUID().toString();
            Assertions.assertThrows(NotAcceptableException.class, () -> receiver.receive(requestID, message));
        }
    }

    @Test
    void test2() throws IOException {
        try (InputStream in = JdbcLowLevelStorageTest.class.getResourceAsStream("/files/soapenv-exchange-test2.xml")) {
            Assertions.assertNotNull(in);
            byte[] message = in.readAllBytes();
            String requestID = UUID.randomUUID().toString();
            Assertions.assertDoesNotThrow(() -> receiver.receive(requestID, message));
        }
    }
}
