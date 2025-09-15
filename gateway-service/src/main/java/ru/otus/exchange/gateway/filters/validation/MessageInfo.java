package ru.otus.exchange.gateway.filters.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageInfo {

    @NotNull(message = "Field MessageID is null")
    @Size(max = 25, min = 25, message = "Field MessageID have wrong size")
    @Pattern(regexp = "\\d+", message = "Field MessageID does not match regexp pattern")
    private String messageID;

    @NotNull(message = "Field From is null")
    @Size(max = 9, min = 9, message = "Field From have wrong size")
    @Pattern(regexp = "\\d+", message = "Field From does not match regexp pattern")
    private String from;

    @NotNull(message = "Field To is null")
    @Size(max = 9, min = 9, message = "Field To have wrong size")
    @Pattern(regexp = "\\d+", message = "Field To does not match regexp pattern")
    private String to;

    @NotEmpty(message = "Body content is empty")
    private String bodyQName;

    @Size(max = 25, min = 25, message = "Field ReplyTo have wrong size")
    @Pattern(regexp = "\\d+", message = "Field ReplyTo does not match regexp pattern")
    private String replyTo;
}
