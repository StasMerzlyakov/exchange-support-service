package ru.otus.exchange.sender.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import ru.otus.exchange.sender.core.api.HttpSender;
import ru.otus.exchange.sender.core.errors.HttpException;

@Slf4j
public class HttpSenderImpl implements HttpSender {

    private final HttpClient httpClient;

    public HttpSenderImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void send(String endpoint, byte[] message) throws HttpException {
        try {

            ClassicHttpRequest request = ClassicRequestBuilder.post(endpoint)
                    .setEntity(message, ContentType.APPLICATION_JSON)
                    .build();
            httpClient.execute(request, response -> {
                log.info("send to endpoint {} status {}", endpoint, response.getCode());
                return "";
            });
        } catch (Exception e) {
            throw new HttpException(e);
        }
    }
}
