package com.github.superz97.server;

import com.github.superz97.config.ServerConfig;
import com.github.superz97.server.route.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final Router router;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public Server(ServerConfig config) {
        this.port = config.port();
        this.router = buildRouter(config.directory());
    }

    private Router buildRouter(String directory) {
        Router router = new Router();
        router.register("/", new RootHandler());
        router.registerPrefix("/echo/", new EchoHandler());
        router.register("/user-agent", new UserAgentHandler());
        router.registerPrefix("/files/", new FilesHandler(directory));
        return router;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new RequestHandler(clientSocket, router));
            }
        }
    }

}
