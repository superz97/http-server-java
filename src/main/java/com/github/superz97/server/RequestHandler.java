package com.github.superz97.server;

import com.github.superz97.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;
    private final String directory;

    public RequestHandler(Socket clientSocket, String directory) {
        this.clientSocket = clientSocket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            HttpRequest request = HttpRequestParser.parse(reader);
            if (request != null) {
                HttpResponse response = route(request);
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

    private HttpResponse route(HttpRequest request) {
        String path = request.path();

        if (path.equals("/")) {
            return HttpResponse.builder().status(HttpStatus.OK).build();
        }

        if (path.startsWith("/echo/")) {
            String body = path.substring("/echo/".length());
            return HttpResponse.builder().status(HttpStatus.OK).header("Content-Type", "text/plain").body(body).build();
        }

        if (path.equals("/user-agent")) {
            HttpHeaders headers = request.headers();
            String userAgent = headers.get("User-Agent");
            return HttpResponse.builder().status(HttpStatus.OK).header("Content-Type", "text/plain").body(userAgent).build();
        }

        if (path.startsWith("/files/")) {
            return handleFileRequest(path.substring("/files/".length()));
        }

        return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
    }

    private HttpResponse handleFileRequest(String filename) {
        Path filePath = Paths.get(directory, filename);

        if (!Files.isRegularFile(filePath)) {
            return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
        }
        try {
            byte[] content = Files.readAllBytes(filePath);
            return HttpResponse.builder().header("Content-Type", "application/octet-stream").status(HttpStatus.OK).body(content).build();
        } catch (IOException e) {
            return HttpResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
