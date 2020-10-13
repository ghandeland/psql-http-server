package no.kristiania.http;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HttpServer {

    private final ServerSocket serverSocket;
    private ServerThread serverThread;
    private File documentRoot = new File("src/main/resources");
    private ProjectMemberDao projectMemberDao;

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
    }

    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
        this.projectMemberDao = new ProjectMemberDao(dataSource);
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    handleRequest(socket);
                } catch (IOException | SQLException e) {
                    //e.printStackTrace();
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

    private void handleRequest(Socket socket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage();
        HttpMessage request = new HttpMessage();

        String requestLine = HttpMessage.readLine(socket);
        System.out.println(requestLine);

        String[] requestLineParts = requestLine.split(" ");

        String requestType = requestLineParts[0];

        String requestTarget = requestLineParts[1];


        if(requestType.equals("POST")) {
            request.readAndSetHeaders(socket);
            int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
            String body = request.readBody(socket, contentLength);
            request.setBody(body);

            System.out.println(body);

            Map<String, String> projectMemberMap = new HashMap<>();
            String[] queryParameters = body.split("&");
            for (String parameter : queryParameters) {
                String[] parameterPair = parameter.split("=");
                projectMemberMap.put(parameterPair[0], parameterPair[1]);
            }

            String projectMemberName = java.net.URLDecoder.decode(projectMemberMap.get("name"), StandardCharsets.ISO_8859_1.name());
            String projectMemberRole = java.net.URLDecoder.decode(projectMemberMap.get("role"), StandardCharsets.ISO_8859_1.name());

            System.out.println("PROJECTMEMBERNAME: " + projectMemberName + "PROJECTMEMBERROLE: " + projectMemberRole);
            ProjectMember projectMember = new ProjectMember(projectMemberName, projectMemberRole);

            projectMemberDao.insert(projectMember);

            response.setCode("200");
            response.setStartLine("HTTP/1.1 " + response.getCode() + " OK");
            response.write(socket);
        }

        int questionPos = requestTarget.indexOf('?');

        if(requestTarget.equals("") || requestTarget.equals("/")) {
            response.setBody("Hello world");
        } else if (questionPos != -1) {
            String queryString = requestTarget.substring(questionPos + 1);

            String[] queryParameters = queryString.split("&");
            for (String parameter : queryParameters) {
                String[] parameterPair = parameter.split("=");
                request.setHeader(parameterPair[0], parameterPair[1]);

            }
        } else if (!requestTarget.contains("echo")) {
            File targetFile = new File(documentRoot, requestTarget);
            if(!targetFile.exists()) {
                response.setCode("404");
                response.setStartLine("HTTP/1.1 " + response.getCode() + " NOT FOUND");
                response.writeLine(socket, response.getStartLine());
                response.writeLine(socket, "");
                return;
            }

            String contentType = "text/html";
            if(targetFile.getName().endsWith(".txt")){
                contentType = "text/plain";
            } else if(targetFile.getName().endsWith(".css")) {
                contentType = "text/css";
            }

            response.setCode("200");
            response.setStartLine("HTTP/1.1 " + response.getCode() + " OK");
            response.setHeader("Content-Type", contentType);

            response.setHeader("Content-Length", ""+targetFile.length());
            response.writeWithFile(socket, targetFile);
            return;
        }

        if (request.getHeader("body") != null) {
            String requestBody = request.getHeader("body");
            response.setBody(requestBody);
        } else {
            response.setBody("Hello World");
        }

        if (request.getHeader("Location") != null) {
            response.setHeader("Location", request.getHeader("Location"));
        }

        if (request.getHeader("status") != null) {
            response.setCode(request.getHeader("status"));
        } else {
            response.setCode("200");
        }

        response.setStartLine("HTTP/1.1 " + response.getCode() + " OK");
        response.setHeader("Content-Length", Integer.toString(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.write(socket);
    }

    public static void main(String[] args) throws IOException {
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setUrl("jdbc:postgresql://localhost:5432/project");
        pgDataSource.setUser("oppgavesett08");
        pgDataSource.setPassword("nw3fGmA9nKgbwtGwpj");

        HttpServer server = new HttpServer(8080, pgDataSource);

        server.start();
    }


}
