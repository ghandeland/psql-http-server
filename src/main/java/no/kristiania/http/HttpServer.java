package no.kristiania.http;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final ServerSocket serverSocket;
    private ServerThread serverThread;

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
    }

    public void setDocumentRoot(File documentRoot) {

    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    handleRequest(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start(){ serverThread.start(); }

    public void stop() throws IOException {
        serverSocket.close();
        serverThread = null;
    }

    public int getActualPort(){
        return serverSocket.getLocalPort();
    }


    private void handleRequest(Socket socket) throws IOException {
        HttpMessage response = new HttpMessage();
        HttpMessage request = new HttpMessage();

        String requestLine = HttpMessage.readLine(socket);
        System.out.println(requestLine);
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');

        if(questionPos != -1) {
            String queryString = requestTarget.substring(questionPos + 1);

            String[] queryParameters = queryString.split("&");
            for(String parameter : queryParameters) {
                String[] parameterPair = parameter.split("=");
                request.setHeader(parameterPair[0], parameterPair[1]);

            }
        }

        String requestBody = request.getHeader("body");

        if(request.getHeader("body") != null){
            response.setBody(requestBody);
        } else{
            response.setBody("Hello World");
        }

        if(request.getHeader("Location") != null) {
            response.setHeader("Location", request.getHeader("Location"));
        }

        if(request.getHeader("status") != null) {
            response.setCode(request.getHeader("status"));
        } else {
            response.setCode("200");
        }

        response.setStartLine("HTTP/1.1 " + response.getCode() + " OK");
        response.setHeader("Content-Length", Integer.toString(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");


        /*String responseString = "HTTP/1.1 " + response.getCode() + " OK\r\n" +
                "Content-Length: 12\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Hello World!";*/

        response.write(socket);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10011);
        server.start();
        /*HttpClient client = new HttpClient("localhost", 10010, "\"?status=302&Location=http://www.example.com\"");
        client.executeRequest();
        HttpMessage response = client.executeRequest();
        client.closeSocket();
        server.stop();*/
    }

}
