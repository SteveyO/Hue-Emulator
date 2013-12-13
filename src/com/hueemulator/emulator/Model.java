package com.hueemulator.emulator;

import com.hueemulator.model.PHBridgeConfiguration;


public class Model {
 
 private PHBridgeConfiguration bridgeConfiguration; 
 private boolean showConsoleTime;
     

 public Model(){
     showConsoleTime=true;
    }

 
    public boolean isShowConsoleTime() {
  return showConsoleTime;
 }
 public void setShowConsoleTime(boolean showConsoleTime) {
  this.showConsoleTime = showConsoleTime;
 }
 
    public PHBridgeConfiguration getBridgeConfiguration() {
  return bridgeConfiguration;
 }

 public void setBridgeConfiguration(PHBridgeConfiguration bridgeConfiguration) {
  this.bridgeConfiguration = bridgeConfiguration;
 }

 
}