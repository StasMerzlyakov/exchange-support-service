package ru.otus.exchange.fxml;

import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.exchange.fxml.IXmlParser.TOKEN_TYPE.END;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CharArrXMLParserTest {

    @Test
    void testResetXmlDeclAfterEncodingFinding() throws IOException {
        try (InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/xml0.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));
            CharArrXMLParser parser = new CharArrXMLParser(new ByteArrayInputStream(xml));
            assertEquals(IXmlParser.TOKEN_TYPE.XMLDECL, parser.nextToken());
            assertArrayEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>".toCharArray(), parser.getTagInternal());
        }
    }

    @Test
    void testResetFirstTagAfterEncodingFinding() throws IOException {
        try (InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/xml1.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));
            CharArrXMLParser parser = new CharArrXMLParser(new ByteArrayInputStream(xml));
            assertEquals(IXmlParser.TOKEN_TYPE.START_TAG, parser.nextToken());
            assertArrayEquals("<a id=\"1\">".toCharArray(), parser.getTagInternal());
        }
    }

    @Test
    void testEncodingFinding() throws Exception {
        try (InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/xml1.xml")) {
            byte[] xml = new byte[Objects.requireNonNull(in).available()];
            Assertions.assertEquals(xml.length, in.read(xml));
            assertEquals(
                    "UTF-8",
                    CharArrXMLParser.getEncoding(new ByteArrayInputStream(xml)).toUpperCase());
        }
    }

    @Test
    void test9000000032019042499957320() throws IOException {
        InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/9000000032019042499957320.xml");
        byte[] xml = new byte[Objects.requireNonNull(in).available()];
        Assertions.assertEquals(xml.length, in.read(xml));
        CharArrXMLParser parser = new CharArrXMLParser(new ByteArrayInputStream(xml));
        assertDoesNotThrow(() -> {
            while ((parser.nextToken()) != END) {
                // do nothing
            }
        });
    }

    @Test
    void testGoznakNotif() throws IOException {
        InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/goznak_notif.xml");
        byte[] xml = new byte[Objects.requireNonNull(in).available()];
        Assertions.assertEquals(xml.length, in.read(xml));
        CharArrXMLParser parser = new CharArrXMLParser(new ByteArrayInputStream(xml));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bout, StandardCharsets.UTF_8);

        while (parser.nextToken() != END) {
            char[] tagInternal = parser.getTagInternal();
            writer.write(tagInternal);
        }

        writer.flush();

        assertArrayEquals(xml, bout.toByteArray());
        writer.close();
        bout.close();
        in.close();
    }

    @Test
    void testGoznakFail() throws IOException {
        InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/goznak_fail.xml");
        byte[] xml = new byte[Objects.requireNonNull(in).available()];
        Assertions.assertEquals(xml.length, in.read(xml));

        ByteArrayOutputStream bout = getByteArrayOutputStream(xml);
        in.close();
        String result = bout.toString(StandardCharsets.UTF_8);
        assertEquals(
                "&#x41F;&#x43E;&#x432;&#x442;&#x43E;&#x440;&#x43D;&#x43E;&#x435; &#x437;&#x430;&#x434;&#x430;&#x43D;&#x438;&#x435; &#x43D;&#x430; &#x43F;&#x435;&#x447;&#x430;&#x442;&#x44C; &#x441; MessageId 9000000032019043052150923 &#x431;&#x443;&#x434;&#x435;&#x442; &#x43F;&#x440;&#x43E;&#x438;&#x433;&#x43D;&#x43E;&#x440;&#x438;&#x440;&#x43E;&#x432;&#x430;&#x43D;&#x43E;.",
                result);
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(byte[] xml) throws IOException {
        CharArrXMLParser parser = new CharArrXMLParser(new ByteArrayInputStream(xml));
        IXmlParser.TOKEN_TYPE type = null;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bout, StandardCharsets.UTF_8);
        while ((type = parser.nextToken()) != END) {
            char[] tagInternal = parser.getTagInternal();
            if (Objects.requireNonNull(type) == IXmlParser.TOKEN_TYPE.TEXT) {
                writer.write(tagInternal);
            }
        }

        writer.flush();
        writer.close();
        bout.close();
        return bout;
    }

    @Test
    void testReadReferences() throws IOException {

        InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/xml5.xml");
        byte[] xml = new byte[Objects.requireNonNull(in).available()];
        Assertions.assertEquals(xml.length, in.read(xml));
        CharArrXMLParser parser = new CharArrXMLParser(new ByteArrayInputStream(xml));
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bout, StandardCharsets.UTF_8);
        while ((parser.nextToken()) != END) {
            char[] tagInternal = parser.getTagInternal();
            writer.write(tagInternal);
        }

        writer.flush();
        writer.close();
        bout.close();
        assertArrayEquals(xml, bout.toByteArray());
    }
}
