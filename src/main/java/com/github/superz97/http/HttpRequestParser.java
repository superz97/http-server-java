package com.github.superz97.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        String[] parts = requestLine.split(" ");
        HttpMethod method = HttpMethod.from(parts[0]);
        String path = parts[1];
        String version = parts[2];

        HttpHeaders headers = new HttpHeaders();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            headers.set(headerParts[0], headerParts[1]);
        }

        return new HttpRequest(method, path, version, headers);
    }

}
