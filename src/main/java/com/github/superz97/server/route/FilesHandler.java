package com.github.superz97.server.route;

import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesHandler implements RouteHandler {

    private final String directory;

    public FilesHandler(String directory) {
        this.directory = directory;
    }

    @Override
    public HttpResponse handle(HttpRequest request, String param) {
        Path filePath;

        try {
            filePath = SafeFileResolver.resolve(directory, param);
        } catch (IllegalArgumentException e) {
            return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
        }

        if (!Files.isRegularFile(filePath)) {
            return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
        }

        try {
            byte[] content = Files.readAllBytes(filePath);
            return HttpResponse.builder()
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/octet-stream")
                    .body(content)
                    .build();
        } catch (IOException e) {
            return HttpResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
