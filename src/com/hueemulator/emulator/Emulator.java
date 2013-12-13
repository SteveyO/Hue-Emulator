package com.hueemulator.emulator;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.server.Server;



// Taken from here: http://www.java2s.com/Code/Java/JDK-6/LightweightHTTPServer.htm
// Here is another one:  http://www.sourcestream.com/programming-stuff/java-http-server

public class Emulator {
 
 private Server server;
 private Controller controller;
     
      public Emulator(Controller controller)  {
         this.controller = controller;
         String fileName = "/config-2bulbs.json";
         controller.addTextToConsole("Loading in Configuration, Filename: " + fileName, Color.WHITE);

         loadConfiguration(fileName);
         controller.addTextToConsole("Starting Emulator...", Color.GREEN);         

    }
      
      public void startServer() {

          try {
              server = new Server(controller.getModel().getBridgeConfiguration(), controller, controller.getPort());
              controller.setIPAddress();
          } catch (java.net.BindException e) {
              controller.addTextToConsole(" **NOT STARTED **  Server already running.    " + e.getMessage(), Color.RED);
          } catch (IOException e) {
              e.printStackTrace();
          }           
          server.getHttpServer().start();        
          controller.addTextToConsole("**STARTED**     Emulator is listening on port: " + controller.getPort(), Color.WHITE);

      }      
    
    public void stopServer() {
     if (server!=null && server.getHttpServer() !=null) {
      server.getHttpServer().stop(0);
      server.getHttpServer().removeContext("/api");
      controller.addTextToConsole("Stopping the Server on port: " + server.getHttpServer().getAddress().getPort(), Color.RED);
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



