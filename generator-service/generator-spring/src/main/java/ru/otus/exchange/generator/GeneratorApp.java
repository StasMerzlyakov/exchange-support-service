package ru.otus.exchange.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.otus.exchange.generator")
public class GeneratorApp {
    public static void main(String... args) {
        SpringApplication.run(GeneratorApp.class, args);
    }
}
