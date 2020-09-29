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
    private Socket socket;

    public HttpClient(final String hostName, int port, final String requestTarget) throws IOException {

        this.startLine = "GET " + requestTarget + " HTTP/1.1";
        this.socket = new Socket(hostName, port);
        requestHeaders.put("Host", hostName);

        //executeRequest();
        //handleResponse();
    }

    public Socket getSocket() { return socket; }

    public void closeSocket() throws IOException { socket.close(); }

    public int getStatusCode() { return statusCode; }

    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpMessage executeRequest() throws IOException {
        HttpMessage request = new HttpMessage();

        for(Map.Entry<String, String> header : requestHeaders.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
            System.out.println("header: "+header.getKey() + "value: " + header.getValue());
        }

        request.setStartLine(startLine);
        request.write(socket);

        HttpMessage response = handleResponse();
        return response;
    }

    public HttpMessage handleResponse() throws IOException {
        HttpMessage response = new HttpMessage();

        String responseLine = HttpMessage.readLine(socket);
        response.setStartLine(responseLine);
        System.out.println("ResponseLine from handleResponse() return: " + responseLine);

        String[] responseLineParts = response.getStartLine().split(" ");
        response.setCode(responseLineParts[1]);
        //statusCode = Integer.parseInt(responseLineParts[1]);
        if(response.getCode().equals("404")) {
            return response;
        }
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

        response.setBody(body.toString());
        this.responseBody = body.toString();
        System.out.println("\r\r\r" + responseBody);

        return response;
    }


}
