package com.hueemulator.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class PHCommand
{
 @JsonProperty("body")
 private PHBody body;
 
 @JsonProperty("address")
 private String address;
 
 @JsonProperty("method")
 private String method;

 public PHBody getBody()
 {
  return body;
 }

 public void setBody(PHBody body)
 {
  this.body = body;
 }

 public String getAddress()
 {
  return address;
 }

 public void setAddress(String address)
 {
  this.address = address;
 }

 public String getMethod()
 {
  return method;
 }

 public void setMethod(String method)
 {
  this.method = method;
 }

}
