package com.philips.lighting.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class PHLight
{

 @JsonProperty("name")
 private String name;
 
 @JsonProperty("state")
 private PHLightState state;
 
 @JsonProperty("modelid")
 private String modelid;
 
 @JsonProperty("swversion")
 private Integer  swversion;
 
 @JsonProperty("type")
 private String type;
 
 @JsonProperty("pointsymbol")
 private Map<String, String> pointsymbol;

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

 public Integer getSwversion()
 {
  return swversion;
 }

 public void setSwversion(Integer swversion)
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

}