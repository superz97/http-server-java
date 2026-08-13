package com.github.superz97.server.route;

import com.github.superz97.http.HttpMethod;
import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class Router {

    private record ExactRoute(HttpMethod method, String path, RouteHandler handler) {
    }

    private record PrefixRoute(HttpMethod method, String prefix, RouteHandler handler) {
    }

    private final List<ExactRoute> exactRoutes = new ArrayList<>();
    private final List<PrefixRoute> prefixRoutes = new ArrayList<>();
    public void register(HttpMethod method, String path, RouteHandler handler) {
        exactRoutes.add(new ExactRoute(method, path, handler));
    }

    public void registerPrefix(HttpMethod method, String prefix, RouteHandler handler) {
        prefixRoutes.add(new PrefixRoute(method, prefix, handler));
    }

    public HttpResponse handle(HttpRequest request) {
        String path = request.path();
        HttpMethod method = request.method();

        for (ExactRoute route : exactRoutes) {
            if (route.method() == method && route.path().equals(path)) {
                return route.handler().handle(request, null);
            }
        }

        for (PrefixRoute route : prefixRoutes) {
            if (route.method() == method && path.startsWith(route.prefix())) {
                String param = path.substring(route.prefix().length());
                return route.handler().handle(request, param);
            }
        }

        return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
    }

}
