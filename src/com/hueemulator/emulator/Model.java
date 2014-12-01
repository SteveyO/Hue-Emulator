package com.hueemulator.emulator;

import com.hueemulator.model.PHBridgeConfiguration;


public class Model {
 
 private PHBridgeConfiguration bridgeConfiguration; 
 private boolean showConsoleTime;
 private boolean showRequestJSON=true;
 private boolean showResponseJSON=true;
 private boolean showFullConfigJSON=true;
     

 public Model(){
     showConsoleTime=true;
 }

 public boolean isShowConsoleTime() {
     return showConsoleTime;
 }

 public boolean isShowFullConfig() {
     return showFullConfigJSON;
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

public boolean isShowRequestJSON() {
    return showRequestJSON;
}

public void setShowRequestJSON(boolean showRequestJSON) {
    this.showRequestJSON = showRequestJSON;
}

public boolean isShowResponseJSON() {
    return showResponseJSON;
}

public void setShowResponseJSON(boolean showResponseJSON) {
    this.showResponseJSON = showResponseJSON;
}

public void setShowFullConfigJSON(boolean showFullConfigJSON) {
    this.showFullConfigJSON = showFullConfigJSON;
}

 
}