package com.hueemulator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.hueemulator.emulator.Model;

public class LightsFrame extends JFrame {
    
    // Used for slightly dimming bulbs which are off.
    private Model model;
    private GraphicsPanel graphicsPanel;
    
   public LightsFrame() {
      graphicsPanel = new GraphicsPanel("LARGE");
      setTitle("MyHue Lights");
      getContentPane().add(graphicsPanel, BorderLayout.CENTER);
   
      setMinimumSize(new Dimension(1000,300));
      pack();
      setVisible(true);
     
   }
   
   public void setModel(Model model) {
       this.model = model;
       graphicsPanel.setModel(model);
   }
   

}
