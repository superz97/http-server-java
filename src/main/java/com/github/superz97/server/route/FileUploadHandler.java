package com.github.superz97.server.route;

import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class FileUploadHandler implements RouteHandler {

    private final String directory;
    private final Set<String> allowedExtensions;

    public FileUploadHandler(String directory, Set<String> allowedExtensions) {
        this.directory = directory;
        this.allowedExtensions = allowedExtensions;
    }

    @Override
    public HttpResponse handle(HttpRequest request, String param) {
        String extension = extractExtension(param);
        if (extension == null || !allowedExtensions.contains(extension)) {
            return HttpResponse.builder().status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        Path filePath;
        try {
            filePath = SafeFileResolver.resolve(directory, param);
        } catch (IllegalArgumentException e) {
            return HttpResponse.builder().status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Files.write(filePath, request.body());
        } catch (IOException e) {
            return HttpResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return HttpResponse.builder().status(HttpStatus.CREATED).header("Location", "/files/" + param).build();
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

}
