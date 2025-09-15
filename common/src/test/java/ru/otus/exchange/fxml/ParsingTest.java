package ru.otus.exchange.fxml;

import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.exchange.fxml.IXmlParser.TOKEN_TYPE.END;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class ParsingTest {

    @Test
    void parseXMLWithChildFormYaml() throws IOException {

        byte[] xml;
        NamespaceResolverXMLParser parser;
        FileInputStream xmlInputStream = new FileInputStream(this.getClass()
                .getClassLoader()
                .getResource("fxml/child_anket_yaml_4t.xml")
                .getFile());
        xml = new byte[xmlInputStream.available()];
        assertEquals(xmlInputStream.available(), xmlInputStream.read(xml));
        xmlInputStream.close();
        parser = new NamespaceResolverXMLParser(new ByteArrayInputStream(xml));

        assertNotNull(parser);
        int readTokens = 0;
        while (parser.nextToken() != END) {
            readTokens++;
        }
        assertTrue(readTokens > 0);
    }

    @Test
    void parseFormattedXMLWithChildFormYaml() throws IOException {
        byte[] xml;
        NamespaceResolverXMLParser parser;
        FileInputStream xmlInputStream = new FileInputStream(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("fxml/child_anket_yaml_4t_f.xml"))
                        .getFile());
        xml = new byte[xmlInputStream.available()];
        assertEquals(xmlInputStream.available(), xmlInputStream.read(xml));
        xmlInputStream.close();
        parser = new NamespaceResolverXMLParser(new ByteArrayInputStream(xml));

        assertNotNull(parser);
        int readTokens = 0;
        while (parser.nextToken() != END) {
            readTokens++;
        }
        assertTrue(readTokens > 0);
    }

    private String getSuffix(String name) {
        String[] split = name.split(":");
        if (split.length > 1) {
            return split[1];
        }
        return null;
    }

    private String getPrefix(String name) {
        String[] split = name.split(":");
        if (split.length > 1) {
            return split[0];
        }
        return "";
    }

    @Test
    void checkXMLNSProcessing() throws Exception {
        FileInputStream xmlInputStream = new FileInputStream(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("fxml/xml4.xml"))
                        .getFile());
        byte[] xml = new byte[xmlInputStream.available()];
        assertEquals(xmlInputStream.available(), xmlInputStream.read(xml));
        xmlInputStream.close();
        NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(new ByteArrayInputStream(xml));
        IXmlParser.TOKEN_TYPE tokenType;
        while ((tokenType = parser.nextToken()) != END) {

            if (Objects.requireNonNull(tokenType) == IXmlParser.TOKEN_TYPE.START_TAG
                    && parser.getElementLocalName().equals("b")) {
                assertEquals("http://b", parser.getElementNamespace());
                for (HashMap.Entry<String, LinkedList<String>> entry :
                        parser.getAttributeMap().entrySet()) {
                    String attrSuffix = getSuffix(entry.getKey());
                    String prefix = getPrefix(entry.getKey());
                    if (Objects.equals(attrSuffix, "id")) {
                        String namespace = ((LinkedList<?>)
                                        parser.getAttributeMap().get("xmlns:" + prefix))
                                .getFirst()
                                .toString();
                        // проверяем что для атрибут правильно определилось пространство имени
                        assertEquals("http://b", namespace);
                    }
                }
            }
        }
    }
}
