package com.hueemulator.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class PHScenesEntry
{
    
 @JsonProperty("name")
 private String   name;
 
 @JsonProperty("lights")
 private List<String> lights;
 
 @JsonProperty("active")
 private boolean active;

 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public List<String> getLights()
 {
  return lights;
 }

 public void setLights(List<String> lights)
 {
  this.lights = lights;
 }

 public boolean isActive() {
     return active;
 }

 public void setActive(boolean active) {
     this.active = active;
 }

}
