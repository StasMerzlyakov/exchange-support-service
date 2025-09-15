package ru.otus.exchange.sender.conf;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientMapper;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientStorage;
import ru.otus.exchange.blobutils.*;
import ru.otus.exchange.sender.core.BlobLoaderCallbackImpl;

@Configuration
public class BlobStorageConfiguration {

    @Bean
    public JsonProcessor jsonProcessor(BlobLoader blobLoader) {
        return new JsonProcessorImpl(blobLoader);
    }

    @Bean
    public BlobLoader blobLoader(BlobLoaderCallback blobLoaderCallback) {
        return new BlobLoaderImpl(blobLoaderCallback);
    }

    @Bean
    public BlobLoaderCallback blobLoaderCallback(StorageSync storageSync) {
        return new BlobLoaderCallbackImpl(storageSync);
    }

    @Bean
    public StorageSync grpcBlobStorageClientStorage(SenderProperties properties) {
        String host = properties.getBlobStorageHost();
        int port = properties.getBlobStoragePort();

        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        var mapper = Mappers.getMapper(GRPCBlobStorageClientMapper.class);
        return new GRPCBlobStorageClientStorage(channel, mapper);
    }
}
