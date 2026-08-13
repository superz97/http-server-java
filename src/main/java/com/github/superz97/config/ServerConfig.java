package com.github.superz97.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public record ServerConfig(int port, String directory, long maxFileSizeBytes, Set<String> allowedExtensions) {

    private static final int DEFAULT_PORT = 4221;
    private static final long DEFAULT_MAX_FILE_SIZE = 10L * 1024 * 1024;

    public static ServerConfig load() {
        Properties properties = loadProperties();
        int port = parsePort(properties);
        String directory = properties.getProperty("files.directory");
        long maxFileSizeBytes = parseMaxFileSize(properties);
        Set<String> allowedExtensions = parseAllowedExtensions(properties);
        return new ServerConfig(port, directory, maxFileSizeBytes, allowedExtensions);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ServerConfig.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        return properties;
    }

    private static int parsePort(Properties properties) {
        String portValue = properties.getProperty("server.port");
        if (portValue == null) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(portValue);
    }

    private static long parseMaxFileSize(Properties properties) {
        String value = properties.getProperty("files.max-size-bytes");
        if (value == null) {
            return DEFAULT_MAX_FILE_SIZE;
        }
        return Long.parseLong(value);
    }

    private static Set<String> parseAllowedExtensions(Properties properties) {
        String value = properties.getProperty("files.allowed-extensions");
        if (value == null) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

}
