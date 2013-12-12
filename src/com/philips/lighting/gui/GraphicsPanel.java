package com.philips.lighting.gui;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.philips.lighting.emulator.Model;
import com.philips.lighting.model.PHConfig;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;


public class GraphicsPanel extends JPanel implements MouseListener {
    private Model model;
    private BufferedImage bulbImage;
    private BufferedImage lampTop;   // For when the bulb is off
       
    private BufferedImage bridgeImage;
    
    // Used for slightly dimming bulbs which are off.
    private AlphaComposite dimAlphaComposite;
    private AlphaComposite helpAlphaComposite;
    private AlphaComposite normalAlphaComposite;

    private static final int VIEW_TYPE_LARGE=0;
    private static final int VIEW_TYPE_SMALL=1;
    private int viewType;
    private int lightXOffset;
    private int lightsGap;
    private int yPosition;
    private boolean drawBulbInfo=false;
    private boolean drawBridgeInfo=false;
   
    private int numberOfBulbs=0;
    private int mouseOverBulb=-1;  // Used for Helper Message, to indicate for which bulb to display the help/info.
    
    public GraphicsPanel(String size) {
        
        String path = "/";
        if (size.equalsIgnoreCase("LARGE")) {
            path +="largeImages/";
            viewType=VIEW_TYPE_LARGE;
            lightsGap=240;
            lightXOffset=245;
            yPosition=-40;
        }
        else {
            yPosition=-6;
            lightsGap=80;
            lightXOffset=130;
            viewType=VIEW_TYPE_SMALL;
        }

        setPreferredSize(new Dimension(1000,100));
        try {      
         bridgeImage = ImageIO.read(getClass().getResource(path + "bridge.png"));
         bulbImage   = ImageIO.read(getClass().getResource(path + "lamp.png"));
         lampTop     = ImageIO.read(getClass().getResource(path + "lampTop.png"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int type = AlphaComposite.SRC_OVER;
        dimAlphaComposite    = AlphaComposite.getInstance(type, 0.5f);
        normalAlphaComposite = AlphaComposite.getInstance(type, 1f);
        helpAlphaComposite = AlphaComposite.getInstance(type, 0.8f);
        
        addMouseListener(this);
    }
   
    public void paintComponent( Graphics g ) {
        super.paintComponent(g);
        

        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        g2.setColor(Color.black);
        g2.fillRect(0,0,super.getWidth(),super.getHeight());

        g2.drawImage(bridgeImage, 5,10,null);

        if (viewType==VIEW_TYPE_LARGE) {
            yPosition=-40;
        }
    
        if (model!=null) {         
         Map <String, PHLight> lightsMap = model.getBridgeConfiguration().getLights();
         
         Iterator it = lightsMap.entrySet().iterator();
         
         int counter=0;
         
         while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          PHLight light = (PHLight) entry.getValue();
          PHLightState state = light.getState();
                
                if (state.getOn()) { 
                    g2.setComposite(normalAlphaComposite);
                }
                else  { 
                    g2.setComposite(dimAlphaComposite);    
                }
                
                if (viewType==VIEW_TYPE_LARGE && counter > 0 && counter % 5 == 0) {
                 counter=0;
                 yPosition+=300;
                }

                if (state.getOn()) {
                      // Bulb Colour
                    float h =  (float) state.getHue()/65535;
                    float s =  (float) state.getSat()/254;
                    float b =  (float) state.getBri()/254;
                    
                    int rgb = Color.HSBtoRGB(h,s,b);
                    Color color = new Color(rgb);
                    g2.setColor(color);
                    g2.fillRect(lightXOffset + (counter*lightsGap),yPosition, bulbImage.getWidth(), bulbImage.getHeight());

                }
                else {
                    g2.drawImage(lampTop, lightXOffset + (counter*lightsGap),yPosition,null);
                }
                
                g2.drawImage(bulbImage, lightXOffset + (counter*lightsGap),yPosition,null);
                
                counter++;
                
            
                if (drawBridgeInfo && viewType == VIEW_TYPE_LARGE) {
                 showBridgeInfo(g2, model);                        
                }
                if (drawBulbInfo && viewType == VIEW_TYPE_LARGE && mouseOverBulb != -1 && (mouseOverBulb +1) ==  counter) {
                 showBulbInfo(g2, light, state);                        
                }
         }  // End of Lights loop   
         
         numberOfBulbs=counter;
        }
        
        
     }
    
    public void showBridgeInfo(Graphics2D g2, Model model) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.setComposite(helpAlphaComposite);
        g2.fillRect(15, 10, lightsGap-20, 270);
        g2.setComposite(normalAlphaComposite);
        g2.setColor(Color.MAGENTA);  
        
        PHConfig config = model.getBridgeConfiguration().getConfig();
        
        g2.drawLine(15, 12, 200, 12);
        g2.drawLine(15, 30, 200, 30);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Name:",               30,    25);
        g2.drawString(config.getName(),     100,    25);

        
        g2.drawString("IP Address:",         30,    50);
        g2.drawString(config.getIpaddress(),100,    50);  
        
        g2.drawString("UTC:",                30,    70);
        g2.drawString(config.getUTC(),      100,    70); 
        
        g2.drawString("SW Version:",                30,   90);
        g2.drawString("" + config.getSwversion(),   100,  90); 
        
     }
    
    public void showBulbInfo(Graphics2D g2, PHLight light, PHLightState state) {
        int newXOffset = (1 + mouseOverBulb) * lightXOffset;
        int offSet = 260;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.setComposite(helpAlphaComposite);
        g2.fillRect(newXOffset+15, 10, lightsGap-30, offSet);
        g2.setComposite(normalAlphaComposite);
        g2.setColor(Color.MAGENTA);  
        
        g2.drawLine(newXOffset+30, 12,   newXOffset+200, 12);
        g2.drawLine(newXOffset+30, 30, newXOffset+200, 30);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Name:",           newXOffset+30,     25);
        g2.drawString(light.getName(),   newXOffset+100,    25);
     
        g2.drawString("Model:",          newXOffset+30,     50);
        g2.drawString(light.getModelid(),newXOffset+100,    50);  
        
        g2.drawString("Hue:",          newXOffset+30,       70);
        g2.drawString("" + state.getHue(),  newXOffset+100, 70); 
        
        g2.drawString("Sat:",          newXOffset+30,       90);
        g2.drawString("" + state.getSat(),  newXOffset+100, 90); 
        
        g2.drawString("Bri:",          newXOffset+30,      110);
        g2.drawString("" + state.getBri(),  newXOffset+100,110); 
        
        g2.drawString("x/y:",              newXOffset+30,  130);
        g2.drawString("" + state.getXy(),  newXOffset+100, 130); 
        
        g2.drawString("Alert:",            newXOffset+30,  150);
        g2.drawString(state.getAlert(),    newXOffset+100, 150);  
        
        g2.drawString("Effect",            newXOffset+30,  170);
        g2.drawString(state.getEffect(),   newXOffset+100, 170); 
        
        g2.drawString("On:",               newXOffset+30,  190);
        g2.drawString("" + state.getOn(),  newXOffset+100, 190); 
    }
    
    public Model getModel() {
       return model;
    }

    public void setModel(Model model) {
       this.model = model;
    }




 @Override
 public void mouseClicked(MouseEvent e) {

  
 }

 @Override
 public void mouseEntered(MouseEvent arg0) {
  // TODO Auto-generated method stub
  
 }

 @Override
 public void mouseExited(MouseEvent arg0) {
        drawBulbInfo   = false;
        drawBridgeInfo = false;
        this.repaint(); 
 }

 @Override
 public void mousePressed(MouseEvent e) {
        if (viewType == VIEW_TYPE_LARGE) {
            int x= e.getX();
//            int y=e.getY();
            
            drawBulbInfo   = false;
            drawBridgeInfo = false;
            
            for (int bulb=0; bulb<  numberOfBulbs; bulb++) {
           
             if (x < lightXOffset) {
              drawBridgeInfo = true;
             }
             else if (x > lightXOffset + (bulb * lightsGap) && x < lightXOffset + (bulb * lightsGap) + lightsGap) {
                    drawBulbInfo=true;
                    mouseOverBulb = bulb;
                }
            }
               
        }

        this.repaint(); 
 }

 @Override
 public void mouseReleased(MouseEvent arg0) {
        drawBulbInfo   = false;
        drawBridgeInfo = false;
        this.repaint(); 
 }
}