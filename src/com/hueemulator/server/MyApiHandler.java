package com.hueemulator.server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import com.hueemulator.emulator.Controller;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.server.handlers.ConfigurationAPI;
import com.hueemulator.server.handlers.GroupsAPI;
import com.hueemulator.server.handlers.LightsAPI;
import com.hueemulator.server.handlers.ScenesAPI;
import com.hueemulator.server.handlers.SchedulesAPI;
import com.hueemulator.utils.Utils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class MyApiHandler implements HttpHandler {

    private PHBridgeConfiguration bridgeConfiguration;
    private Controller controller;
    private LightsAPI lightsAPIhandler;
    private ConfigurationAPI configurationAPIhandler;
    private GroupsAPI groupsAPIhandler;
    private SchedulesAPI schedulesAPIhandler;
    private ScenesAPI    scenesAPIhandler;

    public MyApiHandler(PHBridgeConfiguration bridgeConfiguration, Controller controller) {
        this.bridgeConfiguration = bridgeConfiguration;
        this.controller          = controller;

        lightsAPIhandler = new LightsAPI();
        groupsAPIhandler = new GroupsAPI();
        schedulesAPIhandler = new SchedulesAPI();
        scenesAPIhandler    = new ScenesAPI();
        configurationAPIhandler = new ConfigurationAPI();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String url = exchange.getRequestURI().toString();

        OutputStream responseBody = exchange.getResponseBody();
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        responseHeaders.set("Pragma", "no-cache");
        responseHeaders.set("Expires", "Mon, 1 Aug 2011 09:00:00 GMT");
        responseHeaders.set("Connection", "close");  // Not sure if the server will actually close the connections by just setting the header
        responseHeaders.set("Access-Control-Max-Age", "0");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        responseHeaders.set("Access-Control-Allow-Credentials", "true");
        responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        responseHeaders.set("Access-Control-Allow-Headers", "Content-Type");
        if (url.contains("description.xml")) {
            responseHeaders.set("Content-Type", "application/xml; charset=utf-8"); 
        }
        else {
            responseHeaders.set("Content-Type", "application/json; charset=utf-8");
        }
        
        exchange.sendResponseHeaders(200, 0);
        ObjectMapper mapper = new ObjectMapper();

        String urlElements[] = url.split("/");   
        controller.addTextToConsole(url.toString(), Color.gray, true);
        if (url.equals("/api") || url.equals("/api/")) {           
            configurationAPIhandler.createNewUsername(bridgeConfiguration, responseBody, requestMethod);
        }
        else if (url.equals("/api/config")|| url.equals("/api/config/")) {           
            configurationAPIhandler.returnNonAuthenticatedConfig(bridgeConfiguration, responseBody);
        }
        // Check if username is on the whitelist.  If not a JSON "Unauthorized User" response is sent back.
        else if (!configurationAPIhandler.isValidUserName(bridgeConfiguration, responseBody, urlElements)) {            
            configurationAPIhandler.returnErrorResponse("1", "unauthorized user", "/", responseBody);
            return;
        }
        
        responseHeaders.set("Content-Type", "application/json; charset=utf-8");

        if (requestMethod.equalsIgnoreCase("GET")) {
            handleGet(mapper, url, responseBody, urlElements);
            responseBody.close();
        }
        else if (requestMethod.equalsIgnoreCase("DELETE")) {
            handleDelete(mapper, responseBody, urlElements);
            responseBody.close();
        }
        else  if (requestMethod.equalsIgnoreCase("PUT") || requestMethod.equalsIgnoreCase("POST")) {

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);

            String jSONString="";
            String line="";
            while ((line = br.readLine()) != null) {
                jSONString += line;
            }

            // Check the json is valid, if not return the same response as the bridge.
            boolean isValidJSON = Utils.isJSONValid(jSONString);
            
            if (!isValidJSON) {     
                configurationAPIhandler.returnErrorResponse("2", "body contains invalid json", url, responseBody);
            }

            controller.addTextToConsole(jSONString, Color.gray, controller.showRequestJson());   // Show the JSON we are sending to the Bridge (i.e. Emulator) in the console.            

            
            if (requestMethod.equalsIgnoreCase("PUT")) {
                handlePut(mapper, url, responseBody, jSONString, urlElements);
            }
            if (requestMethod.equalsIgnoreCase("POST")) {
                handlePost(mapper, url, responseBody, jSONString, urlElements);
            }
        } else { // probably OPTIONS
            responseBody.close();        	
        }
    }

    public void handlePut(ObjectMapper mapper, String url, OutputStream responseBody, String jSONString, String[] urlElements) throws JsonParseException, IOException  {
        int noURLEelements=urlElements.length;
        String lastURLElement = urlElements[noURLEelements-1];

        if (urlElements[noURLEelements-2].equals("lights")) {
            String light=urlElements[noURLEelements-1];
            lightsAPIhandler.setLightAttributes_1_5(mapper, jSONString, bridgeConfiguration, responseBody, controller, light);
        }
        else if (lastURLElement.equals("name")) {   // This is a temporary fudge fix for the Java SDK.  It is appending /name to the Update Lights URL.
            String light=urlElements[noURLEelements-2];
            lightsAPIhandler.setLightAttributes_1_5(mapper, jSONString, bridgeConfiguration, responseBody, controller, light);
        }
        else if (lastURLElement.equals("state")) {
            lightsAPIhandler.setLightState_1_6(mapper, jSONString, bridgeConfiguration, responseBody, controller, urlElements[noURLEelements-2]);
        }
        else if (lastURLElement.equals("action")) {
            groupsAPIhandler.setGroupState_2_5(mapper, jSONString, bridgeConfiguration, responseBody, controller, urlElements[noURLEelements-2],lightsAPIhandler);
        }
        else if (urlElements[noURLEelements-2].equals("groups")) {
            String groupIdentifier=urlElements[noURLEelements-1];         
            groupsAPIhandler.setGroupAttributes_2_4(mapper, jSONString, bridgeConfiguration, responseBody, controller, groupIdentifier);
        } 
        else if (urlElements[noURLEelements-2].equals("schedules")) {
            String scheduleIdentifier=urlElements[noURLEelements-1];         
            schedulesAPIhandler.setScheduleAttributes_3_4(mapper, jSONString, bridgeConfiguration, responseBody, controller, scheduleIdentifier);
        } 
        else if (urlElements[noURLEelements-2].equals("scenes")) {
            String sceneIdentifier=urlElements[noURLEelements-1];         
            scenesAPIhandler.createScene_4_2(mapper, jSONString, bridgeConfiguration, responseBody, controller, sceneIdentifier);
        } 
    }

    public void handlePost(ObjectMapper mapper, String url, OutputStream responseBody, String jSONString, String[] urlElements) throws JsonParseException, IOException  {
        int noURLEelements=urlElements.length;
        String lastURLElement = urlElements[noURLEelements-1];

        if (lastURLElement.equals("schedules")) {
            schedulesAPIhandler.createSchedule_3_2(mapper, jSONString, bridgeConfiguration, responseBody, controller);
        }
        else if (lastURLElement.equals("groups")) {
            groupsAPIhandler.createGroup_2_2(mapper, jSONString, bridgeConfiguration, responseBody, controller);
        }
        else if (lastURLElement.equals("api")) {
            configurationAPIhandler.createUser_7_1(mapper, jSONString, bridgeConfiguration, responseBody, controller);
        }

    }

    public void handleGet(ObjectMapper mapper, String url, OutputStream responseBody, String[] urlElements) throws JsonGenerationException, IOException {

        int noURLEelements=urlElements.length;
        String lastURLElement = urlElements[noURLEelements-1];

        // URL Ends with /lights, 
        if (lastURLElement.equals("lights")) {
            lightsAPIhandler.getAllLights_1_1(mapper, bridgeConfiguration, responseBody, controller);
        }
        else if (urlElements[noURLEelements-2].equals("lights")) {
            String light=urlElements[noURLEelements-1];
            lightsAPIhandler.getLightAttributes_1_4(mapper, bridgeConfiguration, responseBody, controller, light);
        }
        else if (lastURLElement.equals("groups")) {
            groupsAPIhandler.getAllGroups_2_1(mapper, bridgeConfiguration, responseBody, controller);
        }
        else if (lastURLElement.equals("schedules")) {  
            schedulesAPIhandler.getAllSchedules_3_1(bridgeConfiguration, responseBody, controller);
        }
        else if (lastURLElement.equals("scenes")) {  
            scenesAPIhandler.getAllScenes_4_1(mapper, bridgeConfiguration, responseBody, controller);
        }
        else if (lastURLElement.equals("config")) {  
            configurationAPIhandler.getConfig_7_2(mapper, bridgeConfiguration, responseBody, controller);
        }
        else if (urlElements[noURLEelements-2].equals("schedules")) {
            String scheduleId=urlElements[noURLEelements-1];
            schedulesAPIhandler.getScheduleAttributes_3_3(mapper, bridgeConfiguration, responseBody, controller, scheduleId);
        }
        else if (urlElements[noURLEelements-2].equals("groups")) {
            String groupId=urlElements[noURLEelements-1];
            groupsAPIhandler.getGroupAttributes_2_3(mapper, bridgeConfiguration, responseBody, controller, groupId);
        }
        else {
            configurationAPIhandler.getFullState_7_5(mapper, bridgeConfiguration, responseBody, controller);       
        }
    }

    public void handleDelete(ObjectMapper mapper, OutputStream responseBody, String[] urlElements) throws JsonParseException, IOException  {
        int noURLEelements=urlElements.length;
        String lastURLElement = urlElements[noURLEelements-1];

        if (urlElements[noURLEelements-2].equals("schedules")) {
            schedulesAPIhandler.deleteSchedule_3_5(mapper, bridgeConfiguration, responseBody, controller, lastURLElement);
        }           
        else if (urlElements[noURLEelements-2].equals("groups")) {
            groupsAPIhandler.deleteGroup_2_6(mapper, bridgeConfiguration, responseBody, controller, lastURLElement);
        }         
        else if (urlElements[noURLEelements-2].equals("whitelist")) {
            controller.addTextToConsole("Handling delete for whitelist entry: " + lastURLElement, Color.RED, true);
            configurationAPIhandler.deleteUser_7_4(mapper, bridgeConfiguration, responseBody, controller, lastURLElement);
        }           

    }

}