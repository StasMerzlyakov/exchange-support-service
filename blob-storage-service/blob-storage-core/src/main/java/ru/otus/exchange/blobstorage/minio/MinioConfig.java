package ru.otus.exchange.blobstorage.minio;

import java.time.Duration;

public record MinioConfig(String bucket, Duration opTimeout) {}
