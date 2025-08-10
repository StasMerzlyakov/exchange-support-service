package ru.otus.exchange.receiver.rest;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;
import static ru.otus.exchange.common.Constants.REQUEST_ID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "receiver")
@RestController
@RequestMapping("/api/v1/api/v1.0")
public interface ReceiverSwaggerApi {

    @Operation(summary = "message receiver", description = "receive and store message")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Successfully retrieved message", content = @Content),
                @ApiResponse(responseCode = "400", description = "Message format error", content = @Content),
                @ApiResponse(responseCode = "403", description = "Department is not acceptable", content = @Content),
                @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
            })
    @PostMapping(value = "/", consumes = "application/octet-stream")
    ResponseEntity<Void> receive(
            @Parameter(
                            required = true,
                            description = "request identifier",
                            example = "12345678-1234-5678-1234-567812345678",
                            name = REQUEST_ID,
                            in = HEADER)
                    String requestId,
            @RequestBody(
                            required = true,
                            description = "contend defined by soapenv-exchange.xsd",
                            content = @Content(mediaType = "application/octet-stream"))
                    byte[] message);
}
