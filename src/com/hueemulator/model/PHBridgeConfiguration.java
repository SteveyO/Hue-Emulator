package com.hueemulator.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class PHBridgeConfiguration
{
 @JsonProperty("lights")
 private Map<String, PHLight> lights;
 
 @JsonProperty("schedules")
 private Map<String, PHSchedulesEntry> schedules;
 
 @JsonProperty("config")
 private PHConfig config;
 
 @JsonProperty("groups")
 private Map<String, PHGroupsEntry> groups;

 @JsonProperty("scenes")
 private Map<String, PHScenesEntry> scenes;


 public Map<String, PHLight> getLights()
 {
  return lights;
 }

 public void setLights(Map<String, PHLight> lights)
 {
  this.lights = lights;
 }

 public Map<String, PHSchedulesEntry> getSchedules()
 {
  return schedules;
 }

 public void setSchedules(Map<String, PHSchedulesEntry> schedules)
 {
  this.schedules = schedules;
 }

 public PHConfig getConfig()
 {
  return config;
 }

 public void setConfig(PHConfig config)
 {
  this.config = config;
 }

 public Map<String, PHGroupsEntry> getGroups()
 {
  return groups;
 }

 public Map<String, PHScenesEntry> getScenes()
 {
     return scenes;
 }

 public void setGroups(Map<String, PHGroupsEntry> groups)
 {
  this.groups = groups;
 }

 public void setScenes(Map<String, PHScenesEntry> scenes)
 {
     this.scenes = scenes;
 }

}
