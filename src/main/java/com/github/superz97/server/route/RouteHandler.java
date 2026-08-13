package com.github.superz97.server.route;

import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;

@FunctionalInterface
public interface RouteHandler {

    HttpResponse handle(HttpRequest request, String param);

}
