package ru.otus.exchange.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication(scanBasePackages = "ru.otus.exchange.gateway")
public class GateWayService {

    public static void main(String... args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(GateWayService.class, args);
    }
}
