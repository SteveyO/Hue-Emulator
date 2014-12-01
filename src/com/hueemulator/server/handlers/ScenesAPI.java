package com.hueemulator.server.handlers;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hueemulator.emulator.Controller;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.model.PHLight;
import com.hueemulator.model.PHLightState;
import com.hueemulator.model.PHScenesEntry;

public class ScenesAPI {
    
    // In the bridge, the light states for each scene are stored in the bulbs (not in the Bridge JSON/COnfig). Hence this map is for storing the LightStates for each scene.
    public static Map<String, List<PHLight>> emulatorScenes = new HashMap<String, List<PHLight>>();      
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  4.1  GET ALL SCENES
    //  http://www.developers.meethue.com/documentation/scenes-api#41_get_all_scenes   4.1. Get all scenes
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void getAllScenes_4_1(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        Map <String, PHScenesEntry> scenesMap = bridgeConfiguration.getScenes();
        
        Iterator it = scenesMap.entrySet().iterator();
       
        JSONObject scenesResponseJson = new JSONObject();
        
        while (it.hasNext()) {
            Map.Entry <String, PHScenesEntry> entry = (Map.Entry) it.next();
            String identifier = (String) entry.getKey();
            PHScenesEntry scene = (PHScenesEntry) entry.getValue();
            
            JSONObject scenesJson = new JSONObject();
            scenesJson.putOpt("name", scene.getName());
            scenesJson.putOpt("active", true);
            
            JSONArray lightsArray = new JSONArray();
            for (String lightID: scene.getLights()) {
                lightsArray.put(lightID);
            }
            scenesJson.put("lights", lightsArray);
            scenesResponseJson.putOpt(identifier, scenesJson);
        }
        
        responseBody.write(scenesResponseJson.toString().getBytes());
        responseBody.close();
    } 
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  4.2  CREATE SCENES SCENE
    //  http://www.developers.meethue.com/documentation/scenes-api#42_create_scene   4.2. Create Scene
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void createScene_4_2(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String sceneIdentifier) throws JsonParseException, IOException {

        PHScenesEntry sceneObject = new PHScenesEntry();

        String responseBase = "/scenes/";
        String resourceUrl="";
        String errorDescription="";

        String name="scene";
        JSONArray responseArray = new JSONArray();

        JSONObject jObject = new JSONObject(jSONString);
        if (jObject != null) {
            JSONArray names = jObject.names();

            boolean isSuccess=true;  // Success is returned for a valid fieldname, if a field name is invalid then an "error" is returned.
            JSONObject successObject = new JSONObject();
            List<PHLight> lightsList = new ArrayList<PHLight>(); // A list of all light objects that are part of the scene.
            responseArray.put(successObject);

            for (int i=0; i<names.length(); i++) {              
                name = names.getString(i);

                resourceUrl = responseBase + name;

                if (name.equals("name")) {
                    String sceneName = jObject.optString(name);

                    if (sceneName == null || sceneName.length() > 16) {
                        errorDescription = "invalid value, " + sceneName + ", for parameter, name";
                        isSuccess=false;
                    }
                    else {                          
                        sceneObject.setName(sceneName);                           
                    }
                }
                else if (name.equals("lights")) {
                    JSONArray lightsArray = jObject.optJSONArray("lights");
                    List<String> lightIdentifiers = new ArrayList<String>();

                    for (int a=0; a< lightsArray.length(); a++) {
                        lightIdentifiers.add(lightsArray.get(a).toString());
                    }
                    lightsList.addAll(cloneLightObjectsFromLightIds(bridgeConfiguration, lightIdentifiers));
                    sceneObject.setLights(lightIdentifiers);
                }

                if (!isSuccess)  {   // Handle errors,  i.e.  Non Supported fields
                    JSONObject errorLine = new JSONObject();
                    errorLine.putOpt("type", 7);
                    errorLine.putOpt("address", resourceUrl);
                    errorLine.putOpt("description", errorDescription);
                    successObject.putOpt("error", errorLine);
                    break;
                } 

            }  // For Names

            if (isSuccess) { 
                JSONObject idObject = new JSONObject();
                idObject.put("id", responseBase + sceneIdentifier);

                successObject.putOpt("success",  idObject);
                emulatorScenes.put(sceneIdentifier,lightsList);

                bridgeConfiguration.getScenes().put(sceneIdentifier, sceneObject);
            }

        }  // JObject !=null

        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();

        controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    }    
    
    
  // Returns a list of PHLight objects from a list of Strings (Light Identifiers).
  public List<PHLight> cloneLightObjectsFromLightIds(PHBridgeConfiguration bridgeConfiguration, List <String>lightIdentifiers) {
      List <PHLight> phLightList = new ArrayList<PHLight>();
      
      for (String sceneLightIdentifier: lightIdentifiers) {
          PHLight light = bridgeConfiguration.getLights().get(sceneLightIdentifier);
          
          // Clone both the Light State object (which is saved per scene) and light, otherwise they can get modified by other lightUpdate calls.          
          PHLightState savedState = new PHLightState(light.getState());             
          PHLight savedLight= new PHLight(light);
          savedLight.setIdentifier(sceneLightIdentifier);
          savedLight.setState(savedState);
          
          phLightList.add(savedLight);            
      }
      return phLightList;
  }
    
    
}
