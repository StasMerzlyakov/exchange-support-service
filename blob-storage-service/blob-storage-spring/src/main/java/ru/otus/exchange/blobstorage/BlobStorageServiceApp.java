package ru.otus.exchange.blobstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication(scanBasePackages = "ru.otus.exchange.blobstorage")
public class BlobStorageServiceApp {
    public static void main(String... args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(BlobStorageServiceApp.class, args);
    }
}
