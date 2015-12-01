package com.hueemulator.server;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import com.hueemulator.emulator.Controller;
import com.hueemulator.server.handlers.ConfigurationAPI;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class MyRootHandler implements HttpHandler {

    private Controller controller;
    private ConfigurationAPI configurationAPIhandler;

    public MyRootHandler(Controller controller) {
        this.controller          = controller;

        configurationAPIhandler = new ConfigurationAPI();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();

        OutputStream responseBody = exchange.getResponseBody();
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        responseHeaders.set("Pragma", "no-cache");
        responseHeaders.set("Expires", "Mon, 1 Aug 2011 09:00:00 GMT");
        responseHeaders.set("Connection", "close");  // Not sure if the server will actually close the connections by just setting the header
        responseHeaders.set("Access-Control-Max-Age", "0");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        responseHeaders.set("Access-Control-Allow-Credentials", "true");
        responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        responseHeaders.set("Access-Control-Allow-Headers", "Content-Type");
        if (url.contains("description.xml")) {
            responseHeaders.set("Content-Type", "application/xml; charset=utf-8"); 
        }
        else {
            responseHeaders.set("Content-Type", "application/json; charset=utf-8");
        }
        
        exchange.sendResponseHeaders(200, 0);
        
        if (url.equals("/description.xml")) {
            configurationAPIhandler.getBridgeDescription(responseBody, controller.getIpAddress());
            controller.addTextToConsole(url.toString(), Color.gray, true);
        }
    }
}