package com.hueemulator.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.hueemulator.emulator.Controller;
import com.hueemulator.model.PHBridgeConfiguration;
import com.sun.net.httpserver.HttpServer;


public class Server {

    private HttpServer httpServer;

    public Server(PHBridgeConfiguration bridgeConfiguration, Controller controller, String portNumber) throws IOException {
        
        InetSocketAddress addr = new InetSocketAddress(Integer.valueOf(portNumber));

        httpServer = HttpServer.create(addr, 0);

        createContext(bridgeConfiguration, controller);
        httpServer.setExecutor(Executors.newCachedThreadPool());  
    }

    public void createContext(PHBridgeConfiguration bridgeConfiguration, Controller controller) {
        httpServer.createContext("/", new MyRootHandler(controller));
        httpServer.createContext("/api", new MyApiHandler(bridgeConfiguration, controller));
   }

    public void removeContext() {
        httpServer.removeContext("/api");
        httpServer.removeContext("/");
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }
}