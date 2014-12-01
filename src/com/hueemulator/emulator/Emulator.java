package com.hueemulator.emulator;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.server.Server;
import com.hueemulator.server.UPNPServer;



// Taken from here: http://www.java2s.com/Code/Java/JDK-6/LightweightHTTPServer.htm
// Here is another one:  http://www.sourcestream.com/programming-stuff/java-http-server

public class Emulator {

    private Server server;
    private UPNPServer upnpServer;
    private Controller controller;

    public Emulator(Controller controller)  {
        this.controller = controller;
        String fileName = "/config-2bulbs.json";
        controller.addTextToConsole("Loading in Configuration, Filename: " + fileName, Color.WHITE, true);

        loadConfiguration(fileName);
        controller.addTextToConsole("Starting Emulator...", Color.GREEN, true);         
    }

    public void startServers() {
        try {
            server = new Server(controller.getModel().getBridgeConfiguration(), controller, controller.getPort());
            controller.setIPAddress();
        } catch (java.net.BindException e) {
            controller.addTextToConsole(" **NOT STARTED **  Server already running.    " + e.getMessage(), Color.RED, true);
        } catch (IOException e) {
            e.printStackTrace();
        }           
        server.getHttpServer().start();        
        controller.addTextToConsole("**STARTED**     Emulator is listening on port: " + controller.getPort(), Color.WHITE, true);

        upnpServer = new UPNPServer(controller);
        upnpServer.startUPNPServer();

        controller.addTextToConsole("UPnP Server Started" , Color.WHITE, true);
        
        if (!controller.getPort().equals("80")) {
             controller.addTextToConsole("UPnP works best with the Emulator running on port 80.  Apps written with the Java/iOS SDK's wont connect to the Emulator." , Color.RED, true);        
        }

    }      

    public void stopServer() {
        if (server!=null && server.getHttpServer() !=null) {
            server.getHttpServer().stop(0);
            server.getHttpServer().removeContext("/api");
            controller.addTextToConsole("Stopping the Server on port: " + server.getHttpServer().getAddress().getPort(), Color.RED, true);
        }
        if (upnpServer !=null) {
            upnpServer.stopUPNPServer();
        }
    }

    public void loadConfiguration(String fileName) {
        //2. Convert JSON to Java object
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            controller.getModel().setBridgeConfiguration(mapper.readValue(is, PHBridgeConfiguration.class));
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();   
        } catch (IOException e) {
            e.printStackTrace();

        }
    }  

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }    

}



