package org.dstadler.commoncrawl.elasticsearch;

import org.dstadler.commoncrawl.elasticsearch.ElasticsearchWriter;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.http.HttpClientWrapper;
import org.dstadler.commons.testing.MockRESTServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElasticsearchWriterTest {
    private final AtomicInteger called = new AtomicInteger();
    private MockRESTServer server;

    @BeforeEach
    public void setUp() throws IOException {
        server = new MockRESTServer(
                called::incrementAndGet,
                "200", "application/json", "{\"acknowledged\":true}");
    }

    @AfterEach
    public void tearDown() {
        if(server != null) {
            server.close();
        }
    }

    @Test
    public void sendDocuments() throws IOException, InterruptedException {
        try (HttpClientWrapper httpClient = new HttpClientWrapper("", null, 60_000)) {
            POIStatus o = new POIStatus();
            o.setFilename("test");
            ElasticsearchWriter.sendDocuments("http://localhost:" + server.getPort() + "/",
                    httpClient, Collections.singletonList(o));
            assertEquals(1, called.get());
        }
    }

    @Test
    public void sendDocumentWithRetry() throws IOException, InterruptedException {
        try (HttpClientWrapper httpClient = new HttpClientWrapper("", null, 60_000)) {
            ElasticsearchWriter.sendDocumentWithRetry("http://localhost:" + server.getPort() + "/",
                    httpClient, new StringBuilder("{}"));
            assertEquals(1, called.get());
        }
    }

    @Test
    public void sanitize() {
        assertEquals("", ElasticsearchWriter.sanitize(""));
        assertEquals("abc", ElasticsearchWriter.sanitize("abc"));
        assertEquals("___", ElasticsearchWriter.sanitize("/\\_"));
        assertEquals("___", ElasticsearchWriter.sanitize("[\\]"));
        assertEquals("___", ElasticsearchWriter.sanitize("?&="));
    }

    @Test
    public void sendDocument() throws IOException {
        try (HttpClientWrapper httpClient = new HttpClientWrapper("", null, 60_000)) {
            ElasticsearchWriter.sendDocument(httpClient.getHttpClient(), "http://localhost:" + server.getPort(), "{}");
            assertEquals(1, called.get());
        }
    }

    @Test
    public void setupTemplate() throws IOException {
        ElasticsearchWriter.setupTemplate("http://localhost:" + server.getPort(), "", null);
        assertEquals(1, called.get());
    }
}