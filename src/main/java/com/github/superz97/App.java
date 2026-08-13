package com.github.superz97;

import com.github.superz97.config.ServerConfig;
import com.github.superz97.server.Server;

import java.io.IOException;

public class App {

    public static void main( String[] args ) {
        ServerConfig config = ServerConfig.load();
        try {
            new Server(config).start();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

}
