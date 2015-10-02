package com.hueemulator.emulator;

import com.hueemulator.gui.View;

public class HueEmulator{
   
    public static void main(String args[]) {
        new HueEmulator(args.length > 0 ? args[0] : null);
    }
    
    public HueEmulator(String fileName) {
        Model model = new Model();
        
        //  Set Up the View (A JFrame, MenuBar and Console).
        View view = new View();
        
        // Bind the Model and View
        Controller controller = new Controller(model,view,fileName);
        view.getMenuBar().setController(controller);
        view.getGraphicsPanel().setController(controller);
        
        // Add all the Menu Listeners.
        controller.addMenuListeners();   
        
        // Add all the Property Frame Listeners.
        controller.addPropertiesListeners();        
        
        //  Model is needed here to paint Light Bulbs/ Show bulb information.
        view.getGraphicsPanel().setModel(model);   
    }        
    
}