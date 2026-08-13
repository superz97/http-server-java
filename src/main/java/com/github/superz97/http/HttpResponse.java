package com.github.superz97.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {

    private final HttpStatus status;
    private final HttpHeaders headers;
    private final byte[] body;

    private HttpResponse(Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void writeTo(OutputStream out) throws IOException {
        if (body.length > 0 && headers.get("Content-Length") == null) {
            headers.set("Content-Length", String.valueOf(body.length));
        }
        StringBuilder responseHead = new StringBuilder();
        responseHead.append("HTTP/1.1 ").append(status.getCode()).append(" ").append(status.getReasonPhrase()).append("\r\n");

        for (Map.Entry<String, String> entry : headers.asMap().entrySet()) {
            responseHead.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseHead.append("\r\n");

        out.write(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    public static class Builder {
        private HttpStatus status = HttpStatus.OK;
        private final HttpHeaders headers = new HttpHeaders();
        private byte[] body = new byte[0];

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

}
