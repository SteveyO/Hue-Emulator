package com.hueemulator.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class PHLight
{
 @JsonIgnore   
 private String identifier;
    
 @JsonProperty("state")
 private PHLightState state;
    
 @JsonProperty("type")
 private String type;
 
 @JsonProperty("name")
 private String name; 
 
 @JsonProperty("modelid")
 private String modelid;
 
 @JsonProperty("swversion")
 private String  swversion;
 
 @JsonProperty("uniqueid")
 private String  uniqueid;
  
 @JsonProperty("pointsymbol")
 private Map<String, String> pointsymbol;
 
 public PHLight() {}
 
 public PHLight(PHLight light) {
     this.name=light.name;
     this.modelid = light.modelid;
     this.state   = light.state;
     this.swversion = light.swversion;
     this.type =  light.type;
     this.uniqueid = light.uniqueid;
 }
 
 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public PHLightState getState()
 {
  return state;
 }

 public void setState(PHLightState state)
 {
  this.state = state;
 }

 public String getModelid()
 {
  return modelid;
 }

 public void setModelid(String modelid)
 {
  this.modelid = modelid;
 }

 public String getSwversion()
 {
  return swversion;
 }

 public void setSwversion(String swversion)
 {
  this.swversion = swversion;
 }

 public String getType()
 {
  return type;
 }

 public void setType(String type)
 {
  this.type = type;
 }

 public Map<String, String> getPointsymbol()
 {
  return pointsymbol;
 }

 public void setPointsymbol(Map<String, String> pointsymbol)
 {
  this.pointsymbol = pointsymbol;
 }

 public String getIdentifier() {
     return identifier;
 }

 public void setIdentifier(String identifier) {
     this.identifier = identifier;
 }

 public String getUniqueid() {
     return uniqueid;
 }

 public void setUniqueid(String uniqueid) {
     this.uniqueid = uniqueid;
 }

}