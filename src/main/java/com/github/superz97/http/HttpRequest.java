package com.github.superz97.http;

public record HttpRequest(HttpMethod method, String path, String version, HttpHeaders headers) {

}
