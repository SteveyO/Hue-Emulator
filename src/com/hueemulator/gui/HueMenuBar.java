package com.hueemulator.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import com.hueemulator.emulator.Constants;
import com.hueemulator.emulator.Controller;

public class HueMenuBar extends JMenuBar {
    private JTextField port;
    private JMenuBar menuBar;
    private JMenu fileMenu, viewsMenu, debugMenu, helpMenu;
    private JMenuItem menuItem;

    private JButton startButton;
    private JButton stopButton;
   private JMenuItem clearConsoleMenuItem, helpMenuItem, aboutMenuItem,loadConfigMenuItem, saveConfigMenuItem, newColorBulbMenuItem, newLuxBulbMenuItem;

    // Debug Menus Items
    private JMenuItem showResponseJsonMenuItem, showRequestJsonMenuItem, showFullConfigMenuItem, showTimeInJson;

    private JCheckBoxMenuItem viewGraphicsMenuItems;

    private Controller controller;
    private LightsFrame lightFrameLarge;
    private LightsFrame lightFrameSmall;
    
 public HueMenuBar() { 
               
        //Create the menu bar.
        menuBar = new JMenuBar();

        
        //Build the File Menu.
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        loadConfigMenuItem = new JMenuItem("Load Config", KeyEvent.VK_L);               
        saveConfigMenuItem = new JMenuItem("Save Config", KeyEvent.VK_L);               
        fileMenu.add(loadConfigMenuItem);
        fileMenu.add(saveConfigMenuItem);
       
        newColorBulbMenuItem = new JMenuItem("Add New Color Bulb", KeyEvent.VK_B);  
        newColorBulbMenuItem.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                   controller.addNewBulb(false);
            }
        });  
        fileMenu.add(newColorBulbMenuItem);
        
        newLuxBulbMenuItem = new JMenuItem("Add New Lux Bulb", KeyEvent.VK_L);  
        newLuxBulbMenuItem.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                controller.addNewBulb(true);
            }
        });  
        fileMenu.add(newLuxBulbMenuItem);
        
        
        clearConsoleMenuItem = new JMenuItem("Clear Console", KeyEvent.VK_L);           
        fileMenu.add(clearConsoleMenuItem);
      

        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_B);
        fileMenu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                    System.exit(0);
            }
        });        

        
        //Build the Views Menu.
        viewsMenu = new JMenu("Views");
        viewsMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewsMenu);
        viewGraphicsMenuItems = new JCheckBoxMenuItem("Graphical View");
        viewGraphicsMenuItems.setSelected(true);
        viewsMenu.add(viewGraphicsMenuItems);
        JCheckBoxMenuItem graphicsLargeFrameMenuItems;
        graphicsLargeFrameMenuItems = new JCheckBoxMenuItem("Large Frame");
        graphicsLargeFrameMenuItems.setSelected(false);
        graphicsLargeFrameMenuItems.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {      
                   if (lightFrameLarge == null) {
                       lightFrameLarge = new LightsFrame(controller, Constants.LIGHT_FRAME_LARGE);
                       lightFrameLarge.setModel(controller.getModel());
                   }
                   lightFrameLarge.setVisible(true);
                   
            }
        });
        viewsMenu.add(graphicsLargeFrameMenuItems);

        JCheckBoxMenuItem graphicsSmallFrameMenuItems;
        graphicsSmallFrameMenuItems = new JCheckBoxMenuItem("Small Frame");
        graphicsSmallFrameMenuItems.setSelected(false);
        graphicsSmallFrameMenuItems.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {      
                if (lightFrameSmall == null) {
                    lightFrameSmall = new LightsFrame(controller, Constants.LIGHT_FRAME_SMALL);
                    lightFrameSmall.setModel(controller.getModel());
                }
                lightFrameSmall.setVisible(true);
                
            }
        });
        viewsMenu.add(graphicsSmallFrameMenuItems);
        
        //Build the Debug Menu.
        debugMenu = new JMenu("Debug");
        debugMenu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(debugMenu);
        showRequestJsonMenuItem = new JCheckBoxMenuItem("Show Request JSON");
        showRequestJsonMenuItem.setSelected(true);
        showRequestJsonMenuItem.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {      
                controller.setShowRequestJson(showRequestJsonMenuItem.isSelected());                   
            }
        });
        debugMenu.add(showRequestJsonMenuItem);
        
        showResponseJsonMenuItem = new JCheckBoxMenuItem("Show Response JSON");
        showResponseJsonMenuItem.setSelected(true);
        showResponseJsonMenuItem.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {      
                controller.setShowResponseJson(showResponseJsonMenuItem.isSelected());  
            }
        });
        debugMenu.add(showResponseJsonMenuItem); 

        showFullConfigMenuItem = new JCheckBoxMenuItem("Show FullConfig JSON (Heartbeat)");
        showFullConfigMenuItem.setSelected(true);
        showFullConfigMenuItem.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {      
                controller.setShowFullConfigJson(showFullConfigMenuItem.isSelected());  
            }
        });
        debugMenu.add(showFullConfigMenuItem); 
        
        debugMenu.addSeparator();
        
        showTimeInJson = new JCheckBoxMenuItem("Show Time");
        showTimeInJson.setSelected(true);
        showTimeInJson.addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {       
                controller.getModel().setShowConsoleTime(showTimeInJson.isSelected());
            }
        });
        debugMenu.add(showTimeInJson); 

        // Debug Menus Items
 //       private JMenuItem showResponseJsonMenuItem, showRequestJsonMenuItem, showFullConfigMenuItem;
        

        //Build the Help Menu.
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        aboutMenuItem = new JMenuItem("About", KeyEvent.VK_P);        
        helpMenu.add(aboutMenuItem);

        helpMenuItem = new JMenuItem("Help");        
        helpMenu.add(helpMenuItem);

        menuBar.add(Box.createRigidArea(new Dimension(10,0)));
       
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
       
        menuBar.add(startButton);
        startButton.setPreferredSize(new Dimension(50,18));
       

        menuBar.add(Box.createRigidArea(new Dimension(6,0)));
       
       
        menuBar.add(stopButton);
        stopButton.setPreferredSize(new Dimension(50,18));           
        stopButton.setEnabled(false);
      
       
       
        menuBar.add(Box.createRigidArea(new Dimension(6,0)));
       
        JLabel portLabel = new JLabel("port:  ");
        menuBar.add(portLabel);
       
        port = new JTextField("8000", 4);
        port.setColumns(4);
        port.setMaximumSize(new Dimension(50,18));
        menuBar.add(port);              
  add(menuBar);
 }

 
    public JTextField getPort() {
  return port;
 }


 public void setPort(JTextField port) {
  this.port = port;
 }


 public JMenuBar getMenuBar() {
  return menuBar;
 }


 public void setMenuBar(JMenuBar menuBar) {
  this.menuBar = menuBar;
 }


 public JMenu getFileMenu() {
  return fileMenu;
 }


 public void setFileMenu(JMenu fileMenu) {
  this.fileMenu = fileMenu;
 }


 public JMenu getHelpMenu() {
  return helpMenu;
 }


 public void setHelpMenu(JMenu helpMenu) {
  this.helpMenu = helpMenu;
 }


 public JMenuItem getMenuItem() {
  return menuItem;
 }


 public void setMenuItem(JMenuItem menuItem) {
  this.menuItem = menuItem;
 }
 
    public JButton getStartButton() {
  return startButton;
 }

 public void setStartButton(JButton startButton) {
  this.startButton = startButton;
 }

 public JButton getStopButton() {
  return stopButton;
 }

 public void setStopButton(JButton stopButton) {
  this.stopButton = stopButton;
 }
 
 public JMenuItem getClearConsoleMenuItem() {
  return clearConsoleMenuItem;
 }

 public void setClearConsoleMenuItem(JMenuItem clearConsoleMenuItem) {
  this.clearConsoleMenuItem = clearConsoleMenuItem;
 } 

 public JMenuItem getHelpMenuItem() {
  return helpMenuItem;
 }

 public void setHelpMenuItem(JMenuItem helpMenuItem) {
  this.helpMenuItem = helpMenuItem;
 }
 
 public JMenuItem getAboutMenuItem() {
  return aboutMenuItem;
 }
 
 public void setAboutMenuItem(JMenuItem aboutMenuItem) {
  this.aboutMenuItem = aboutMenuItem;
 }
 
 public JCheckBoxMenuItem getViewGraphicsMenuItems() {
  return viewGraphicsMenuItems;
 }

 public void setViewGraphicsMenuItems(JCheckBoxMenuItem viewGraphicsMenuItems) {
  this.viewGraphicsMenuItems = viewGraphicsMenuItems;
 } 
 
 public JMenuItem getLoadConfigMenuItem() {
  return loadConfigMenuItem;
 }

 public void setLoadConfigMenuItem(JMenuItem loadConfigMenuItem) {
  this.loadConfigMenuItem = loadConfigMenuItem;
 } 
 
 public JMenuItem getSaveConfigMenuItem() {
     return saveConfigMenuItem;
 }
 
 public void setSaveConfigMenuItem(JMenuItem saveConfigMenuItem) {
     this.saveConfigMenuItem = saveConfigMenuItem;
 } 
 
 public void setController(Controller controller) {
     this.controller=controller;
 }

    public LightsFrame getLargeLightFrame() {
        return lightFrameLarge;
    }

    public void setLargeLightFrame(LightsFrame lightFrame) {
        this.lightFrameLarge = lightFrame;
    }

    public LightsFrame getSmallLightFrame() {
        return lightFrameSmall;
    }
    
    public void setSmallLightFrame(LightsFrame lightFrame) {
        this.lightFrameSmall = lightFrame;
    }
    
}
