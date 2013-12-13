package com.hueemulator.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class PHSwupdate
{
 @JsonProperty("text")
 private String text;
 
 @JsonProperty("notify")
 private Boolean notify;
 
 @JsonProperty("updatestate")
 private Integer updatestate;
 
 @JsonProperty("url")
 private String url;

 public String getText()
 {
  return text;
 }

 public void setText(String text)
 {
  this.text = text;
 }

 public Boolean getNotify()
 {
  return notify;
 }

 public void setNotify(Boolean notify)
 {
  this.notify = notify;
 }

 public Integer getUpdatestate()
 {
  return updatestate;
 }

 public void setUpdatestate(Integer updatestate)
 {
  this.updatestate = updatestate;
 }

 public String getUrl()
 {
  return url;
 }

 public void setUrl(String url)
 {
  this.url = url;
 }

}
