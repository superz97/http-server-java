package com.github.superz97;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main( String[] args ) {
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

                String requestLine = bufferedReader.readLine();

                OutputStream outputStream = clientSocket.getOutputStream();

                if (requestLine != null && !requestLine.isEmpty()) {
                    String[] requestPaths = requestLine.split(" ");

                    String path = requestPaths[1];

                    if (path.equals("/")) {
                        outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                    } else {
                        outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                    }
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
