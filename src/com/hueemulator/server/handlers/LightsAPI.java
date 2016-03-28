package com.hueemulator.server.handlers;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hueemulator.emulator.Constants;
import com.hueemulator.emulator.Controller;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.model.PHLight;
import com.hueemulator.model.PHLightState;
import com.hueemulator.model.PHSchedulesEntry;
import com.hueemulator.utils.PHUtilitiesHelper;
import com.hueemulator.utils.PointF;
import com.hueemulator.utils.Utils;

public class LightsAPI {

    DecimalFormat fourDP = new DecimalFormat("#.####");

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  1.1  GET ALL LIGHTS
    //  http://www.developers.meethue.com/documentation/lights-api#11_get_all_lights   1.1. Get all lights
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    
    public void getAllLights_1_1(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {    
        Map <String, PHLight> lightsMap = bridgeConfiguration.getLights();

  //      mapper.writeValue(responseBody, lightsMap);   // Write to the response.
        
        JSONObject lightsJson = new JSONObject();
        for (Map.Entry<String, PHLight> entry : lightsMap.entrySet()) {
            PHLight light = entry.getValue();
            lightsJson.put(entry.getKey(), getLightJSON(light));

        }
        responseBody.write(lightsJson.toString().getBytes());
        responseBody.close();
    }


    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  1.4  GET LIGHT ATTRIBUTES AND STATE
    //  http://www.developers.meethue.com/documentation/lights-api#14_get_light_attributes_and_state   1.4. Get light attributes and state
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void getLightAttributes_1_4(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String lightIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getLights() == null || bridgeConfiguration.getLights().get(lightIdentifier) == null) {
            sendErrorResponse(lightIdentifier, "3", responseBody);
        }
        else {
            
            responseBody.write(getLightJSON(bridgeConfiguration.getLights().get(lightIdentifier)).toString().getBytes());
            responseBody.close();
            
//            mapper.writeValue(responseBody, bridgeConfiguration.getLights().get(lightIdentifier));   // Write to the response.
            controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration.getLights().get(lightIdentifier)),Color.WHITE, controller.showResponseJson()); 
        }

    }

    
    public JSONObject getLightJSON(PHLight light) {
        JSONObject lightJson = new JSONObject();

        JSONObject stateJson = new JSONObject();
        PHLightState state = light.getState();
        stateJson.putOpt("on", state.getOn());
        stateJson.putOpt("bri", state.getBri());
        
        if (!light.getModelid().equals(Constants.MODEL_ID_LUX_BULB)) {
          stateJson.putOpt("sat", state.getSat());
          stateJson.putOpt("hue", state.getHue());
          stateJson.putOpt("xy",  state.getXy());
          stateJson.putOpt("ct",  state.getCt());
          stateJson.putOpt("effect",     state.getEffect());
          stateJson.putOpt("colormode",  state.getColormode());
        }
        
        stateJson.putOpt("alert",      state.getAlert());
        
        stateJson.putOpt("reachable",  state.getReachable());
        
        
        lightJson.putOpt("state", stateJson);
        lightJson.putOpt("type", light.getType());
        lightJson.putOpt("name", light.getName());
        lightJson.putOpt("swversion", light.getSwversion());
        lightJson.putOpt("uniqueid", light.getUniqueid());
        lightJson.putOpt("modelid", light.getModelid());
        lightJson.putOpt("pointsymbol", light.getPointsymbol());
        
        return lightJson;
        
    }
    
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  1.5  SET LIGHT ATTRIBUTES
    //  http://www.developers.meethue.com/documentation/lights-api#15_set_light_attributes_rename   1.5. Set light attributes (rename)
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void setLightAttributes_1_5(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String light) throws JsonParseException, IOException {    
        PHLight lightObject = bridgeConfiguration.getLights().get(light);

        String responseBase = "/lights/" + light + "/";
        String resourceUrl="";

        JSONArray responseArray = new JSONArray();

        JSONObject jObject = new JSONObject(jSONString);
        if (jObject != null) {
            JSONArray names = jObject.names();

            for (int i=0; i<names.length(); i++) {
                JSONObject successObject = new JSONObject();
                boolean isSuccess=true;  // Success is returned for a valid fieldname, if a field name is invalid then an "error" is returned.
                responseArray.put(successObject);

                JSONObject successLine = new JSONObject();
                String name = names.getString(i);
                resourceUrl = responseBase + name;

                if (name.equals("name")) {
                    String lightName = jObject.optString("name");

                    if (lightName == null || lightName.length() > 32) {
                        isSuccess=false;
                    }
                    else {
                        successLine.putOpt(resourceUrl, lightName);
                        lightObject.setName(lightName);                     
                    }
                }

                if (!isSuccess)  {   // Handle errors,  i.e.  Non Supported fields
                    JSONObject errorLine = new JSONObject();
                    errorLine.putOpt("type", 7);
                    errorLine.putOpt("address", resourceUrl);
                    errorLine.putOpt("description", "invalid value, " + Utils.chopName(jObject.optString(name)) + ", for parameter, name");
                    successObject.putOpt("error", errorLine);
                }


                if (isSuccess) { 
                    successObject.putOpt("success", successLine);
                }
            }
        }

        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();
        bridgeConfiguration.getLights().put(light, lightObject);  
        controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  1.6  SET LIGHT STATE
    // http://www.developers.meethue.com/documentation/lights-api#16_set_light_state   1.6. Set light state
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void setLightState_1_6(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String lightIdentifier) throws JsonParseException, IOException {

        String resourceUrl = "/lights/" + lightIdentifier + "/state/";

        if (bridgeConfiguration.getLights() == null || bridgeConfiguration.getLights().get(lightIdentifier) == null) {
            sendErrorResponse(lightIdentifier, "3", responseBody);
        }

        PHLightState ls = bridgeConfiguration.getLights().get(lightIdentifier).getState();

        String lightType = bridgeConfiguration.getLights().get(lightIdentifier).getType();
        JSONArray responseArray = new JSONArray();

        setLightState(resourceUrl,lightType, ls, responseArray, jSONString);

        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();

        bridgeConfiguration.getLights().get(lightIdentifier).setState(ls);
        controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson());     
    }


    public void setLightState(String baseUrl, String lightType, PHLightState ls,   JSONArray responseArray, String jSONString) {
        JSONObject jObject = new JSONObject(jSONString);
        if (jObject != null) {
            JSONArray names = jObject.names();

            boolean isOn = ls.getOn(); // An attempt to modify the state of a bulb which is off results in a bridge error.
            if (jObject.has("on")) {   // do not throw an error when 'on' is set to true.
                isOn = jObject.getBoolean("on");
            }

            for (int i=0; i<names.length(); i++) {
                JSONObject successObject = new JSONObject();

                JSONObject successLine = new JSONObject();
                String name = names.getString(i);
                String errorDescription="";
                int errorType=0;

                String resourceUrl=baseUrl + name;
                responseArray.put(successObject);

                boolean isSuccess=true;  // Success is returned for a valid fieldname, if a field name is invalid then an "error" is returned.

                if (name.equals("on")) {

                    successLine.putOpt(resourceUrl, jObject.optBoolean(name));
                    ls.setOn(jObject.optBoolean(name)); 
                }
                else if (name.equals("hue") && !lightType.equals(Constants.LIGHT_TYPE_LUX_BULB) ) {
                    int val = jObject.optInt(name);
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, hue, is not modifiable. Device is set to off.";
                    }
                    else if (Utils.isInRange(val, 0, 65535)) {
                        successLine.putOpt(resourceUrl, val);
                        ls.setHue(val); 
                    }
                    else {
                        isSuccess=false;
                        errorDescription = "invalid value, " + val + " , for parameter, hue";
                        errorType=7;
                    }
                }
                else if (name.equals("bri")) {
                    int val = jObject.optInt(name);
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, bri, is not modifiable. Device is set to off.";
                    }
                    else if (Utils.isInRange(val, 0, 255)) {
                        successLine.putOpt(resourceUrl, val);
                        ls.setBri(val); 
                    }
                    else {
                        isSuccess=false;
                        errorDescription = "invalid value, " + val + " , for parameter, bri";
                        errorType=7;
                    }
                }
                else if (name.equals("sat")  && !lightType.equals(Constants.LIGHT_TYPE_LUX_BULB)) {
                    int val = jObject.optInt(name);
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, sat, is not modifiable. Device is set to off.";
                    }
                    else if (Utils.isInRange(val, 0, 255)) {
                        successLine.putOpt(resourceUrl, val);
                        ls.setSat(val); 
                    }
                    else {
                        isSuccess=false;
                        errorDescription = "invalid value, " + val + " , for parameter, sat";
                        errorType=7;
                    }
                }
                else if (name.equals("ct")  && !lightType.equals(Constants.LIGHT_TYPE_LUX_BULB)) {
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, ct, is not modifiable. Device is set to off.";
                    }
                    else {
                        successLine.putOpt(resourceUrl, jObject.optInt(name));
                        ls.setCt(jObject.optInt(name)); 
                    }

                }
                else if (name.equals("xy")  && !lightType.equals(Constants.LIGHT_TYPE_LUX_BULB)) {
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, xy, is not modifiable. Device is set to off.";
                    }
                    else {
                        JSONArray xyArray = jObject.optJSONArray("xy");
                        successLine.putOpt(resourceUrl, xyArray);

                        float point1 = Float.valueOf(xyArray.get(0).toString());
                        float point2 = Float.valueOf(xyArray.get(1).toString());
                        PointF xy = new PointF(point1,point2);
                        xy = PHUtilitiesHelper.fixIfOutOfRange(xy, Constants.MODEL_ID_COLOR_BULB);  // If the sent x/y values are out of range, the find the closest point.
                        float[] xyFloatArray = {xy.x, xy.y};
                        int colour = PHUtilitiesHelper.colorFromXY(xyFloatArray, Constants.MODEL_ID_COLOR_BULB);

                        Color col = new Color(colour);
                        int r = col.getRed();
                        int g = col.getGreen();
                        int b = col.getBlue();
                        float[] hsv = new float[3];
                        Color.RGBtoHSB(r,g,b,hsv);

                        // Recalculate Hue
                        ls.setHue((int) (hsv[0] * 65535));
                        ls.setSat((int) (hsv[1] * 254));

                        List<Double> xyList = new ArrayList();
                        String xStr = fourDP.format(xy.x);
                        String yStr = fourDP.format(xy.y);
                        xyList.add(Double.valueOf(xStr)); 
                        xyList.add(Double.valueOf(yStr));

                        ls.setXy(xyList);
                    }
                }
                else if (name.equals("effect") && !lightType.equals(Constants.LIGHT_TYPE_LUX_BULB)) {
                    String effect = jObject.optString(name);
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, effect, is not modifiable. Device is set to off.";
                    }
                    else if (effect==null || (!effect.equals("none") && !effect.equals("colorloop"))) {
                        isSuccess=false;
                        errorDescription = "invalid value, " + effect + " , for parameter, effect";
                        errorType=7;
                    }
                    else {
                        successLine.putOpt(resourceUrl, effect);
                        ls.setEffect(effect); 
                    }
                }
                else if (name.equals("alert")) {
                    String alert = jObject.optString(name);
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, alert, is not modifiable. Device is set to off.";
                    }
                    else if (alert==null || (!alert.equals("none") && !alert.equals("select") && !alert.equals("lselect"))) {
                        isSuccess=false;
                        errorDescription = "invalid value, " + alert + " , for parameter, alert";
                        errorType=7;
                    }
                    else {
                        successLine.putOpt(resourceUrl, alert);
                        ls.setAlert(alert); 
                    }

                }
                else if (name.equals("reachable")) {
                    successLine.putOpt(resourceUrl, jObject.optBoolean(name));
                    ls.setReachable(jObject.optBoolean(name)); 
                }
                else if (name.equals("transitiontime")) {
                    int val = jObject.optInt(name);
                    if (!isOn) {
                        isSuccess=false; errorType=201;  errorDescription = "parameter, transitiontime, is not modifiable. Device is set to off.";
                    }
                    else{
                        successLine.putOpt(resourceUrl, val);
                    }

                }
                else {
                    isSuccess=false;     // Handle errors,  i.e.  Non Supported fields
                    errorDescription = "parameter, " + name + ", not available";
                    errorType=6;
                }

                if (!isSuccess) {   
                    isSuccess=false;
                    JSONObject errorLine = new JSONObject();
                    errorLine.putOpt("type", errorType);
                    errorLine.putOpt("address", resourceUrl);
                    errorLine.putOpt("description", errorDescription);
                    successObject.putOpt("error", errorLine);
                }

                if (isSuccess) { 
                    successObject.putOpt("success", successLine);
                }
            }  // End of for loop.
        }
    }
 
   public void sendErrorResponse(String lightIdentifier, String type, OutputStream responseBody) throws IOException {
       JSONArray responseArray = new JSONArray();
       JSONObject errorObject = new JSONObject();
       JSONObject errorLine = new JSONObject();
       errorLine.putOpt("type", type);
       errorLine.putOpt("address", "/lights/" + lightIdentifier);
       errorLine.putOpt("description", "resource, /lights/" + lightIdentifier + ", not available");
       errorObject.putOpt("error", errorLine);
       responseArray.put(errorObject);
       responseBody.write(responseArray.toString().getBytes());
       responseBody.close();
   }
 
}
