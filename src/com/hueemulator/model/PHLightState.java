package com.hueemulator.model;

import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// @JsonFilter("stateFilter")
public class PHLightState
{
    
 @JsonProperty("on")
 private Boolean   on;
    
 @JsonProperty("bri")
 private Integer   bri;
 
 @JsonProperty("hue")
 private Integer   hue; 

 @JsonProperty("sat")
 private Integer   sat; 
 
 @JsonProperty("xy")
 private List<Double> xy;
 
 @JsonProperty("ct")
 private Integer   ct; 
 
 @JsonProperty("alert")
 private String   alert;
 
 @JsonProperty("effect")
 private String   effect;
 
 @JsonProperty("colormode")
 private String   colormode;
 
 @JsonProperty("reachable")
 private Boolean   reachable;
 
 @JsonIgnore 
 private Integer transitionTime;
 
 public PHLightState() { }
 
 public PHLightState(PHLightState lightState) {
     this.hue = lightState.hue;
     this.bri = lightState.bri;
     this.sat = lightState.sat;
     this.ct  = lightState.ct;
     this.on  = lightState.on;
     this.colormode      = lightState.colormode;
     this.alert          = lightState.alert;
     this.reachable      = lightState.reachable;
     this.xy             = lightState.xy;
 }
 
 
 public Integer getBri()
 {
  return bri;
 }

 public void setBri(Integer bri)
 {
  this.bri = bri;
 }

 public String getEffect()
 {
  return effect;
 }

 public void setEffect(String effect)
 {
  this.effect = effect;
 }

 public Integer getSat()
 {
  return sat;
 }

 public void setSat(Integer sat)
 {
  this.sat = sat;
 }

 @JsonIgnore
 public Boolean getReachable()
 {
  return reachable;
 }

 public void setReachable(Boolean reachable)
 {
  this.reachable = reachable;
 }

 public String getAlert()
 {
  return alert;
 }

 public void setAlert(String alert)
 {
  this.alert = alert;
 }

 public Integer getHue()
 {
  return hue;
 }

 public void setHue(Integer hue)
 {
  this.hue = hue;
 }

 public String getColormode()
 {
  return colormode;
 }

 public void setColormode(String colormode)
 {
  this.colormode = colormode;
 }

 public Boolean getOn()
 {
  return on;
 }

 public void setOn(Boolean on)
 {
  this.on = on;
 }

 public Integer getCt()
 {
  return ct;
 }

 public void setCt(Integer ct)
 {
  this.ct = ct;
 }

 public List<Double> getXy()
 {
  return xy;
 }

 public void setXy(List<Double> xy)
 {
  this.xy = xy;
 }

 public JSONObject serializeLightState(PHLightState state) throws JSONException{
     JSONObject toSend = new JSONObject();

     if(state.getHue() != null) {

         toSend.putOpt("hue", state.getHue());
     }
     if(state.getOn() != null){
         toSend.putOpt("on", state.getOn());
     }

     if(state.getBri() !=  null) {
         toSend.putOpt("bri", state.getBri());
     }

     if(state.getSat() !=  null) {
         toSend.putOpt("sat", state.getSat());
     }
     if(state.getTransitionTime() !=  null) {
         toSend.putOpt("transitiontime", state.getTransitionTime());
     }
     if(state.getCt() != null) {
         toSend.putOpt("ct", state.getCt());
     }
     if (state.getReachable() !=null) {
         toSend.putOpt("reachable", state.getReachable());
     }

     if (state.getXy() !=  null) {
         JSONArray xyArray = new JSONArray();
         JSONObject tempX = new JSONObject(String.format(Locale.ENGLISH,"{\"tempX\": %.4f}", state.getXy().get(0)));
         JSONObject tempY = new JSONObject(String.format(Locale.ENGLISH,"{\"tempY\": %.4f}", state.getXy().get(1)));
         xyArray.put(tempX.get("tempX")); //send 4 decimal places
         xyArray.put(tempY.get("tempY"));
         toSend.putOpt("xy", xyArray);
     }
     if(state.getAlert() != null) {
         String alert = state.getAlert();
         if(alert != null) {
             toSend.putOpt("alert", alert);
         }
     }

     if(state.getEffect() != null) {
         String effect = state.getEffect();
         if(effect != null) {
             toSend.putOpt("effect", effect);
         }
     }

     return toSend;
 }
 
 
 @Override
 public String toString() {
  return "Hue: " + hue + "  Bri: " + bri + " sat: " + sat + "  on: " + on + " reachable: " + reachable;  
 }

public Integer getTransitionTime() {
    return transitionTime;
}

public void setTransitionTime(Integer transitionTime) {
    this.transitionTime = transitionTime;
}
 
}
