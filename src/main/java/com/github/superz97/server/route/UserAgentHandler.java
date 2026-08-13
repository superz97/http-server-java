package com.github.superz97.server.route;

import com.github.superz97.http.HttpHeaders;
import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;

public class UserAgentHandler implements RouteHandler {

    @Override
    public HttpResponse handle(HttpRequest request, String param) {
        HttpHeaders headers = request.headers();
        String userAgent = headers.get("User-Agent");
        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .header("Content-Type", "text/plain")
                .body(userAgent)
                .build();
    }
}
