package no.kristiania.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReadResponseCode() throws IOException {
        HttpServer server = new HttpServer(10000);
        server.start();
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", 10000, "");
        HttpMessage response = client.executeRequest();
        assertEquals("200", response.getCode());
    }

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        HttpServer server = new HttpServer(10001);
        server.start();
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        HttpMessage response = client.executeRequest();
        assertEquals("200", response.getCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {

        HttpServer server = new HttpServer(10002);
        server.start();
        HttpClient client = new HttpClient("localhost", 10002, "echo/?status=404");
        HttpMessage response = client.executeRequest();
        assertEquals("404", response.getCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldParseRequestParameters() throws IOException {

        HttpServer server = new HttpServer(10004);
        server.start();
        HttpClient client = new HttpClient("localhost", 10004, "echo/?status=401");
        HttpMessage response = client.executeRequest();
        assertEquals("401", response.getCode());
        client.closeSocket();
        server.stop();
    }


    @Test
    void shouldParseRequestParametersWithLocation() throws IOException {
        HttpServer server = new HttpServer(10005);
        server.start();
        HttpClient client = new HttpClient("localhost", 10005, "?status=302&Location=http://www.example.com");
        HttpMessage response = client.executeRequest();
        assertEquals("http://www.example.com", response.getHeader("Location"));
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldParseRequestParametersWithBody() throws IOException {
        HttpServer server = new HttpServer(10006);
        server.start();
        HttpClient client = new HttpClient("localhost", 10006, "?body=HelloWorld");
        HttpMessage response = client.executeRequest();
        assertEquals("HelloWorld", response.getBody());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldReturnFileOnDisk() throws IOException {
        HttpServer server = new HttpServer(10013);
        server.start();

        File documentRoot = new File("target");
        server.setDocumentRoot(documentRoot);
        String fileContent = "Test " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10013, "/test.txt");
        HttpMessage response = client.executeRequest();

        assertEquals(fileContent, response.getBody());

        client.closeSocket();
        server.stop();
    }


    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10014);
        server.start();

        server.setDocumentRoot(new File("target"));
        HttpClient client = new HttpClient("localhost", 10014, "/nonexistingFile.txt");

        HttpMessage response = client.executeRequest();

        assertEquals("404", response.getCode());

        client.closeSocket();
        server.stop();

    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10015);
        server.start();

        File documentRoot = new File("target");
        server.setDocumentRoot(documentRoot);

        Files.writeString(new File(documentRoot, "index.html").toPath(), "<html>Hello world</html>");
        Files.writeString(new File(documentRoot, "test.txt").toPath(), "Hello world");

        HttpClient client1 = new HttpClient("localhost", 10015, "/test.txt");
        HttpMessage response1 = client1.executeRequest();
        client1.closeSocket();

        assertEquals("text/plain", response1.getHeader("Content-Type"));

        HttpClient client2 = new HttpClient("localhost", 10015, "/index.html");
        HttpMessage response2 = client2.executeRequest();
        client1.closeSocket();

        assertEquals("text/html", response2.getHeader("Content-Type"));

        server.stop();
    }
}
