package ru.otus.exchange.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.otus.exchange.sender")
public class SenderApp {
    public static void main(String[] args) {
        SpringApplication.run(SenderApp.class, args);
    }
}
