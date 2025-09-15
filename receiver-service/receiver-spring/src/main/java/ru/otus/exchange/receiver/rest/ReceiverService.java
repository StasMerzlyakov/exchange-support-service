package ru.otus.exchange.receiver.rest;

import static ru.otus.exchange.common.Constants.REQUEST_ID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.otus.exchange.receiver.ReceiverController;

@Slf4j
@Controller
public class ReceiverService implements ReceiverSwaggerApi {

    private final ReceiverController controller;

    public ReceiverService(ReceiverController controller) {
        this.controller = controller;
    }

    @Override
    public ResponseEntity<Void> receive(@RequestHeader(REQUEST_ID) String requestId, @RequestBody byte[] message) {
        log.info("requestId {}, message size: {}", requestId, message.length);
        controller.process(requestId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
