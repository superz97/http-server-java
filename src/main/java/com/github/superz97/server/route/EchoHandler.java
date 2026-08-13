package com.github.superz97.server.route;

import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;

public class EchoHandler implements RouteHandler {

    @Override
    public HttpResponse handle(HttpRequest request, String param) {
        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .header("Content-Type", "text/plain")
                .body(param)
                .build();
    }

}
