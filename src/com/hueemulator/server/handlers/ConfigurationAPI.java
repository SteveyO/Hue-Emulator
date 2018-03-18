package com.hueemulator.server.handlers;

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

import com.hueemulator.emulator.Controller;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.model.PHConfig;
import com.hueemulator.model.PHWhitelistEntry;
import com.hueemulator.utils.Utils;

public class ConfigurationAPI {

    // Used for Pushlinking.  Stores all usernames (that are being created).
    // For pushlinking you have to click anywhere on the bridge panel
    public List<String> users = new ArrayList<String>();    

    public void getBridgeDescription(OutputStream responseBody, String ipAddressAndPort) {
        String descriptionFile = "";
        try {
           descriptionFile = Utils.loadDescriptionFile("/description.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        descriptionFile = descriptionFile.replace("##URLBASE##", ipAddressAndPort);
        
        try {
            responseBody.write(descriptionFile.getBytes());
            responseBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  7.1  CREATE USER
    //  http://www.developers.meethue.com/documentation/configuration-api#71_create_user   7.1  Create User
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    
    
    public void createUser_7_1(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller)  throws JsonParseException, IOException {

            PHWhitelistEntry  whitelistEntry = new PHWhitelistEntry();
            
            String responseBase = "/api/";
            String resourceUrl="";
            String errorDescription="";
            String userName="";
            
            String name="";
            String errorCode = "7";
            
            JSONArray responseArray = new JSONArray();
            
              JSONObject jObject = new JSONObject(jSONString);
              if (jObject != null) {
                  JSONArray names = jObject.names();
                
                  boolean isSuccess=true;  // Success is returned for a valid fieldname, if a field name is invalid then an "error" is returned.
                  JSONObject successObject = new JSONObject();
                  responseArray.put(successObject);
                  
                   for (int i=0; i<names.length(); i++) {              
                      name = names.getString(i);
                      
                      resourceUrl = responseBase + name;
                      
                      userName = Utils.generateRandomUsername();
                      
                     if (!controller.isHasBridgeBeenPushLinked() ) {
                          controller.setHasBridgeBeenPushLinked(false);
                          errorDescription = "link button not pressed";
                          controller.addTextToConsole("Mouse click anywhere close to the bridge image (Graphical View) to simulate Push linking the bridge.", Color.RED, controller.showResponseJson());
                          isSuccess=false; 
                          errorCode="101";
                          resourceUrl="";   // Copying what the bridge returns here.
                          users.add(userName);
                      }
                      else if (controller.isHasBridgeBeenPushLinked()){
                          isSuccess=true;
                      }
                     
                       if (name.equals("devicetype")) {
                          String devicetype = jObject.optString(name);
                          
                          if (devicetype == null || devicetype.length() > 40) {
                              errorDescription = "invalid value, " + devicetype + ", for parameter, devicetype";
                              isSuccess=false;
                          }
                          else {                          
                              whitelistEntry.setName(devicetype);                           
                          }
                      }
                      
                      if (!isSuccess)  {   // Handle errors,  i.e.  Non Supported fields
                          JSONObject errorLine = new JSONObject();
                          errorLine.putOpt("type", errorCode);
                          errorLine.putOpt("address", resourceUrl);
                          errorLine.putOpt("description", errorDescription);
                          successObject.putOpt("error", errorLine);
                          break;
                         } 

                  }  // For Names
                  
                  if (isSuccess) { 
                      JSONObject idObject = new JSONObject();
                      idObject.put("username", userName);
                      successObject.putOpt("success",  idObject);
                      
                      whitelistEntry.setCreateDate(Utils.getCurrentDate());
                      whitelistEntry.setLastUseDate(Utils.getCurrentDate());
                      bridgeConfiguration.getConfig().getWhitelist().put(userName, whitelistEntry);
                  }
                  
              }  // JObject !=null

              responseBody.write(responseArray.toString().getBytes());
              responseBody.close();
                         
              controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  7.2  GET CONFIGURATION
    //  http://www.developers.meethue.com/documentation/configuration-api#72_get_configuration   7.2 Get Configuration
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void getConfig_7_2(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        PHConfig config = bridgeConfiguration.getConfig();
        mapper.writeValue(responseBody, config);   // Write to the response.
        controller.addTextToConsole(mapper.writeValueAsString(config), Color.WHITE, controller.showResponseJson());
    }
    
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  7.4  DELETE USER FROM WHITELIST
    //  http://www.developers.meethue.com/documentation/configuration-api#74_delete_user_from_whitelist   7.4 Delete user from whitelist
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    

    public void deleteUser_7_4(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String removingUsername) throws JsonParseException, IOException {
    	bridgeConfiguration.getConfig().getWhitelist().remove(removingUsername);
        JSONObject responseObj = new JSONObject();
        responseObj.put("success", "/config/whitelist/" + removingUsername + " deleted.");
        mapper.writeValue(responseBody, responseObj);   // Write to the response.
        controller.addTextToConsole(mapper.writeValueAsString(responseObj), Color.WHITE, controller.showResponseJson());
    }
    
    

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  7.5  GET FULL STATE (DATASTORE)
    //  http://www.developers.meethue.com/documentation/configuration-api#75_get_full_state_datastore   7.5 Get Full State (DataStore
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=    
    
    public void getFullState_7_5(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        mapper.writeValue(responseBody, bridgeConfiguration);   // Write to the response.
        if (controller.getModel().isShowFullConfig()) {   // The full config can be large, and displayed every 10 seconds if an app has it enabled, so we have the option to hide this.
           controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration), Color.WHITE, controller.showResponseJson());
        }
    }

    public boolean isValidUserName(PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, String[] urlElements)  {

        if (urlElements.length < 3) {
            return false;
        }

        String username = urlElements[2];
        Map<String, PHWhitelistEntry> whiteListMap = bridgeConfiguration.getConfig().getWhitelist();

        Iterator it = whiteListMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry <String, PHWhitelistEntry> entry = (Map.Entry) it.next();
            String whiteListUsername = (String)entry.getKey();

            if (whiteListUsername.equals(username)) {
            	whiteListMap.get(username).setLastUseDate(Utils.getCurrentDate());  // Set the last use date.
                return true;
            }
        }


        return false;  // An unauthorized user
    }
    

    public void createNewUsername(PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, String requestMethod) {

        if (!requestMethod.equalsIgnoreCase("POST")) {
            returnErrorResponse("3", "method, " + requestMethod + ", not available for resource, /", "/", responseBody);
        }

    }
    
    // The Hue Bridge returns some fields when a <IP ADDRESS>/api/config is sent (with no authenticated usernames). See 'Bridge Information without a valid user" here: http://www.developers.meethue.com/documentation/message-structure-and-response-0
    public void returnNonAuthenticatedConfig(PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody) {
        JSONObject responseObject = new JSONObject();
        responseObject.put("name",      bridgeConfiguration.getConfig().getName());
        responseObject.put("mac",       bridgeConfiguration.getConfig().getMac());
        responseObject.put("bridgeid",  bridgeConfiguration.getConfig().getBridgeid());
        responseObject.put("modelid",   bridgeConfiguration.getConfig().getModelid());
        
        try {
            responseBody.write(responseObject.toString().getBytes());
            responseBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void returnErrorResponse(String errorType, String description, String address, OutputStream responseBody) {
        JSONObject errorObject = new JSONObject();

        JSONArray responseArray = new JSONArray();
        responseArray.put(errorObject);

        JSONObject errorLine = new JSONObject();
        errorLine.putOpt("type", errorType);
        errorLine.putOpt("address", address);
        errorLine.putOpt("description", description);
        errorObject.putOpt("error", errorLine);


        try {
            responseBody.write(responseArray.toString().getBytes());
            responseBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
