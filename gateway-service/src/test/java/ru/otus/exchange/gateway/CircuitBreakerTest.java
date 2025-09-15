package ru.otus.exchange.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("circuitbreaker")
class CircuitBreakerTest {

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private Environment environment;

    @Value("${serviceId}")
    private String serviceId;

    @Test
    @DirtiesContext
    void doOkTest() {
        assertThat(environment.containsProperty("wiremock.server.port")).isTrue();

        String testContent = "Hello from downstream!";

        stubFor(post("/receive").willReturn(aResponse().withStatus(201).withBody(testContent)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpUriRequest request = new HttpPost(String.format("http://localhost:%d/receive", localServerPort));
        Assertions.assertDoesNotThrow(() -> {
            HttpResponse httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();
            assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(201);
            String responseString = EntityUtils.toString(entity, "UTF-8");
            assertThat(responseString).isEqualTo(testContent);
        });
    }

    @Test
    @DirtiesContext
    void doErrorTest() {

        assertThat(environment.containsProperty("wiremock.server.port")).isTrue();

        var iterationCount = 30;
        var iterationSleepDuration = Duration.ofMillis(200); // 30 * 200mls = 6 sec; rps ok, rpm ok

        var errorCounter = new AtomicInteger();
        var okCounter = new AtomicInteger();

        IntStream.rangeClosed(1, iterationCount).forEach(it -> {
            if (it % 2 == 0) {
                okCounter.incrementAndGet();
                stubFor(post("/receive")
                        .inScenario("circuitbreaker")
                        .whenScenarioStateIs("state" + it)
                        .willReturn(
                                aResponse().withStatus(HttpStatus.OK.value()).withBody("Hello from downstream!")));
            } else {
                errorCounter.incrementAndGet();
                stubFor(post("/receive")
                        .inScenario("circuitbreaker")
                        .whenScenarioStateIs("state" + it)
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withBody("internal error ")));
            }
        });

        var responses = new CopyOnWriteArrayList<Integer>();

        IntStream.rangeClosed(1, iterationCount).forEach(it -> {
            Assertions.assertDoesNotThrow(() -> {
                log.info("processing {}", it);
                WireMock.setScenarioState("circuitbreaker", "state" + it);
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpUriRequest request = new HttpPost(String.format("http://localhost:%d/receive", localServerPort));
                HttpResponse httpResponse = httpClient.execute(request);
                responses.add(httpResponse.getStatusLine().getStatusCode());
                await().during(iterationSleepDuration);
            });
        });

        var countMap = responses.stream().collect(Collectors.groupingBy(it -> it, Collectors.counting()));

        var okCount = okCounter.get();
        var errCount = errorCounter.get();

        // Сервис при работе mock-бина возвращает либо OK, либо INTERNAL_SERVER_ERROR
        // Но не все запросы прошли
        assertTrue(countMap.getOrDefault(HttpStatus.OK.value(), 0l) < okCount);
        assertTrue(countMap.getOrDefault(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0l) < errCount);

        // При срабатывании circuitBreaker возвращается SERVICE_UNAVAILABLE
        var unavailableCount = countMap.getOrDefault(HttpStatus.SERVICE_UNAVAILABLE.value(), 0l);

        // Проверяем, что circuitBreaker реально отрабатывает !!!
        assertTrue(unavailableCount > 0);
    }
}
