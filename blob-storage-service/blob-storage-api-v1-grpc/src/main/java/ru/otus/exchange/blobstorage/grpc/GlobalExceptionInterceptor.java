package ru.otus.exchange.blobstorage.grpc;

import io.grpc.*;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.blobstorage.exceptions.MemoryException;
import ru.otus.exchange.blobstorage.exceptions.MinioException;
import ru.otus.exchange.blobstorage.exceptions.OverrideForbiddenException;
import ru.otus.exchange.blobstorage.exceptions.RedisException;

@Slf4j
@SuppressWarnings("java:S119")
public class GlobalExceptionInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall, Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {
        try {
            return next.startCall(new SimpleForwardingServerCall<>(serverCall) {}, requestHeaders);
        } catch (Exception ex) {
            log.error("interaptCall", ex);
            return handleInterceptorException(ex, serverCall);
        }
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> handleInterceptorException(
            Throwable t, ServerCall<ReqT, RespT> serverCall) {

        switch (t) {
            case MemoryException me:
                {
                    log.error("memory exception", me);
                    serverCall.close(Status.INTERNAL.withCause(t), new Metadata());
                }
                break;
            case MinioException me:
                {
                    log.error("minio exception", me);
                    serverCall.close(Status.INTERNAL.withCause(t), new Metadata());
                }
                break;
            case RedisException re:
                {
                    log.error("redis exception", re);
                    serverCall.close(Status.INTERNAL.withCause(t), new Metadata());
                }
                break;
            case OverrideForbiddenException fe:
                {
                    log.error("override forbidden exception", fe);
                    serverCall.close(Status.ALREADY_EXISTS.withCause(t), new Metadata());
                }
                break;
            default:
                log.error("unexpected throwable type", t);
                throw new IllegalStateException("Unexpected value: " + t);
        }

        return new ServerCall.Listener<>() {
            // no-op
        };
    }
}
