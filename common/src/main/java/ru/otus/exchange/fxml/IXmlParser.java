package ru.otus.exchange.fxml;

import java.io.IOException;

/**
 *
 */
public interface IXmlParser {
    enum TOKEN_TYPE {
        END,
        START_TAG,
        END_TAG,
        TEXT,
        CDSECT,
        REFERENCE,
        PROCESSING_INSTRUCTION,
        COMMENT,
        XMLDECL,
        UNICODEDECL
        // Разные DOCTYPE
    }

    TOKEN_TYPE nextToken() throws IOException;
}
