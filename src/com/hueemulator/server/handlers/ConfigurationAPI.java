package com.hueemulator.server.handlers;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hueemulator.emulator.Controller;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.model.PHConfig;
import com.hueemulator.model.PHWhitelist;
import com.hueemulator.model.PHWhitelistEntry;

public class ConfigurationAPI {




    //  http://developers.meethue.com/4_configurationapi.html   4.2. Get configuration 
    public void getConfig_4_2(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        PHConfig config = bridgeConfiguration.getConfig();
        mapper.writeValue(responseBody, config);   // Write to the response.
        controller.addTextToConsole(mapper.writeValueAsString(config), Color.WHITE);
    }


    //  http://developers.meethue.com/4_configurationapi.html   4.5. Get full state (datastore) 
    public void getFullState_4_5(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        mapper.writeValue(responseBody, bridgeConfiguration);   // Write to the response.
        controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration), Color.WHITE);  
    }

    public void createNewUsername(PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, String requestMethod) {

        if (!requestMethod.equalsIgnoreCase("POST")) {
            returnErrorResponse("3", "method, " + requestMethod + ", not available for resource, /", responseBody);
        }
        else {
            // Create A new WhiteList Username here.
        }  

    }



    public boolean isValidUserName(PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, String[] urlElements)  {

        if (urlElements.length < 3) {
            return false;
        }

        String username = urlElements[2];
        PHWhitelist whitelist = bridgeConfiguration.getConfig().getWhitelist();

        Map<String, PHWhitelistEntry> whiteListMap = whitelist.getWhitelist();

        Iterator it = whiteListMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry <String, PHWhitelistEntry> entry = (Map.Entry) it.next();
            String whiteListUsername = (String)entry.getKey();

            if (whiteListUsername.equals(username)) {
                return true;
            }
        }


        return false;  // An unauthorized user
    }

    public void returnErrorResponse(String errorType, String description, OutputStream responseBody) {
        JSONObject errorObject = new JSONObject();

        JSONArray responseArray = new JSONArray();
        responseArray.put(errorObject);

        JSONObject errorLine = new JSONObject();
        errorLine.putOpt("type", errorType);
        errorLine.putOpt("address", "/");
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
