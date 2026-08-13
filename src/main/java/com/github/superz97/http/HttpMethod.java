package com.github.superz97.http;

public enum HttpMethod {

    GET, POST, PUT, DELETE;

    public static HttpMethod from(String token) {
        return HttpMethod.valueOf(token.toUpperCase());
    }

}
