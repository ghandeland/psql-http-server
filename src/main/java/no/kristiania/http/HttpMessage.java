package no.kristiania.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {

    private Map<String, String> headers = new HashMap<>();
    private String startLine;
    private String code;
    private String body;

    public String getBody() { return body; }

    public void setBody(String body) { this.body = body; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public void setHeader(String headerName, String headerValue) { headers.put(headerName, headerValue); }

    public String getHeader(String key) { return headers.get(key); }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public String getStartLine() { return startLine; }

    public void printAllHeadersAndBody() {
        for(Map.Entry<String, String> header : headers.entrySet()) {
            System.out.println("Key: " + header.getKey() + " Value: " + header.getValue());
        }
        System.out.println("Body:\r" + getBody());
    }

    public void write(Socket socket) throws IOException {
        writeLine(socket, startLine);
        for(Map.Entry<String, String> header : headers.entrySet()) {
            writeLine(socket, header.getKey() + ": " + header.getValue());
        }
        writeLine(socket, "");
        if(body != null) {
            socket.getOutputStream().write(getBody().getBytes());
        }
    }

    public void writeWithFile(Socket socket, File targetFile) throws IOException {
        writeLine(socket, startLine);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            writeLine(socket, header.getKey() + ": " + header.getValue());
        }
        writeLine(socket, "");

        try (FileInputStream inputStream = new FileInputStream(targetFile)) {
            inputStream.transferTo(socket.getOutputStream());
        }
    }

    public void writeLine(Socket socket, String line) throws IOException {
        socket.getOutputStream().write((line + "\r\n").getBytes());
    }

    public String getRequestTarget() {
        return startLine.split(" ")[1];
    }

    public static String readLine(Socket socket) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;

        while((c = socket.getInputStream().read()) != -1) {
            if(c == '\r') {
                socket.getInputStream().read();
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }

    public void readAndSetHeaders(Socket socket) throws IOException {
        String headerLine;
        while(!(headerLine = HttpMessage.readLine(socket)).isEmpty()){
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            setHeader(headerName, headerValue);
        }
    }

    public String readBody(Socket socket, int contentLength) throws IOException {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }

        return body.toString();
    }
}
