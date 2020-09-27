package no.kristiania.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReadResponseCode() throws IOException {
        HttpServer server = new HttpServer(0);
        server.start();
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", port, "");
        HttpMessage response = client.executeRequest();
        assertEquals(200, response.getCode());
    }

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        HttpServer server = new HttpServer(10001);
        server.start();
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {

        HttpServer server = new HttpServer(10002);
        server.start();
        HttpClient client = new HttpClient("localhost", 10002, "echo/?status=404");
        HttpMessage response = client.executeRequest();
        assertEquals(404, response.getCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldParseRequestParameters() throws IOException {

        HttpServer server = new HttpServer(10004);
        server.start();
        HttpClient client = new HttpClient("localhost", 10004, "echo/?status=401");
        HttpMessage response = client.executeRequest();
        assertEquals(401, response.getCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldParseRequestParametersWithLocation() throws IOException {
        HttpServer server = new HttpServer(10005);
        server.start();
        HttpClient client = new HttpClient("localhost", 10005, "?status=302&Location=http://www.example.com");
        HttpMessage response = client.executeRequest();
        assertEquals("http://example.com", response.getHeader("Location"));
        client.closeSocket();
        server.stop();
    }
}
