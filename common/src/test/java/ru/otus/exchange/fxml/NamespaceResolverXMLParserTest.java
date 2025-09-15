package ru.otus.exchange.fxml;

import static ru.otus.exchange.fxml.IXmlParser.TOKEN_TYPE.END;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NamespaceResolverXMLParserTest {

    @Test
    void doElementPathTest1() throws IOException {

        String inXml = "<simpleType xmlns=\"a\" name=\"SignatureValueType\"/>";
        ByteArrayInputStream bin = new ByteArrayInputStream(inXml.getBytes(StandardCharsets.UTF_8));
        NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(bin);

        IXmlParser.TOKEN_TYPE tokenType;

        String expected = "/{a}simpleType";

        while ((tokenType = parser.nextToken()) != END) {
            switch (tokenType) {
                case START_TAG:
                    String path = parser.getCurrentPath();
                    Assertions.assertEquals(expected, path);
                    break;
                case END_TAG:
                    path = parser.getCurrentPath();
                    Assertions.assertEquals(expected, path);
                    break;
                default:
                    throw new IOException("unexpected token");
            }
        }
    }

    @Test
    void doElementPathTest2() throws IOException {

        String inXml =
                "<n:n1 xmlns:n=\"n\"><simpleType xmlns=\"a\" name=\"SignatureValueType\">asdsd</simpleType></n:n1>";
        ByteArrayInputStream bin = new ByteArrayInputStream(inXml.getBytes(StandardCharsets.UTF_8));
        NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(bin);

        IXmlParser.TOKEN_TYPE tokenType;

        String expectedPath = "/{n}n1/{a}simpleType";
        while ((tokenType = parser.nextToken()) != END) {
            switch (tokenType) {
                case START_TAG, END_TAG:
                    if ("simpleType".equals(parser.getElementLocalName())) {
                        String path = parser.getCurrentPath();
                        Assertions.assertEquals(expectedPath, path);
                    }
                    break;
                case TEXT:
                    String expectedText = "asdsd";
                    Assertions.assertEquals(expectedText, new String(parser.getTagInternal()));
                    String path = parser.getCurrentPath();
                    Assertions.assertEquals(expectedPath, path);
                    break;
                default:
                    throw new IOException("unexpected token");
            }
        }
    }

    @Test
    void doElementPathTest3() throws IOException {
        String inXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><n:n1 xmlns:n=\"n\"><simpleType xmlns=\"a\" name=\"SignatureValueType\">asdsd</simpleType></n:n1>";
        ByteArrayInputStream bin = new ByteArrayInputStream(inXml.getBytes(StandardCharsets.UTF_8));
        NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(bin);

        IXmlParser.TOKEN_TYPE tokenType;

        String expectedPath = "/{n}n1/{a}simpleType";
        while ((tokenType = parser.nextToken()) != END) {
            switch (tokenType) {
                case START_TAG, END_TAG:
                    if ("simpleType".equals(parser.getElementLocalName())) {
                        String path = parser.getCurrentPath();
                        Assertions.assertEquals(expectedPath, path);
                    }
                    break;
                case TEXT:
                    String expectedText = "asdsd";
                    Assertions.assertEquals(expectedText, new String(parser.getTagInternal()));
                    String path = parser.getCurrentPath();
                    Assertions.assertEquals(expectedPath, path);
                    break;
                case XMLDECL:
                    break;
                default:
                    throw new IOException("unexpected token");
            }
        }
    }

    @Test
    void doElementPathTest4() throws IOException {

        String inXml = "<n:n1><simpleType xmlns=\"a\" name=\"SignatureValueType\">asdsd</simpleType></n:n1>";
        ByteArrayInputStream bin = new ByteArrayInputStream(inXml.getBytes(StandardCharsets.UTF_8));
        NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(bin);

        Assertions.assertThrows(IOException.class, parser::nextToken);
    }

    @Test
    void doElementPathTest5() throws IOException {

        InputStream in = CharArrXMLParserTest.class.getResourceAsStream("/fxml/1000000032016050600000020.xml");
        Assertions.assertNotNull(in);
        NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(in);

        Assertions.assertDoesNotThrow(() -> {
            while (parser.nextToken() != END) {
                // just read xml
            }
        });
    }
}
