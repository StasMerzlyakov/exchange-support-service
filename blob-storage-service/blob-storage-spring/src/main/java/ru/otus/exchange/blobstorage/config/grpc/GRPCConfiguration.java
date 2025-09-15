package ru.otus.exchange.blobstorage.config.grpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.service.GrpcService;
import ru.otus.exchange.blobstorage.Storage;
import ru.otus.exchange.blobstorage.grpc.GRPCBlobStorageService;
import ru.otus.exchange.blobstorage.grpc.GlobalExceptionInterceptor;
import ru.otus.exchange.blobstorage.grpc.mapping.BlobStorageMapper;

@Configuration
public class GRPCConfiguration {

    @Bean
    @GlobalServerInterceptor
    public GlobalExceptionInterceptor exceptionInterceptor() {
        return new GlobalExceptionInterceptor();
    }

    @GrpcService
    public GRPCBlobStorageService blobStorageService(Storage storage, BlobStorageMapper mapper) {
        return new GRPCBlobStorageService(storage, mapper);
    }
}
