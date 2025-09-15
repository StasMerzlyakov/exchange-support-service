package ru.otus.exchange.gateway.filters.validation;

import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.otus.exchange.fxml.NotXmlException;

@Component
@Slf4j
public class ValidateInputXMLGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ValidateInputXMLGatewayFilterFactory.Config> {

    private final MessageInfoExtractor extractor;

    private final Validator validator;

    public ValidateInputXMLGatewayFilterFactory(MessageInfoExtractor messageInfoExtractor, Validator validator) {
        super(Config.class);
        extractor = messageInfoExtractor;
        this.validator = validator;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            // Прим. Для себя на будущее: XPathSearcher работает в один проход, технически нет смысла кэшировать
            // входной запрос и надо переделать на nio.
            byte[] cachedBody = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);

            if (cachedBody == null) {
                log.warn("configuration error: body is not cached");
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return Mono.empty();
            }

            MessageInfo messageInfo;
            try {
                messageInfo = extractor.extractInfo(cachedBody);

                var validations = validator.validate(messageInfo);
                if (!validations.isEmpty()) {
                    log.warn(
                            "❌ message validation error: {}",
                            String.join(
                                    ", ",
                                    validations.stream()
                                            .map(it -> it.getMessage())
                                            .toList()));
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return Mono.empty();
                }

                if (!config.acceptable.contains(messageInfo.getFrom())) {
                    log.warn("department is not acceptable");
                    exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                    return Mono.empty();
                }

            } catch (NotXmlException e) {
                log.warn("xml validation error", e);
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return Mono.empty();
            } catch (IOException e) {
                log.warn("ioexception error: ", e);
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return Mono.empty();
            }

            return chain.filter(exchange)
                    .doOnError(t -> log.error("❌ message {} error", messageInfo.getMessageID(), t))
                    .doOnSuccess(v -> log.info("✅ message {} accepted", messageInfo.getMessageID()));
        };
    }

    @Data
    public static class Config {
        private Set<String> acceptable;
    }
}
