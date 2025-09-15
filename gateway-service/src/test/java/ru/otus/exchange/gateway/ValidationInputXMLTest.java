package ru.otus.exchange.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("validateInput")
class ValidationInputXMLTest {

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private Environment environment;

    @Test
    @DirtiesContext
    void doOkRequest() throws IOException {

        try (InputStream in = ValidationInputXMLTest.class.getResourceAsStream("/soapenv-exchange-1.xsd.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));

            assertThat(environment.containsProperty("wiremock.server.port")).isTrue();

            String testContent = "Hello from downstream!";

            stubFor(post("/receive").willReturn(aResponse().withStatus(201).withBody(testContent)));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(String.format("http://localhost:%d/receive", localServerPort));
            HttpEntity requestEntity = new ByteArrayEntity(xml);
            request.setEntity(requestEntity);

            Assertions.assertDoesNotThrow(() -> {
                HttpResponse httpResponse = httpClient.execute(request);
                HttpEntity entity = httpResponse.getEntity();
                assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(201);
                String responseString = EntityUtils.toString(entity, "UTF-8");
                assertThat(responseString).isEqualTo(testContent);
            });
        }
    }

    @Test
    @DirtiesContext
    void doBadRequest() throws IOException {

        try (InputStream in = ValidationInputXMLTest.class.getResourceAsStream("/soapenv-exchange-4.xsd.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));

            assertThat(environment.containsProperty("wiremock.server.port")).isTrue();

            String testContent = "Hello from downstream!";

            stubFor(post("/receive").willReturn(aResponse().withStatus(201).withBody(testContent)));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(String.format("http://localhost:%d/receive", localServerPort));
            HttpEntity requestEntity = new ByteArrayEntity(xml);
            request.setEntity(requestEntity);

            Assertions.assertDoesNotThrow(() -> {
                HttpResponse httpResponse = httpClient.execute(request);
                assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(BAD_REQUEST.code());
            });
        }
    }
}
