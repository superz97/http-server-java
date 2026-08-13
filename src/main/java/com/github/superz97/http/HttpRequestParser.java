package com.github.superz97.http;

import com.github.superz97.exception.PayloadTooLargeException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream in, long maxBodySize) throws IOException {
        String requestLine = readLine(in);
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        String[] parts = requestLine.split(" ");
        HttpMethod method = HttpMethod.from(parts[0]);
        String path = parts[1];
        String version = parts[2];

        HttpHeaders headers = new HttpHeaders();
        String headerLine;
        while ((headerLine = readLine(in))!= null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            headers.set(headerParts[0], headerParts[1]);
        }

        byte[] body = readBody(in, headers, maxBodySize);

        return new HttpRequest(method, path, version, headers, body);
    }

    private static byte[] readBody(InputStream in, HttpHeaders headers, long maxBodySize) throws IOException {
        String contentLengthValue = headers.get("Content-Length");
        if (contentLengthValue == null) {
            return new byte[0];
        }
        int contentLength = Integer.parseInt(contentLengthValue);
        if (contentLength > maxBodySize) {
            throw new PayloadTooLargeException();
        }
        return in.readNBytes(contentLength);
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();
        int current;
        int previous = -1;
        while ((current = in.read()) != -1) {
            if (previous == '\r' && current == '\n') {
                byte[] bytes = lineBytes.toByteArray();
                return new String(bytes, 0, bytes.length - 1, StandardCharsets.UTF_8);
            }
            lineBytes.write(current);
            previous = current;
        }
        if (lineBytes.size() == 0) {
            return null;
        }
        return lineBytes.toString(StandardCharsets.UTF_8);
    }

}
