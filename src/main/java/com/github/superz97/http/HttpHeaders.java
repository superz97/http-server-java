package com.github.superz97.http;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void set(String name, String value) {
        headers.put(name, value);
    }

    public String get(String name) {
        return headers.get(name);
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(headers);
    }

}
