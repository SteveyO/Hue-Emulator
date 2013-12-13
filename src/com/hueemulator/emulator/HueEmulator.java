package com.hueemulator.emulator;

import com.hueemulator.gui.View;

public class HueEmulator{
   
    public static void main(String args[]) {
        new HueEmulator();
    }
    
    public HueEmulator() {
        Model model = new Model();
        
        //  Set Up the View (A JFrame, MenuBar and Console).
        View view = new View();
        
        // Bind the Model and View
        Controller controller = new Controller(model,view);
        view.getMenuBar().setController(controller);
        
        // Add all the Menu Listeners.
        controller.addMenuListeners();   
        
        // Add all the Property Frame Listeners.
        controller.addPropertiesListeners();        
        
        //  Model is needed here to paint Light Bulbs/ Show bulb information.
        view.getGraphicsPanel().setModel(model);   
    }        
    
}