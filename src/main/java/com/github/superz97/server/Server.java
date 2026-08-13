package com.github.superz97.server;

import com.github.superz97.config.ServerConfig;
import com.github.superz97.http.HttpMethod;
import com.github.superz97.server.route.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final long maxBodySize;
    private final Router router;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public Server(ServerConfig config) {
        this.port = config.port();
        this.maxBodySize = config.maxFileSizeBytes();
        this.router = buildRouter(config);
    }

    private Router buildRouter(ServerConfig config) {
        Router router = new Router();
        router.register(HttpMethod.GET, "/", new RootHandler());
        router.registerPrefix(HttpMethod.GET, "/echo/", new EchoHandler());
        router.register(HttpMethod.GET, "/user-agent", new UserAgentHandler());
        router.registerPrefix(HttpMethod.GET, "/files/", new FilesHandler(config.directory()));
        router.registerPrefix(HttpMethod.POST, "/files/", new FileUploadHandler(config.directory(), config.allowedExtensions()));
        return router;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new RequestHandler(clientSocket, router, maxBodySize));
            }
        }
    }

}
