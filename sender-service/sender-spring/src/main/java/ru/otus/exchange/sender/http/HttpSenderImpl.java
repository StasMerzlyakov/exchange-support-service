package ru.otus.exchange.sender.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.otus.exchange.sender.core.api.HttpSender;
import ru.otus.exchange.sender.core.errors.HttpException;

@Slf4j
public class HttpSenderImpl implements HttpSender {

    private final RestTemplate restTemplate;

    public HttpSenderImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(String endpoint, byte[] message) throws HttpException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(message, headers);
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(endpoint, requestEntity, Void.class);
            log.info("send to endpoint {} status {}", endpoint, responseEntity.getStatusCode());
        } catch (Exception e) {
            throw new HttpException(e);
        }
    }
}
