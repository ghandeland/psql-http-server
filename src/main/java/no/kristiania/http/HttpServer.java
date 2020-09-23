package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final ServerSocket serverSocket;

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

    }

    public void start(){

        new Thread(() -> {
            try {
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getActualPort(){
        return serverSocket.getLocalPort();
    }


    private void handleRequest(Socket socket) throws IOException {
        String responseCode = "200";
        String requestLine = HttpClient.readLine(socket);

        System.out.println(requestLine);
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');
        if(questionPos != -1) {
            String queryString = requestTarget.substring(questionPos + 1);
            String[] queryPair = queryString.split("=");
            responseCode = queryPair[1];
        }


        String response = "HTTP/1.1 " + responseCode + " OK\r\n" +
                "Content-Length: 12\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Hello World!";
        socket.getOutputStream().write((response).getBytes());
    }

}
