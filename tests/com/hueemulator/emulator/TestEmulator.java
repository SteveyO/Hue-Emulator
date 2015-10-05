package com.hueemulator.emulator;

import java.io.IOException;

import com.hueemulator.emulator.Controller;
import com.hueemulator.emulator.Emulator;
import com.hueemulator.emulator.Model;
import com.hueemulator.server.Server;



public class TestEmulator {
    private static TestEmulator instance = null;
    private Emulator emulator;
    private Model model;
    private Controller controller;
    String fileName = "/config-3bulbs.json";
    
    private boolean isServerRunning=false;
    
    public static final String PORT_NUMBER="8888"; 
    String baseURL = "http://localhost:" + PORT_NUMBER + "/api/";
    
    public static TestEmulator getInstance() {
        if (instance == null) {
            instance = new TestEmulator();
        }
        return instance;
    }
    
   
    
    public  void startEmulator() throws IOException {
        if (controller!=null) return;

        model = new Model();
        controller = new Controller(model, null, null);
        emulator = new Emulator(controller, null);

        emulator.loadConfiguration(fileName);
        setModel(model);
   
        try {        
            isServerRunning=true;
            emulator.setServer(new Server(model.getBridgeConfiguration(), controller, PORT_NUMBER));
        } catch (java.net.BindException e) {
            System.out.println(" **NOT STARTED **  Server already running.    " + e.getMessage());            
        }   

       if (emulator.getServer() != null) {   //   setUp is started before each test.    
            System.out.println("Starting JUnit Test Emulator...");    
            emulator.getServer().getHttpServer().start();
        }
    }

    /**
     * This method is to ensure the Tests are stateless.  This method should be called in the setUp before each test is run so the data is reloaded in the server.
     * i.e. So changing a light state in 1 test does not affect any other tests.
     */
    public void reloadInitialConfig() {
        emulator.loadConfiguration(fileName);
        emulator.getServer().removeContext();
        emulator.getServer().createContext(model.getBridgeConfiguration(), controller);
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

    public void setServerRunning(boolean isServerRunning) {
        this.isServerRunning = isServerRunning;
    }



    public Emulator getEmulator() {
        return emulator;
    }


    public void setEmulator(Emulator emulator) {
        this.emulator = emulator;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
    
}
