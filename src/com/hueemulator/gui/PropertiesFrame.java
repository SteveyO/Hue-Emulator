package com.hueemulator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class PropertiesFrame extends JFrame{
   private JCheckBox includeTime;
   private JCheckBox showJSONResponses;
 

   public PropertiesFrame() {
    setTitle("Emulator Properties");
    includeTime = new JCheckBox("Write time to console");
    includeTime.setSelected(true);


    showJSONResponses = new JCheckBox("Show JSON Responses");
    showJSONResponses.setSelected(true);
    showJSONResponses.addActionListener(new ActionListener() {  
     public void actionPerformed(ActionEvent e) {
    
     }
    });
    
    JPanel listPane = new JPanel();
    listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
    listPane.add(includeTime);
    listPane.add(showJSONResponses);
    
    add(listPane);    
    setResizable(false);
    setSize(300, 200);
    }

 public JCheckBox getIncludeTime() {
  return includeTime;
 }
 
 public void setIncludeTime(JCheckBox includeTime) {
  this.includeTime = includeTime;
 }
 
 public JCheckBox getShowJSONResponses() {
  return showJSONResponses;
 }
 
 public void setShowJSONResponses(JCheckBox showJSONResponses) {
  this.showJSONResponses = showJSONResponses;
 }


}
