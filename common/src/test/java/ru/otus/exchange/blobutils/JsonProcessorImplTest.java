package ru.otus.exchange.blobutils;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JsonProcessorImplTest {

    @Test
    @DisplayName("restore blob test")
    void test1() throws IOException {
        BlobLoader blobLoader = Mockito.mock(BlobLoader.class);

        String exchange = "exchange";
        String jsonName = "json";

        String testData = "dGVzdHBob3RvdGVzdHBob3RvdGVzdHBob3Rv";
        String photoFile = "abcd";
        String photoFileNameB64 = Base64.getEncoder().encodeToString(photoFile.getBytes(StandardCharsets.UTF_8));

        String jsonTemplate =
                """
                {
                    "name":"Вася",
                    "photo":"%s"

                }
                """;

        String testJson = String.format(jsonTemplate, "QkxPQlo6" + photoFileNameB64);

        when(blobLoader.loadBlob(exchange, jsonName)).thenReturn(testJson.getBytes(StandardCharsets.UTF_8));
        when(blobLoader.loadBlob(exchange, photoFile)).thenReturn(testData.getBytes(StandardCharsets.UTF_8));

        JsonProcessorImpl jsonProcessor = new JsonProcessorImpl(blobLoader);

        String expectedJson = String.format(jsonTemplate, testData);
        byte[] actualJsonBa = jsonProcessor.restoreJson(exchange, jsonName);
        String actualJson = new String(actualJsonBa, StandardCharsets.UTF_8);

        assertThatJson(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("parse json test")
    void test2() throws IOException {
        BlobLoader blobLoader = Mockito.mock(BlobLoader.class);

        String exchange = "exchange";
        String jsonName = "json";

        String expectedJson =
                """
                {
                    "Actors": [
                      {
                        "name": "Tom Cruise",
                        "age": 56,
                        "Born At": "Syracuse, NY",
                        "Birthdate": "July 3, 1962",
                        "photo": "https://jsonformatter.org/img/tom-cruise.jpg",
                        "wife": null,
                        "weight": 67.5,
                        "hasChildren": true,
                        "hasGreyHair": false,
                        "children": [
                          "Suri",
                          "Isabella Jane",
                          "Connor"
                        ]
                      },
                      {
                        "name": "Robert Downey Jr.",
                        "age": 53,
                        "Born At": "New York City, NY",
                        "Birthdate": "April 4, 1965",
                        "photo": "https://jsonformatter.org/img/Robert-Downey-Jr.jpg",
                        "wife": "Susan Downey",
                        "weight": 77.1,
                        "hasChildren": true,
                        "hasGreyHair": false,
                        "children": [
                          "Indio Falconer",
                          "Avri Roel",
                          "Exton Elias"
                        ]
                      },
                      [
                        "test",
                        null,
                        1,
                        56.7,
                        {
                          "name": [
                            "abc",
                            [
                              1,
                              2,
                              3
                            ]
                          ]
                        }
                      ]
                    ]
                  }
                """;

        when(blobLoader.loadBlob(exchange, jsonName)).thenReturn(expectedJson.getBytes(StandardCharsets.UTF_8));

        JsonProcessorImpl jsonProcessor = new JsonProcessorImpl(blobLoader);

        byte[] actualJsonBa = jsonProcessor.restoreJson(exchange, jsonName);
        String actualJson = new String(actualJsonBa, StandardCharsets.UTF_8);

        assertThatJson(actualJson).isEqualTo(expectedJson);
    }
}
