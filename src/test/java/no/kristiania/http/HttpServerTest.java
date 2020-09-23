package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {

        HttpServer server = new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        HttpServer server = new HttpServer(10002);
        HttpClient client = new HttpClient("localhost", 10002, "echo/?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReadResponseCode() throws IOException{
        HttpServer server = new HttpServer(0);
        server.start();
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", port, "");
        HttpResponse response = client.executeRequest();
        assertEquals(200, response.getResponseCode());
    }

}
