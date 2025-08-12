package ru.otus.exchange.sender.conf;

import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.otus.exchange.sender.core.api.HttpSender;
import ru.otus.exchange.sender.http.HttpSenderImpl;

@Configuration
public class HttpSendConfiguration {

    @Bean
    public RestTemplate getRestTemplate(SenderProperties properties) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(properties.maxConnTotal);
        connectionManager.setDefaultMaxPerRoute(properties.maxConnPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(properties.connectRequestTimeoutMls, TimeUnit.MILLISECONDS)
                .setDefaultKeepAlive(properties.keepAliveMls, TimeUnit.MILLISECONDS)
                .build();

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);
    }

    @Bean
    public HttpSender httpSender(RestTemplate restTemplate) {
        return new HttpSenderImpl(restTemplate);
    }
}
