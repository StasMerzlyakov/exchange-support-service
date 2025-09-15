package ru.otus.exchange.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.otus.exchange.receiver")
public class ReceiverServiceApp {
    public static void main(String... args) {
        SpringApplication.run(ReceiverServiceApp.class, args);
    }
}
