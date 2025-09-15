package ru.otus.exchange.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.exchange.common.Constants;
import ru.otus.exchange.gateway.filters.requestid.UUIDGenerator;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("requestID")
class RequestIDTest {

    private static UUID testId = UUID.randomUUID();

    @MockitoBean
    @Qualifier("uuidGenerator")
    private UUIDGenerator generator;

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private Environment environment;

    @Value("${serviceId}")
    private String serviceId;

    @Test
    @DirtiesContext
    void doRequestIdOkTest() {
        assertThat(environment.containsProperty("wiremock.server.port")).isTrue();

        when(generator.next()).thenReturn(testId);

        String testContent = "Hello from downstream!";

        stubFor(post("/v1/api")
                .withHeader(Constants.REQUEST_ID, equalTo(testId.toString()))
                .withHeader(Constants.SERVICE_ID, equalTo(serviceId))
                .willReturn(aResponse().withStatus(201).withBody(testContent)));

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
    void doRequestIdNotFoundTest() {
        assertThat(environment.containsProperty("wiremock.server.port")).isTrue();

        when(generator.next()).thenReturn(testId);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpUriRequest request = new HttpPost(String.format("http://localhost:%d/notFound", localServerPort));
        Assertions.assertDoesNotThrow(() -> {
            HttpResponse httpResponse = httpClient.execute(request);
            assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(404);
        });
    }
}
