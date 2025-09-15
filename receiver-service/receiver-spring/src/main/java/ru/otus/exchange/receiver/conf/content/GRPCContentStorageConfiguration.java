package ru.otus.exchange.receiver.conf.content;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.exchange.blobstorage.StorageSync;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientMapper;
import ru.otus.exchange.blobstorage.grpc.v1.client.blocking.GRPCBlobStorageClientStorage;
import ru.otus.exchange.receiver.conf.ReceiverProperties;

@Configuration
@ConditionalOnProperty(name = "blobStorageType", havingValue = "grpc")
public class GRPCContentStorageConfiguration {

    @Bean
    public StorageSync grpcBlobStorageClientStorage(ReceiverProperties receiverProperties) {
        String host = receiverProperties.getBlobStorageHost();
        int port = receiverProperties.getBlobStoragePort();

        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        var mapper = Mappers.getMapper(GRPCBlobStorageClientMapper.class);
        return new GRPCBlobStorageClientStorage(channel, mapper);
    }
}
