package com.github.superz97.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record ServerConfig(int port) {

    private static final int DEFAULT_PORT = 4221;

    public static ServerConfig load() {
        Properties properties = new Properties();
        try (InputStream input = ServerConfig.class.getResourceAsStream("/application.properties")) {
            if (input == null) {
                return new ServerConfig(DEFAULT_PORT);
            }
            properties.load(input);
        } catch (IOException e) {
            return new ServerConfig(DEFAULT_PORT);
        }
        String portValue = properties.getProperty("server.port");
        if (portValue == null) {
            return new ServerConfig(DEFAULT_PORT);
        }

        return new ServerConfig(Integer.parseInt(portValue));
    }

}
