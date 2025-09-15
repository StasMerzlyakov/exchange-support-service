package ru.otus.exchange.blobutils;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.exchange.common.Constants;

class XmlProcessorImplTest {

    @Test
    void test1() throws IOException {
        var inputXmlTemplate =
                """
                <env:Envelope xmlns:env="http://exchange.support/envelope" xmlns:add="http://exchange.support/header/addressing"
                 xmlns:faul="http://exchange.support/utility/fault" xmlns:not="http://exchange.support/utility/notification"
                 xmlns:exc="http://exchange.support/messages/exchange">
                  <env:Header>
                    <add:Addressing>
                      <add:MessageID>2222222222222222222222222</add:MessageID>
                      <add:From>123456789</add:From>
                      <add:To>333444555</add:To>
                    </add:Addressing>
                  </env:Header>
                  <env:Body>
                    <exc:ExchangeMessage>
                      <exc:PersonDataType>
                        <exc:FirstName>Иванов</exc:FirstName>
                        <exc:LastName>Иван</exc:LastName>
                        <exc:Patronymic>Иванович</exc:Patronymic>
                        <exc:BirthDate>
                          <exc:year>2000</exc:year>
                          <exc:month>--01</exc:month>
                          <exc:day>---15</exc:day>
                        </exc:BirthDate>
                        <exc:Gender>М</exc:Gender>
                      </exc:PersonDataType>
                      <exc:Photo>%s</exc:Photo>
                    </exc:ExchangeMessage>
                  </env:Body>
                </env:Envelope>
                """;

        String photoData = "dGVzdHBob3RvdGVzdHBob3RvdGVzdHBob3Rv";
        String inputXml = String.format(inputXmlTemplate, photoData);

        String photoFile = "abcd";
        String photoFileNameB64 = Base64.getEncoder().encodeToString(photoFile.getBytes(StandardCharsets.UTF_8));

        String blobRef = Constants.BLOB_PREFIX_BASE64 + photoFileNameB64;
        String expectedXml = String.format(inputXmlTemplate, blobRef);

        BlobSaver blobSaver = Mockito.mock(BlobSaver.class);
        String fileName = "xml";

        XmlProcessorImpl xmlProcessor = new XmlProcessorImpl(blobSaver);

        String exchange = "exchange";
        String envNamespace = "http://exchange.support/envelope";
        String excNamespace = "http://exchange.support/messages/exchange";
        Set<String> blobPaths = Set.of(String.format(
                "{%1$s}Envelope/{%1$s}Body/" + "{%2$s}ExchangeMessage/{%2$s}Photo", envNamespace, excNamespace));

        var photoDataBA = photoData.getBytes(StandardCharsets.UTF_8);
        when(blobSaver.saveBlob(eq(exchange), aryEq(photoDataBA))).thenReturn(photoFile);

        xmlProcessor.storeXml(exchange, inputXml.getBytes(StandardCharsets.UTF_8), blobPaths, fileName);
        verify(blobSaver, times(1)).saveBlob(eq(exchange), aryEq(photoDataBA));

        var expectedXmlBA = expectedXml.getBytes(StandardCharsets.UTF_8);
        verify(blobSaver, times(1)).saveBlob(eq(exchange), eq(fileName), aryEq(expectedXmlBA));
    }
}
