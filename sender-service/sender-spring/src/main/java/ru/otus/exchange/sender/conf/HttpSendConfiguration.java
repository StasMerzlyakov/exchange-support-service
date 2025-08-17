package ru.otus.exchange.sender.conf;

import io.micrometer.core.instrument.binder.httpcomponents.hc5.ObservationExecChainHandler;
import io.micrometer.observation.ObservationRegistry;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.sender.core.api.HttpSender;
import ru.otus.exchange.sender.http.HttpSenderImpl;

@Configuration
public class HttpSendConfiguration {

    @Bean
    public HttpClient createHttpClient(SenderProperties properties, ObservationRegistry observationRegistry) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(properties.maxConnTotal);
        connectionManager.setDefaultMaxPerRoute(properties.maxConnPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(properties.connectRequestTimeoutMls, TimeUnit.MILLISECONDS)
                .setDefaultKeepAlive(properties.keepAliveMls, TimeUnit.MILLISECONDS)
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .addExecInterceptorLast("micrometer", new ObservationExecChainHandler(observationRegistry))
                .build();
    }

    @Bean
    public HttpSender httpSender(HttpClient httpClient) {
        return new HttpSenderImpl(httpClient);
    }
}
