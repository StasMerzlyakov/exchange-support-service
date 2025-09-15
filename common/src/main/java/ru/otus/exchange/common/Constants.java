package ru.otus.exchange.common;

import static ru.otus.exchange.fxml.XPathSearcher.joinStringStack;

import java.util.List;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SERVICE_ID = "ServiceId";
    public static final String REQUEST_ID = "RequestId";

    public static final String BLOB_PREFIX = "BLOBZ:";
    public static final String BLOB_PREFIX_BASE64 = "QkxPQlo6";

    private static final String ENVELOPE_QNAME = "{http://exchange.support/envelope}Envelope";
    private static final String HEADER_QNAME = "{http://exchange.support/envelope}Header";
    private static final String ADDRESSING_QNAME = "{http://exchange.support/header/addressing}Addressing";

    public static final String MESSAGE_ID_PATH = joinStringStack(List.of(
            ENVELOPE_QNAME, HEADER_QNAME, ADDRESSING_QNAME, "{http://exchange.support/header/addressing}MessageID"));

    public static final String FROM_PATH = joinStringStack(
            List.of(ENVELOPE_QNAME, HEADER_QNAME, ADDRESSING_QNAME, "{http://exchange.support/header/addressing}From"));

    public static final String TO_PATH = joinStringStack(
            List.of(ENVELOPE_QNAME, HEADER_QNAME, ADDRESSING_QNAME, "{http://exchange.support/header/addressing}To"));

    public static final String REPLY_TO_PATH = joinStringStack(List.of(
            ENVELOPE_QNAME, HEADER_QNAME, ADDRESSING_QNAME, "{http://exchange.support/header/addressing}ReplyTo"));

    public static final String BODY_CONTENT_PATH =
            joinStringStack(List.of(ENVELOPE_QNAME, "{http://exchange.support/envelope}Body"));
}
