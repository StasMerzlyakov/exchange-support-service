package ru.otus.exchange.blobutils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import ru.otus.exchange.common.Constants;

public class JsonProcessorImpl implements JsonProcessor {

    private final BlobLoader blobLoader;

    public JsonProcessorImpl(BlobLoader blobLoader) {
        this.blobLoader = blobLoader;
    }

    @Override
    public byte[] restoreJson(String exchange, String fileName) throws IOException {
        byte[] json = blobLoader.loadBlob(exchange, fileName);

        JsonFactory factory = new JsonFactory();
        JsonToken token;

        String storedComma = "";

        try (JsonParser parser = factory.createParser(json);
                StringWriter resultJsonWriter = new StringWriter()) {
            while ((token = parser.nextToken()) != null) {
                switch (token) {
                    case START_OBJECT -> {
                        resultJsonWriter.write(storedComma);
                        storedComma = "";
                        resultJsonWriter.write("{");
                    }
                    case END_OBJECT -> {
                        resultJsonWriter.write("}");
                        storedComma = ",";
                    }
                    case START_ARRAY -> {
                        resultJsonWriter.write(storedComma);
                        storedComma = "";
                        resultJsonWriter.write("[");
                    }
                    case END_ARRAY -> {
                        resultJsonWriter.write("]");
                        storedComma = ",";
                    }
                    case FIELD_NAME -> {
                        resultJsonWriter.write(storedComma);
                        storedComma = "";
                        resultJsonWriter.write("\"");
                        resultJsonWriter.write(parser.getText());
                        resultJsonWriter.write("\":");
                    }
                    case VALUE_NULL, VALUE_TRUE, VALUE_FALSE, VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT -> {
                        resultJsonWriter.write(storedComma);
                        resultJsonWriter.write(parser.getText());
                        storedComma = ",";
                    }
                    case VALUE_STRING -> {
                        resultJsonWriter.write(storedComma);
                        String text = parser.getText();
                        resultJsonWriter.write("\"");
                        if (text.startsWith(Constants.BLOB_PREFIX_BASE64)) {
                            String blobNameBase64 = text.substring(Constants.BLOB_PREFIX_BASE64.length());
                            String blobName =
                                    new String(Base64.getDecoder().decode(blobNameBase64), StandardCharsets.UTF_8);
                            var content = blobLoader.loadBlob(exchange, blobName);
                            resultJsonWriter.write(new String(content, StandardCharsets.UTF_8));
                        } else {
                            resultJsonWriter.write(text);
                        }
                        resultJsonWriter.write("\"");
                        storedComma = ",";
                    }
                    default -> throw new IOException("unexpected token");
                }
            }
            return resultJsonWriter.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
