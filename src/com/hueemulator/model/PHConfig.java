package com.hueemulator.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class PHConfig
{
 @JsonProperty("portalservices")
 private Boolean  portalservices;
 
 @JsonProperty("gateway")
 private String  gateway;
 
 @JsonProperty("mac")
 private String  mac;

 @JsonProperty("bridgeid")
 private String  bridgeid;

 @JsonProperty("modelid")
 private String  modelid;
 
 @JsonProperty("swversion")
 private String  swversion;
 
 @JsonProperty("linkbutton")
 private Boolean  linkbutton;
 
 @JsonProperty("ipaddress")
 private String  ipaddress;
 
 @JsonProperty("proxyport")
 private Integer  proxyport;
 
 @JsonProperty("swupdate")
 private PHSwupdate swupdate;
 
 @JsonProperty("netmask")
 private String  netmask;
 
 @JsonProperty("name")
 private String  name;
 
 @JsonProperty("dhcp")
 private Boolean  dhcp;
 
 @JsonProperty("UTC")
 private String  utc;
 
 @JsonProperty("proxyaddress")
 private String  proxyaddress;
 
 @JsonProperty("whitelist")
 private Map<String, PHWhitelistEntry> whitelist;



public Boolean getPortalservices()
 {
  return portalservices;
 }

 public void setPortalservices(Boolean portalservices)
 {
  this.portalservices = portalservices;
 }

 public String getGateway()
 {
  return gateway;
 }

 public void setGateway(String gateway)
 {
  this.gateway = gateway;
 }

 public String getMac()
 {
  return mac;
 }

 public void setMac(String mac)
 {
  this.mac = mac;
 }

 public String getSwversion()
 {
  return swversion;
 }

 public void setSwversion(String swversion)
 {
  this.swversion = swversion;
 }

 public Boolean getLinkbutton()
 {
  return linkbutton;
 }

 public void setLinkbutton(Boolean linkbutton)
 {
  this.linkbutton = linkbutton;
 }

 public String getIpaddress()
 {
  return ipaddress;
 }

 public void setIpaddress(String ipaddress)
 {
  this.ipaddress = ipaddress;
 }

 public Integer getProxyport()
 {
  return proxyport;
 }

 public void setProxyport(Integer proxyport)
 {
  this.proxyport = proxyport;
 }

 public PHSwupdate getSwupdate()
 {
  return swupdate;
 }

 public void setSwupdate(PHSwupdate swupdate)
 {
  this.swupdate = swupdate;
 }

 public String getNetmask()
 {
  return netmask;
 }

 public void setNetmask(String netmask)
 {
  this.netmask = netmask;
 }

 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public Boolean getDhcp()
 {
  return dhcp;
 }

 public void setDhcp(Boolean dhcp)
 {
  this.dhcp = dhcp;
 }

 public String getUtc()
 {
  return utc;
 }

 public void setUtc(String utc)
 {
  this.utc = utc;
 }

 public String getProxyaddress()
 {
  return proxyaddress;
 }

 public void setProxyaddress(String proxyaddress)
 {
  this.proxyaddress = proxyaddress;
 }

 public Map<String, PHWhitelistEntry> getWhitelist() {
	 return whitelist;
 }

 public void setWhitelist(Map<String, PHWhitelistEntry> whitelist) {
	 this.whitelist = whitelist;
 }

public String getBridgeid() {
    return bridgeid;
}

public void setBridgeid(String bridgeid) {
    this.bridgeid = bridgeid;
}

public String getModelid() {
    return modelid;
}

public void setModelid(String modelid) {
    this.modelid = modelid;
}
}
