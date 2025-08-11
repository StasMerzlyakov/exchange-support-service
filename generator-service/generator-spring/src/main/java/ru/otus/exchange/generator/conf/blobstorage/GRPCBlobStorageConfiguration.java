package ru.otus.exchange.generator.conf.blobstorage;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientMapper;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientStorage;
import ru.otus.exchange.generator.conf.GeneratorProperties;

@Configuration
@ConditionalOnProperty(name = "blobStorageType", havingValue = "grpc")
public class GRPCBlobStorageConfiguration {

    @Bean
    public StorageSync grpcBlobStorageClientStorage(GeneratorProperties generatorProperties) {
        String host = generatorProperties.getBlobStorageHost();
        int port = generatorProperties.getBlobStoragePort();

        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        var mapper = Mappers.getMapper(GRPCBlobStorageClientMapper.class);
        return new GRPCBlobStorageClientStorage(channel, mapper);
    }
}
