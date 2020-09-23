package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {
    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        HttpClient client = makeEchoRequest("/echo");

        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        HttpClient client = makeEchoRequest("/echo?status=404");

        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReadResponseHeader() throws IOException {
        HttpClient client = makeEchoRequest("/echo?body=Hello");

        assertEquals("5", client.getResponseHeader("Content-Length"));
    }

    private HttpClient makeEchoRequest(String s) throws IOException {
        return new HttpClient("urlecho.appspot.com", 80, s);
    }

    @Test
    void shouldReadResponseBody() throws IOException {
        HttpClient client = makeEchoRequest("/echo?body=Hello");
        assertEquals("Hello", client.getResponseBody());
    }
}