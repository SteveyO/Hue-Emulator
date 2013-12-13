package com.hueemulator.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PHSchedulesEntry
{
 @JsonProperty("time")
 private String time;
 
 @JsonProperty("description")
 private String description;
 
 @JsonProperty("name")
 private String name;
 
 @JsonProperty("command")
 private PHCommand command;
 
 public String getTime()
 {
  return time;
 }

 public void setTime(String time)
 {
  this.time = time;
 }

 public String getDescription()
 {
  return description;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public PHCommand getCommand()
 {
  return command;
 }

 public void setCommand(PHCommand command)
 {
  this.command = command;
 }

}
