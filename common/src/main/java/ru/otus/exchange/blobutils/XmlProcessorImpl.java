package ru.otus.exchange.blobutils;

import static ru.otus.exchange.fxml.IXmlParser.TOKEN_TYPE.END;
import static ru.otus.exchange.fxml.XPathSearcher.joinStringStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import ru.otus.exchange.common.Constants;
import ru.otus.exchange.fxml.IXmlParser;
import ru.otus.exchange.fxml.NamespaceResolverXMLParser;

public class XmlProcessorImpl implements XmlProcessor {

    private final BlobSaver blobSaver;

    public XmlProcessorImpl(BlobSaver blobSaver) {
        this.blobSaver = blobSaver;
    }

    @Override
    public void storeXml(String exchange, byte[] xml, Set<String> blobPaths, String fileName) throws IOException {
        List<String> stackPath = new LinkedList<>();

        IXmlParser.TOKEN_TYPE tokenType;

        String currentQName = null;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml);
                StringWriter resultXmlWriter = new StringWriter()) {
            NamespaceResolverXMLParser parser = new NamespaceResolverXMLParser(byteArrayInputStream);

            while ((tokenType = parser.nextToken()) != END) {
                switch (tokenType) {
                    case START_TAG:
                        currentQName = new QName(parser.getElementNamespace(), parser.getElementLocalName()).toString();
                        stackPath.add(currentQName);
                        resultXmlWriter.write(parser.getTagInternal());
                        break;
                    case TEXT:
                        String currentPath = joinStringStack(stackPath);
                        if (blobPaths.contains(currentPath)) {
                            String blobContent = new String(parser.getTagInternal());
                            String name = blobSaver.saveBlob(exchange, blobContent.getBytes(StandardCharsets.UTF_8));
                            // store ref
                            String base64Ref = Constants.BLOB_PREFIX_BASE64
                                    + Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
                            resultXmlWriter.write(base64Ref);
                        } else {
                            resultXmlWriter.write(parser.getTagInternal());
                        }
                        break;
                    case END_TAG:
                        stackPath.removeLast();
                        resultXmlWriter.write(parser.getTagInternal());
                        break;
                    default:
                        resultXmlWriter.write(parser.getTagInternal());
                        break;
                }
            }

            var xmlContent = resultXmlWriter.toString();
            blobSaver.saveBlob(exchange, fileName, xmlContent.getBytes(StandardCharsets.UTF_8));
        }
    }
}
