package com.github.superz97.server.route;

import com.github.superz97.http.HttpRequest;
import com.github.superz97.http.HttpResponse;
import com.github.superz97.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class Router {

    private record ExactRoute(String path, RouteHandler handler) {}
    private record PrefixRoute(String prefix, RouteHandler handler) {}

    private final List<ExactRoute> exactRoutes = new ArrayList<>();
    private final List<PrefixRoute> prefixRoutes = new ArrayList<>();

    public void register(String path, RouteHandler handler) {
        exactRoutes.add(new ExactRoute(path, handler));
    }

    public void registerPrefix(String prefix, RouteHandler handler) {
        prefixRoutes.add(new PrefixRoute(prefix, handler));
    }

    public HttpResponse handle(HttpRequest request) {
        String path = request.path();

        for (ExactRoute route : exactRoutes) {
            if (route.path().equals(path)) {
                return route.handler().handle(request, null);
            }
        }

        for (PrefixRoute route : prefixRoutes) {
            if (path.startsWith(route.prefix())) {
                String param = path.substring(route.prefix().length());
                return route.handler().handle(request, param);
            }
        }

        return HttpResponse.builder().status(HttpStatus.NOT_FOUND).build();
    }

}
