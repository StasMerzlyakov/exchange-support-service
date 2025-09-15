package ru.otus.exchange.receiver.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.otus.exchange.receiver.errors.*;

@Slf4j
@ControllerAdvice
public class ReceiverExceptionHandler {
    @ExceptionHandler({SenderException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleKafkaSendError() {
        log.error("can't send message to kafka");
    }

    @ExceptionHandler({MessageFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMessageFormatError() {
        log.error("can't parse error");
    }

    @ExceptionHandler({InfoStorageException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleInfoStorageException() {
        log.error("can't store message info");
    }

    @ExceptionHandler({ContentStorageException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleContentStorageException() {
        log.error("can't store message content");
    }

    @ExceptionHandler({NotAcceptableException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleNotAcceptableException() {
        log.error("message is not acceptable");
    }
}
