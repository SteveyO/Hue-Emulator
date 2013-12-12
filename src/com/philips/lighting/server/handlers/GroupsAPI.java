package com.philips.lighting.server.handlers;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.philips.lighting.emulator.Controller;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHGroupsEntry;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.utils.TestUtils;
import com.philips.lighting.utils.Utils;

public class GroupsAPI {

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.1  GET ALL GROUPS
    //  http://developers.meethue.com/2_groupsapi.html   2.1. Get all groups
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
            
            groupsResponseJson.putOpt(identifier, groupsJson);
        }
        
        responseBody.write(groupsResponseJson.toString().getBytes());
        responseBody.close();
    } 
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.2  CREATE GROUP
    //  http://developers.meethue.com/2_groupsapi.html   3.2. Create Group
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
                       
            controller.addTextToConsole(responseArray.toString(), Color.WHITE); 
    }   
    

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.3  GET GROUP ATTRIBUTES
    //  http://developers.meethue.com/2_groupsapi.html   2.3. Get group attributes
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void getGroupAttributes_2_3(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getGroups() == null || bridgeConfiguration.getGroups().get(groupIdentifier) == null) {
            sendErrorResponse(groupIdentifier, "3", responseBody);
        }
        else {
            mapper.writeValue(responseBody, bridgeConfiguration.getGroups().get(groupIdentifier));   // Write to the response.
            controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration.getGroups().get(groupIdentifier)), Color.WHITE); 
        }

    } 
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.4  SET GROUP  ATTRIBUTES
    //  http://developers.meethue.com/2_groupsapi.html   2.4. Set group attributes
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
            controller.addTextToConsole(responseArray.toString(), Color.WHITE); 
    }    

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.5  SET GROUP STATE
    //  http://developers.meethue.com/2_groupsapi.html   2.5. Set Group State
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void setGroupState_2_5(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier, LightsAPI lightsAPI) throws JsonParseException, IOException {

        String resourceUrl = "/groups/" + groupIdentifier + "/action/";

        if (bridgeConfiguration.getLights() == null) {
            sendErrorResponse(groupIdentifier, "3", responseBody);
        }

        // Temporary Code, until groups is implemented. i.e.  Only accept request for the default 0 group.
        if (!groupIdentifier.equals("0")) {
            return;
        }

        Map<String, PHLight> allLights =  bridgeConfiguration.getLights();

        JSONArray responseArray = new JSONArray();

        Iterator it = allLights.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            PHLight light = (PHLight) entry.getValue();
            PHLightState ls = light.getState();
            lightsAPI.setLightState(resourceUrl, ls, responseArray, jSONString);
            light.setState(ls);
        }
        
        // Here the Response array has duplicates (i.e. commands for each bulb) so duplicates are filtered.  Also error messages are removed, as these are not caught by a group command.
        responseArray = TestUtils.removeDuplicates(responseArray, true);

        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();
        controller.addTextToConsole(responseArray.toString(), Color.WHITE);     


    }
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  2.6  DELETE GROUP
    //  http://developers.meethue.com/2_groupsapi.html   3.5. Delete group
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void deleteGroup_2_6(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String groupIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getGroups() == null || bridgeConfiguration.getGroups().get(groupIdentifier) == null) {
            sendErrorResponse(groupIdentifier, "3", responseBody);
        }
        else {

            bridgeConfiguration.getGroups().remove(groupIdentifier);

            String resourceURL = "/groups/" + groupIdentifier + " deleted";

            JSONObject responseObject = new JSONObject();
            responseObject.putOpt("success", resourceURL);

            responseBody.write(responseObject.toString().getBytes());
            responseBody.close();

            controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration.getGroups().get(groupIdentifier)), Color.WHITE); 
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


}
