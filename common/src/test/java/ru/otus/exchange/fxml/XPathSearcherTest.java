package ru.otus.exchange.fxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.otus.exchange.fxml.XPathSearcher.joinStringStack;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class XPathSearcherTest {

    @Test
    void testJoinStringStack() {
        List<String> list = List.of("a", "b");
        assertEquals("a/b", joinStringStack(list));
    }

    @Test
    void testGetValuesByPath0() throws Exception {

        for (int i = 0; i <= 1; i++) {
            try (InputStream in = XPathSearcherTest.class.getResourceAsStream("/fxml/xml" + i + ".xml")) {
                byte[] xml = new byte[Objects.requireNonNull(in).available()];
                Assertions.assertEquals(xml.length, in.read(xml));

                String searchPath1 = joinStringStack(List.of("a", "b"));

                String searchPath2 = joinStringStack(List.of("a", "c"));

                List<String> qnamePathList = List.of(searchPath1, "a", searchPath2);

                XPathSearcher searcher = new XPathSearcher(new ByteArrayInputStream(xml));

                Map<String, String> resultMap = searcher.getValuesByPath(qnamePathList);
                assertEquals("привет мир", resultMap.get(searchPath1));

                assertTrue(List.of("c", "b").contains(resultMap.get("a")));
                assertEquals("\n\n\n    ", resultMap.get(searchPath2));
            }
        }
    }
}
