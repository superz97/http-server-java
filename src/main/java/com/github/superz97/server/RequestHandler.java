package com.github.superz97.server;

import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpRequestParser;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.server.route.Router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;
    private final Router router;

    public RequestHandler(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            HttpRequest request = HttpRequestParser.parse(reader);
            if (request != null) {
                HttpResponse response = router.handle(request);
                response.writeTo(clientSocket.getOutputStream());
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("IOException on close: " + e.getMessage());
            }
        }
    }

}
