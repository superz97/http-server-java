package com.github.superz97.server;

import com.github.superz97.exception.PayloadTooLargeException;
import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpRequestParser;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;
import com.github.superz97.server.route.Router;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;
    private final Router router;
    private final long maxBodySize;

    public RequestHandler(Socket clientSocket, Router router, long maxBodySize) {
        this.clientSocket = clientSocket;
        this.router = router;
        this.maxBodySize = maxBodySize;
    }

    @Override
    public void run() {
        try {
            InputStream in = new BufferedInputStream(clientSocket.getInputStream());
            HttpRequest request = HttpRequestParser.parse(in, maxBodySize);
            if (request != null) {
                HttpResponse response = router.handle(request);
                response.writeTo(clientSocket.getOutputStream());
            }
        } catch (PayloadTooLargeException e) {
            try {
                HttpResponse.builder().status(HttpStatus.PAYLOAD_TOO_LARGE).build().writeTo(clientSocket.getOutputStream());
            } catch (IOException ignored) {
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
