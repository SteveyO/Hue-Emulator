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
import com.hueemulator.model.PHGroupsEntry;
import com.hueemulator.model.PHLight;
import com.hueemulator.model.PHLightState;
import com.hueemulator.utils.Utils;

public class GroupsAPI {

    
     // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.1  GET ALL GROUPS
    //  http://www.developers.meethue.com/documentation/groups-api#21_get_all_groups   2.1. Get all groups
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void getAllGroups_2_1(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        Map <String, PHGroupsEntry> groupsMap = bridgeConfiguration.getGroups();
        
        Iterator it = groupsMap.entrySet().iterator();
       
        JSONObject groupsResponseJson = new JSONObject();
        
        while (it.hasNext()) {
            Map.Entry <String, PHGroupsEntry> entry = (Map.Entry) it.next();
            String identifier = (String)entry.getKey();
            PHGroupsEntry group = (PHGroupsEntry) entry.getValue();
            
            JSONObject groupsJson = new JSONObject();
            groupsJson.putOpt("name", group.getName());
            
            groupsJson.putOpt("lights", group.getLightIdentifiers());
            
            JSONObject actionJson = new JSONObject(group.getLightState());
            groupsJson.putOpt("action", actionJson);
            
            groupsResponseJson.putOpt(identifier, groupsJson);
        }
        
        responseBody.write(groupsResponseJson.toString().getBytes());
        responseBody.close();
    } 
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.2  CREATE GROUP
    //  http://www.developers.meethue.com/documentation/groups-api#22_create_group   3.2. Create Group
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void createGroup_2_2(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {

          PHGroupsEntry groupObject = new PHGroupsEntry();
          
          String responseBase = "/groups/";
          String resourceUrl="";
          String errorDescription="";
          
          String name="group";
          JSONArray responseArray = new JSONArray();
          String nextGroupNumber = Integer.toString(bridgeConfiguration.getGroups().size() + 1);
          
            JSONObject jObject = new JSONObject(jSONString);
            if (jObject != null) {
                JSONArray names = jObject.names();
              
                boolean isSuccess=true;  // Success is returned for a valid fieldname, if a field name is invalid then an "error" is returned.
                JSONObject successObject = new JSONObject();
                responseArray.put(successObject);
                
                 for (int i=0; i<names.length(); i++) {              
                    name = names.getString(i);
                    
                    resourceUrl = responseBase + name;
                    
                    if (name.equals("name")) {
                          String groupName = jObject.optString(name);
                          
                          if (groupName == null || groupName.length() > 32) {
                              errorDescription = "invalid value, " + groupName + ", for parameter, name";
                              isSuccess=false;
                          }
                          else {                          
                              groupObject.setName(groupName);                           
                          }
                    }
                    else if (name.equals("lights")) {
                        JSONArray lightsArray = jObject.optJSONArray("lights");
                        List<String> lightIdentifiers = new ArrayList();
                        
                        for (int a=0; a< lightsArray.length(); a++) {
                            lightIdentifiers.add(lightsArray.get(a).toString());
                        }
                        
                        groupObject.setLightIdentifiers(lightIdentifiers);
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
                    idObject.put("id", responseBase + nextGroupNumber);
                            
                    successObject.putOpt("success",  idObject);
                    
                    bridgeConfiguration.getGroups().put(nextGroupNumber, groupObject);
                }
                
            }  // JObject !=null

            responseBody.write(responseArray.toString().getBytes());
            responseBody.close();
                       
            controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    }   
    

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.3  GET GROUP ATTRIBUTES
    //  http://www.developers.meethue.com/documentation/groups-api#23_get_group_attributes   2.3. Get group attributes
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void getGroupAttributes_2_3(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getGroups() == null || bridgeConfiguration.getGroups().get(groupIdentifier) == null) {
            sendErrorResponse(groupIdentifier, "3", responseBody);
        }
        else {
            mapper.writeValue(responseBody, bridgeConfiguration.getGroups().get(groupIdentifier));   // Write to the response.
            controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration.getGroups().get(groupIdentifier)), Color.WHITE, controller.showResponseJson()); 
        }

    } 
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.4  SET GROUP  ATTRIBUTES
    //  http://www.developers.meethue.com/documentation/groups-api#24_set_group_attributes   2.4. Set group attributes
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void setGroupAttributes_2_4(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier) throws JsonParseException, IOException {          

          if (bridgeConfiguration.getGroups() == null || bridgeConfiguration.getGroups().get(groupIdentifier) == null) {
              sendErrorResponse(groupIdentifier, "3", responseBody);
           }
          
          PHGroupsEntry groupObject = bridgeConfiguration.getGroups().get(groupIdentifier);
          String responseBase = "/groups/" + groupIdentifier + "/";
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
                          String lightName = jObject.optString(name);
                          
                          if (lightName == null || lightName.length() > 32) {
                              isSuccess=false;
                          }
                          else {
                              successLine.putOpt(resourceUrl, lightName);
                              groupObject.setName(lightName);                             
                          }
                          
                    }
                    else if (name.equals("lights")) {
                        JSONArray lightsArray = jObject.optJSONArray("lights");
                        List<String> lightIdentifiers = new ArrayList();
                        
                       
                        if (lightsArray.length() == 0) {
                           isSuccess=false;     
                        }
                        else {
                            for (int a=0; a< lightsArray.length(); a++) {
                                lightIdentifiers.add(lightsArray.get(a).toString());
                            }
                            successLine.putOpt(resourceUrl, lightIdentifiers);
                            groupObject.setLightIdentifiers(lightIdentifiers);
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
            bridgeConfiguration.getGroups().put(groupIdentifier, groupObject);  
            controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    }    

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.5  SET GROUP STATE
    //  http://www.developers.meethue.com/documentation/groups-api#25_set_group_state   2.5. Set Group State
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void setGroupState_2_5(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier, LightsAPI lightsAPI) throws JsonParseException, IOException {

        String resourceUrl = "/groups/" + groupIdentifier + "/action/";

        if (bridgeConfiguration.getLights() == null) {
            sendErrorResponse(groupIdentifier, "3", responseBody);
        }

        Map<String, PHLight> allLights;
        
        if (groupIdentifier.equals("0")) {   // 0 is the default 'all lights' group
            allLights =  bridgeConfiguration.getLights();
        }
        else {
            allLights = new HashMap<String, PHLight>();  
            
            Map <String, PHLight> lightsMap = bridgeConfiguration.getLights();
            PHGroupsEntry group = bridgeConfiguration.getGroups().get(groupIdentifier);  // Get the selected group, so we can filter out the lights in this group.

            Iterator it = lightsMap.entrySet().iterator();
            
            while (it.hasNext()) {
                Map.Entry <String, PHLight> entry = (Map.Entry) it.next();
                String identifier = (String)entry.getKey();
                PHLight light = (PHLight) entry.getValue();

                if (group.getLightIdentifiers().contains(identifier)) {
                    allLights.put(identifier, light);
                }
            }
        }

        JSONArray responseArray = new JSONArray();
        
        if (jSONString.indexOf("\"scene\"") != -1) {   // Scene recall here (http://www.developers.meethue.com/documentation/scenes-api#44_recall_scene)
            JSONObject jsonObject = new JSONObject(jSONString);                
            String sceneId = jsonObject.optString("scene");
            System.out.println("Recalling scene: " + sceneId);
            if (ScenesAPI.emulatorScenes.containsKey(sceneId)) {  // i.e. This scene has been set
                for (PHLight lightInScene: ScenesAPI.emulatorScenes.get(sceneId)) {
                    lightInScene.setState(lightInScene.getState());
                    
                 // Clone the light state object here (using the copy constructor), so the stored scene lights do not get overwritten.
                    PHLightState newState = new PHLightState(lightInScene.getState());

                    System.out.println("Recall scene hue is: " + newState.getHue());
                    JSONObject jsonLightStateObject = newState.serializeLightState(newState);
                    
                    System.out.println("    JSON STRING: "+ jsonLightStateObject.toString());
                     // TODO Instead of the below Send the JSON String to the  lightsAPI.setLightState so we get the success response from the bridge.
                    bridgeConfiguration.getLights().get(lightInScene.getIdentifier()).setState(newState);  
                     
                }                   
            }
        }
        else {

            Iterator it = allLights.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                PHLight light = (PHLight) entry.getValue();            
                PHLightState ls = light.getState();

                lightsAPI.setLightState(resourceUrl, light.getModelid(), ls, responseArray, jSONString);
                light.setState(ls);
            }
        }
        
        // Here the Response array has duplicates (i.e. commands for each bulb) so duplicates are filtered.  Also error messages are removed, as these are not caught by a group command.
        responseArray = removeDuplicates(responseArray, true);

        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();
        controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson());     

    }

    public static JSONArray removeDuplicates(JSONArray originalArray, boolean removeErrors) {

        List currentObjs = new ArrayList();
        JSONArray newArray = new JSONArray();
        boolean includeLine=true;

        for (int i=0; i< originalArray.length(); i++) {
            Object obj = originalArray.get(i);
            String jsonString =  originalArray.getJSONObject(i).toString();

            includeLine=true;

            if (removeErrors && jsonString.startsWith("{\"error\":{")) {
                includeLine=false;
            }

            if (!currentObjs.contains(obj)) {
                if (includeLine) {
                    newArray.put(obj);
                }
            }

            currentObjs.add(obj);
        }

        return newArray;
    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.6  DELETE GROUP
    //  http://www.developers.meethue.com/documentation/groups-api#26_delete_group   3.5. Delete group
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void deleteGroup_2_6(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getGroups() == null || bridgeConfiguration.getGroups().get(groupIdentifier) == null) {
            sendErrorResponse(groupIdentifier, "3", responseBody);
        }
        else {

            bridgeConfiguration.getGroups().remove(groupIdentifier);

            String resourceURL = "/groups/" + groupIdentifier + " deleted";

            JSONArray jsonArray = new JSONArray();
            JSONObject responseObject = new JSONObject();
            responseObject.putOpt("success", resourceURL);
            jsonArray.put(responseObject);
            String responseText = jsonArray.toString();
            responseBody.write(responseText.getBytes());
            responseBody.close();
            controller.addTextToConsole(responseText, Color.WHITE, controller.showResponseJson()); 
        }

    }

    public void sendErrorResponse(String groupIdentifier, String type, OutputStream responseBody) throws IOException {
        JSONArray responseArray = new JSONArray();
        JSONObject errorObject = new JSONObject();
        JSONObject errorLine = new JSONObject();
        errorLine.putOpt("type", type);
        errorLine.putOpt("address", "/groups/" + groupIdentifier);
        errorLine.putOpt("description", "resource, /groups/" + groupIdentifier + ", not available");
        errorObject.putOpt("error", errorLine);
        responseArray.put(errorObject);
        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();
    }
    
    // Returns a list of PHLight objects from a list of Strings (Light Identifiers).
    public List<PHLight> getLightObjectsFromLightIds(PHBridgeConfiguration bridgeConfiguration, List <String>lightIdentifiers) {
        List <PHLight> phLightList = new ArrayList<PHLight>();
        
        for (String sceneLight: lightIdentifiers) {
            phLightList.add(bridgeConfiguration.getLights().get(sceneLight));
        }
        return phLightList;
    }

}
