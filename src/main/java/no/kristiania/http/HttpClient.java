package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private int statusCode = 200;
    private Map<String, String> requestHeaders = new HashMap<>();
    private Map<String, String> responseHeaders = new HashMap<>();
    private String responseBody = "";
    private String startLine = "";

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;

    public HttpClient(final String hostName, int port, final String requestTarget) throws IOException {

        this.startLine = "GET " + requestTarget + " HTTP/1.1";
        this.socket = new Socket(hostName, port);
        requestHeaders.put("Host", hostName);

        String request = startLine + "\r\n"
                + "Host: " + hostName + "\r\n"
                + "\r\n";

        executeRequest(socket);
        handleResponse();
    }

    /*public static void main(String[] args) throws IOException {
    new HttpClient("urlecho.appspot.com", 80, "/echo?body=Hello+World");
    }*/

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpMessage executeRequest(Socket socket) throws IOException {
        HttpMessage request = new HttpMessage();

        for(Map.Entry<String, String> header : requestHeaders.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
        }
        request.setStartLine(startLine);
        request.write(socket);

        return request;
        // -----
    }

    public HttpMessage handleResponse() throws IOException {
        HttpMessage response = new HttpMessage();

        String responseLine = HttpMessage.readLine(socket);
        response.setStartLine(responseLine);
        System.out.println("responseline: " + responseLine + "=" + response.getStartLine());

        String[] responseLineParts = response.getStartLine().split(" ");
        response.setCode(responseLineParts[1]);
        System.out.println(statusCode);

        String headerLine;
        while(!(headerLine = HttpMessage.readLine(socket)).isEmpty()){
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            response.setHeader(headerName, headerValue);
        }

        int contentLength = Integer.parseInt(response.getHeader("Content-Length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }

        this.responseBody = body.toString();
        System.out.println("\r\r\r" + responseBody);
        return response;
    }


}
