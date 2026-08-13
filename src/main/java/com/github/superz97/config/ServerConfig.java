package com.github.superz97.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record ServerConfig(int port, String directory) {

    private static final int DEFAULT_PORT = 4221;

    public static ServerConfig load() {
        Properties properties = loadProperties();
        int port = parsePort(properties);
        String directory = properties.getProperty("files.directory");
        return new ServerConfig(port, directory);
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

}
