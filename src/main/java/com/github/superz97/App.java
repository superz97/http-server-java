package com.github.superz97;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main( String[] args ) {
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        String requestLine = bufferedReader.readLine();

        Map<String, String> headers = new HashMap<>();
        while (true) {
            String headerLine = bufferedReader.readLine();
            if (headerLine == null || headerLine.isEmpty()) {
                break;
            }
            String[] parts = headerLine.split(": ");
            headers.put(parts[0].toLowerCase(), parts[1]);
        }

        OutputStream outputStream = clientSocket.getOutputStream();

        if (requestLine != null && !requestLine.isEmpty()) {
            String[] requestPaths = requestLine.split(" ");

            String path = requestPaths[1];

            if (path.equals("/")) {
                outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            } else if (path.startsWith("/echo/")) {
                String body = path.substring("/echo/".length());
                String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n" +
                        "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" + "\r\n" + body;
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            } else if (path.equals("/user-agent")) {
                String userAgent = headers.get("user-agent");
                String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n" +
                        "Content-Length: " + userAgent.getBytes(StandardCharsets.UTF_8).length + "\r\n" + "\r\n" + userAgent;
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            } else {
                outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }

        clientSocket.close();
    }
}
