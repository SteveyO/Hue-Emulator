package com.philips.lighting.emulator;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.philips.lighting.gui.View;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class Controller {
 
    // Start up the Emulator
    private Emulator emulator;
    
    private Model model;

 private View view;
    public String consoleText="";
    private DateFormat inputFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private String ipAddress;
    
    private MutableAttributeSet sas;
    private StyleContext context;
    
    public Controller(Model model, View view){
        this.model = model;
        this.view = view;  
        
        this.sas = new SimpleAttributeSet();
        this.context = new StyleContext();
        this.context.addStyle("test", null);
        
        emulator = new Emulator(this);        
        
        String introText  = "Welcome to the Hue Emulator.  Choose a port and click the Start Button"; 
        addTextToConsole(introText, Color.YELLOW);   
    }
    
    public void addPropertiesListeners() {
     view.getPropertiesFrame().getIncludeTime().addActionListener(new ActionListener() {  
     public void actionPerformed(ActionEvent e) {
       boolean isSelected = view.getPropertiesFrame().getIncludeTime().isSelected();
       model.setShowConsoleTime(isSelected);  
   }
    });
    }
    
    public void addMenuListeners(){
     // Add Listeners For Stop and Start Buttons.
     view.getMenuBar().getStartButton().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {             
               emulator.startServer();
             view.getMenuBar().getStartButton().setEnabled(false);
                view.getMenuBar().getStopButton().setEnabled(true);             
            }
        });       
     
     view.getMenuBar().getStopButton().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
             emulator.stopServer();
             view.getMenuBar().getStartButton().setEnabled(true);
                view.getMenuBar().getStopButton().setEnabled(false);
            }
        });    
        view.getMenuBar().getClearConsoleMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                    consoleText="";
                    addTextToConsole("Clear Console", Color.WHITE);
            }
        });        
     
        
        view.getMenuBar().getViewGraphicsMenuItems().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                  if (view.getMenuBar().getViewGraphicsMenuItems().isSelected()) {                     
                        view.getGraphicsPanel().setVisible(true);
                     }
                     else {
                      view.getGraphicsPanel().setVisible(false);
                     }
            }
        });
        view.getMenuBar().getLoadConfigMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
             JOptionPane.showMessageDialog(view.getConsole(), "Not implemented yet!!  Ability to load in different configs coming soon... Yippeee!");
            }
        });         
        
        view.getMenuBar().getPropertiesMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {

       view.getPropertiesFrame().setLocation(300,300);               
       view.getPropertiesFrame().setVisible(true);
            }
        });         
        view.getMenuBar().getAboutMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {

       view.getAbout().setLocation(300,300);               
       view.getAbout().setVisible(true);
            }
        });        
        view.getMenuBar().getHelpMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {

       view.getSimpleHelp().setLocation(300,300);               
       view.getSimpleHelp().setVisible(true);
            }
        });        
        
 
    } 
 
    
     
     public Controller() {
     
     }
     
     
     public void addTextToConsole(String text, Color textColour) {   
      
      if (view==null) {
          return; // No View for JUnit tests, so this is null.
      }
      
         Date currentDate = new Date();
         String dateString = inputFormat.format(currentDate);
        
         if (!model.isShowConsoleTime()) {   // Don't show date string if user has de-selected it (in propertiesFrame)
          dateString="";
         }
         
         append(dateString, text, textColour, view.getConsole());
         
         // Repaint the Light Bulbs after Every Command.
         view.getGraphicsPanel().repaint(); 
         
         // Repaint the Separate Frame version (showing large bulbs), if this is being used.
         if (view.getMenuBar().getLightFrame() != null) {
            view.getMenuBar().getLightFrame().repaint();
         }

     }    
     /**
      * When the emulator starts we should update the Bridge Config with the IP Address and PORT this Emulator is running on.
      * This is needed as JSON responses are sent using the IP Address.
      * 
      * @throws UnknownHostException 
      */
     public void setIPAddress() throws UnknownHostException {
         PHBridgeConfiguration bridgeConfiguration = model.getBridgeConfiguration();

         InetAddress ip = InetAddress.getLocalHost();

         String ipAddressAndPort = ip.getHostAddress() + ":" + view.getMenuBar().getPort().getText();
         bridgeConfiguration.getConfig().setIpaddress(ipAddressAndPort);
         this.ipAddress = ipAddressAndPort;
     }
     
     public String getPort() {
      return view.getMenuBar().getPort().getText();
     }
     
     public Model getModel() {
   return model;
  }

  public void setModel(Model model) {
   this.model = model;
  }   
  
  public void paintGraphicsPanel() {
   
  }
  
    public void addNewBulb() {
        PHBridgeConfiguration bridgeConfiguration = model.getBridgeConfiguration();
        
        int numberOfLights = bridgeConfiguration.getLights().size();
        int newIdentifer = numberOfLights+1;
        String newLightId = "" + newIdentifer;
        
        PHLight light = new PHLight();
        light.setName("New Light - " + newLightId);
        light.setModelid("LCT001");
        
        PHLightState lightState = new PHLightState();
        lightState.setHue(5000);
        lightState.setBri(254);
        lightState.setSat(254);
        lightState.setOn(true);
        lightState.setReachable(true);
        lightState.setEffect("none");
        lightState.setAlert("none");

        
        light.setState(lightState);
        
        bridgeConfiguration.getLights().put(newLightId, light);
        addTextToConsole("New Bulb Created: " + newLightId, Color.ORANGE); 
      }

    public String getIpAddress() {
        return ipAddress;
    }
    
    public void append(final String dateString, final String s, final Color color,  final JEditorPane console) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    append(dateString, s, color, console);
                }
            });
            return;
        }
        Document document = console.getDocument();

        Style style = context.addStyle("test", null);
        // set some style properties
        StyleConstants.setForeground(style, color);
 
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
       
       
        try {
            StyleConstants.setForeground(sas, Color.yellow);
            int docLength = document.getLength();
            document.insertString(docLength, dateString, null);
            docLength = document.getLength();
            document.insertString(docLength, "   " + s + "\n", style);
            console.setCaretPosition( console.getDocument().getLength());
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
