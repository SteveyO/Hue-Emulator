package com.hueemulator.server.handlers;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hueemulator.emulator.Controller;
import com.hueemulator.emulator.PHScheduleTimer;
import com.hueemulator.emulator.PHScheduleTimerManager;
import com.hueemulator.model.PHBody;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.model.PHCommand;
import com.hueemulator.model.PHSchedulesEntry;
import com.hueemulator.utils.Utils;

public class SchedulesAPI {


    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  3.1  GET ALL SCHEDULESCREATE SCHEDULE
    //  http://developers.meethue.com/3_schedulesapi.html   3.1. Get All Schedules
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void getAllSchedules_3_1(PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        Map <String, PHSchedulesEntry> lightsMap = bridgeConfiguration.getSchedules();

        Iterator it = lightsMap.entrySet().iterator();

        JSONObject lightsJson = new JSONObject();

        while (it.hasNext()) {
            Map.Entry <String, PHSchedulesEntry> entry = (Map.Entry) it.next();
            String identifier = (String)entry.getKey();
            PHSchedulesEntry schedule = (PHSchedulesEntry) entry.getValue();

            JSONObject lightJson = new JSONObject();
            lightJson.putOpt("name",        schedule.getName());
            lightJson.putOpt("description", schedule.getDescription());
            lightJson.putOpt("time",        schedule.getTime());
            
            JSONObject commandObject = new JSONObject();
            commandObject.putOpt("address", schedule.getCommand().getAddress());
            
            
            JSONObject commandBody = new JSONObject();
            PHBody body = schedule.getCommand().getBody();
            if (body.getOn()  !=null) commandBody.put("on",  body.getOn());
            if (body.getBri() !=null) commandBody.put("bri", body.getBri());
            if (body.getXy()  !=null) commandBody.put("xy",  body.getXy());
            if (body.getScene()  !=null) commandBody.put("scene",  body.getScene());
            
            
            commandObject.putOpt("body",  commandBody);
            commandObject.putOpt("method",  schedule.getCommand().getMethod());
            lightJson.putOpt("command",   commandObject);

            lightsJson.putOpt(identifier, lightJson);
        }

        responseBody.write(lightsJson.toString().getBytes());
        responseBody.close();

    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  3.2  CREATE SCHEDULE
    //  http://developers.meethue.com/3_schedulesapi.html   3.2. Create Schedule
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void createSchedule_3_2(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller) throws JsonParseException, IOException {
        PHSchedulesEntry scheduleObject = new PHSchedulesEntry();

        String responseBase = "/schedules/";
        String resourceUrl="";
        String errorDescription="";

        String name="schedule";
        JSONArray responseArray = new JSONArray();
        String nextScheduleNumber = Integer.toString(bridgeConfiguration.getSchedules().size() + 1);

        JSONObject jObject = new JSONObject(jSONString);
        if (jObject != null) {
            JSONArray names = jObject.names();

            boolean isSuccess=true;  // Success is returned for a valid fieldname, if a field name is invalid then an "error" is returned.
            JSONObject successObject = new JSONObject();
            responseArray.put(successObject);

            JSONObject commandObject=null;  // This JSON (String) will be stored in the ScheduleTimer object, so will be sent to the Emulator when the Schedule Time occurs.

            for (int i=0; i<names.length(); i++) {              
                name = names.getString(i);

                resourceUrl = responseBase + name;

                if (name.equals("name")) {
                    String scheduleName = jObject.optString(name);

                    if (scheduleName == null || scheduleName.length() > 32) {
                        errorDescription = "invalid value, " + scheduleName + ", for parameter, name";
                        isSuccess=false;
                    }
                    else {                          
                        scheduleObject.setName(scheduleName);                           
                    }
                }
                else if (name.equals("description")) {
                    String scheduleDescription = jObject.optString(name);

                    if (scheduleDescription == null || scheduleDescription.length() > 32) {
                        errorDescription = "invalid value, " + scheduleDescription + ", for parameter, description";
                        isSuccess=false;
                    }
                    else {                          
                        scheduleObject.setDescription(scheduleDescription);                          
                    }

                }
                else if (name.equals("time")) {
                    String scheduleTime = jObject.optString(name);

                    // If date is not correctly formatted or in the past an error is returned.
                    if (!Utils.isDateValid(scheduleTime)) {
                        isSuccess=false;
                        errorDescription = "invalid value, " + scheduleTime + ", for parameter, time";
                    }

                    scheduleObject.setTime(scheduleTime);                           
                }
                else if (name.equals("command")) {
                    commandObject = jObject.optJSONObject("command");
                    PHCommand command = new PHCommand();
                    command.setAddress(commandObject.optString("address"));
                    command.setMethod(commandObject.optString("method"));

                    JSONObject bodyObject = commandObject.optJSONObject("body");
                    PHBody body = new PHBody();
                    body.setBri(bodyObject.optInt("bri"));
                    body.setOn(bodyObject.optBoolean("on"));
                    body.setTransitiontime(bodyObject.optInt("transitiontime"));

                    command.setBody(body);
                    scheduleObject.setCommand(command);                           
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
                idObject.put("id", nextScheduleNumber);

                successObject.putOpt("success", idObject);

                bridgeConfiguration.getSchedules().put(nextScheduleNumber, scheduleObject);

                Date scheduleDate = Utils.stringToDate(scheduleObject.getTime());

                // Create The Schedule.
                PHScheduleTimer scheduleTimer = new PHScheduleTimer();
                scheduleTimer.setScheduleIdentifier(nextScheduleNumber);
                scheduleTimer.schedule(new ScheduleTask(nextScheduleNumber, commandObject, controller.getIpAddress(), bridgeConfiguration) , scheduleDate);

                scheduleTimer.setCommandJSON(commandObject);

                PHScheduleTimerManager timerManager = PHScheduleTimerManager.getInstance();
                timerManager.storeSchedule(nextScheduleNumber, scheduleTimer);
            }

        }  // JObject !=null

        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();


        controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    } 

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  3.3  GET SCHEDULE ATTRIBUTES
    //  http://developers.meethue.com/3_schedulesapi.html   3.3. Get schedule attributes
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void getScheduleAttributes_3_3(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String scheduleIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getSchedules() == null || bridgeConfiguration.getSchedules().get(scheduleIdentifier) == null) {
            sendErrorResponse(scheduleIdentifier, "3", responseBody);
        }
        else {
            mapper.writeValue(responseBody, bridgeConfiguration.getSchedules().get(scheduleIdentifier));   // Write to the response.
            controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration.getSchedules().get(scheduleIdentifier)), Color.WHITE, controller.showResponseJson()); 
        }

    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  3.4  SET SCHEDULE  ATTRIBUTES
    //  http://developers.meethue.com/3_schedulesapi.html   3.4. Set schedule attributes
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*= 
    public void setScheduleAttributes_3_4(ObjectMapper mapper, String jSONString, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String scheduleIdentifier) throws JsonParseException, IOException {          

        if (bridgeConfiguration.getSchedules() == null || bridgeConfiguration.getSchedules().get(scheduleIdentifier) == null) {
            sendErrorResponse(scheduleIdentifier, "3", responseBody);
        }

        PHSchedulesEntry scheduleObject = bridgeConfiguration.getSchedules().get(scheduleIdentifier);
        String responseBase = "/schedules/" + scheduleIdentifier + "/";
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
                        scheduleObject.setName(lightName);                             
                    }

                }
                else if (name.equals("description")) {
                    String lightDescription = jObject.optString(name);

                    successLine.putOpt(resourceUrl, jObject.optString(name));
                    scheduleObject.setDescription(lightDescription);                             
                }
                else if (name.equals("time")) {
                    String time = jObject.optString(name);

                    successLine.putOpt(resourceUrl, jObject.optString(time));
                    scheduleObject.setTime(time);                             
                }
                // TODO Command.  Do the below.
                else if (name.equals("command")) {
                    JSONObject commandObject = jObject.optJSONObject("command");

                    if (commandObject != null) {
                        JSONArray commandNames = commandObject.names();

                        PHCommand command = bridgeConfiguration.getSchedules().get(scheduleIdentifier).getCommand();

                        for (int c=0; c< commandNames.length(); c++) {
                            String cName = commandNames.getString(c);

                            if (cName.equals("address")) {
                                command.setAddress(commandObject.optString("address"));
                            }
                            else if (cName.equals("body")) {
                                JSONObject bodyObject = commandObject.optJSONObject("body");
                                PHBody body =command.getBody();
                                JSONArray bodyNames = bodyObject.names();
                                // TODO  Validate these values (e.g.  If bri is out of range or not).
                                for (int b=0; b < bodyNames.length(); b++) {
                                    String bName = bodyNames.getString(b);
                                    if (bName.equals("bri")) {
                                        body.setBri(bodyObject.optInt("bri"));
                                    }
                                    else if (bName.equals("on")) {
                                        body.setOn(bodyObject.optBoolean("on"));
                                    }
                                    else if (bName.equals("transitiontime")) {
                                        body.setTransitiontime(bodyObject.optInt("transitiontime"));
                                    }

                                }  // End of body Names loop
                                command.setBody(body);
                            }

                        }  // End of Command Names
                        bridgeConfiguration.getSchedules().get(scheduleIdentifier).setCommand(command);
                    } // Command Object != null

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
        bridgeConfiguration.getSchedules().put(scheduleIdentifier, scheduleObject);  
        controller.addTextToConsole(responseArray.toString(), Color.WHITE, controller.showResponseJson()); 
    }

    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    //  3.5  DELETE SCHEDULE
    //  http://developers.meethue.com/3_schedulesapi.html   3.5. Delete schedule
    // *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
    public void deleteSchedule_3_5(ObjectMapper mapper, PHBridgeConfiguration bridgeConfiguration, OutputStream responseBody, Controller controller, String scheduleIdentifier) throws JsonParseException, IOException {

        if (bridgeConfiguration.getSchedules() == null || bridgeConfiguration.getSchedules().get(scheduleIdentifier) == null) {
            sendErrorResponse(scheduleIdentifier, "3", responseBody);
        }
        else {

            bridgeConfiguration.getSchedules().remove(scheduleIdentifier);

            String resourceURL = "/schedules/" + scheduleIdentifier + " deleted";

            JSONObject responseObject = new JSONObject();
            responseObject.putOpt("success", resourceURL);

            responseBody.write(responseObject.toString().getBytes());
            responseBody.close();

            // mapper.writeValue(responseBody, bridgeConfiguration.getSchedules().get(scheduleIdentifier));   // Write to the response.
            controller.addTextToConsole(mapper.writeValueAsString(bridgeConfiguration.getSchedules().get(scheduleIdentifier)), Color.WHITE, controller.showResponseJson()); 
        }

    }


    public void sendErrorResponse(String lightIdentifier, String type, OutputStream responseBody) throws IOException {
        JSONArray responseArray = new JSONArray();
        JSONObject errorObject = new JSONObject();
        JSONObject errorLine = new JSONObject();
        errorLine.putOpt("type", type);
        errorLine.putOpt("address", "/schedules/" + lightIdentifier);
        errorLine.putOpt("description", "resource, /schedules/" + lightIdentifier + ", not available");
        errorObject.putOpt("error", errorLine);
        responseArray.put(errorObject);
        responseBody.write(responseArray.toString().getBytes());
        responseBody.close();
    }

    /*
     * ScheduleTask is called back when a Schedule time has occurred.
     * This sends the HTTP call back to the Emulator Server and executes it!!
     */
    class ScheduleTask extends TimerTask {
        private String scheduleIdentifier;
        private JSONObject commandObject;   // The JSON to execute when the Schedule occurs.
        private String ipAddress;
        private PHBridgeConfiguration bridgeConfiguration;

        public ScheduleTask(String scheduleIdentifier, JSONObject commandObject, String ipAddress, PHBridgeConfiguration bridgeConfiguration) {
            this.scheduleIdentifier = scheduleIdentifier;
            this.commandObject = commandObject;
            this.ipAddress = ipAddress;
            this.bridgeConfiguration = bridgeConfiguration;
        }

        public void run() {

            System.out.format("Time's up!  Identifier: " + scheduleIdentifier + "  Command JSON: " + commandObject.toString());

            JSONObject jsonBody = commandObject.optJSONObject("body");
            String address      = commandObject.optString("address");
            String method       = commandObject.optString("method");

            if (ipAddress == null) {   // Nasty fudge for the Integration testing.  When a schedule's time is up, it makes a http call with the command/action. This fudge will ensure it sends the command back to the test emulator.
                ipAddress = "localhost:8888";
            }

            try {
                Utils.doHttpCall(ipAddress, method, address, jsonBody);
            } catch (IOException e) {
                System.out.println("Unable to make HTTP Call for schedule. " + e.getMessage());
            }


            bridgeConfiguration.getSchedules().remove(scheduleIdentifier);            
            this.cancel(); //Terminate the timer thread
        }
    }
}
