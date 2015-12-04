package com.hueemulator.server.handlers;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.hueemulator.emulator.HttpTester;
import com.hueemulator.emulator.TestEmulator;
import com.hueemulator.lighting.utils.TestUtils;
import com.hueemulator.model.PHBridgeConfiguration;


public class TestSchedulesAPI extends TestCase {

    TestEmulator testEmulator;
    HttpTester httpTester;

    String fileName = "/config-2bulbs.json";
    String baseURL = "http://localhost:" + TestEmulator.PORT_NUMBER + "/api/";

    @Before           
    public void setUp() throws IOException {
        testEmulator = TestEmulator.getInstance();
        // Only start the Emulator/Server once for all tests.
        if (!testEmulator.isServerRunning()) {
           testEmulator.startEmulator();
        }

        httpTester = new HttpTester();
        
        // Tests should be stateless.  Reload initial config before running each test.
        testEmulator.reloadInitialConfig();
    }
    
    @Test
    public void testSchedulesAPI_3_1() throws Exception{
        // 3.1 Get all Schedules
        System.out.println("Testing Schedules API: 3.1. Get all Schedules  (http://developers.meethue.com/3_schedulesapi.html)" );
        String url = baseURL + "newdeveloper/schedules";
        String response="";
        String expected="{\"1\":{\"time\":\"2012-10-29T12:00:00\",\"description\":\"\",\"name\":\"schedule\",\"command\":{\"body\":{\"on\":true},\"address\":\"/api/newdeveloper/groups/0/action\",\"method\":\"PUT\"}}}";

        response = httpTester.doGet(url);
        System.out.println(response);
        assertTrue(TestUtils.jsonsEqual(response, expected));

    }    

    @Test
    public void testSchedulesAPI_3_2() throws Exception{
        // 3.2 Create Schedule
        System.out.println("Testing Schedules API: 2.2. Create Schedule  (http://developers.meethue.com/3_schedulesapi.html)" );
        String url = baseURL + "newdeveloper/schedules";
        String response="";

        
        PHBridgeConfiguration bridgeConfiguration = testEmulator.getModel().getBridgeConfiguration();        
        int noSchedules = bridgeConfiguration.getSchedules().size();
        assertEquals(noSchedules, 1);  // Already have 1 schedule loaded in default config.

        
        // Test 1 - Create a schedule with a date in the past.
        String scheduleTime = "2011-03-30T14:24:40";
        String jsonToPut = "{\"name\": \"Wake up\",\"description\": \"My wake up alarm\",\"command\": {\"address\": \"/api/<username>/groups/0/action\",\"method\": \"PUT\",\"body\": {\"on\": true}},\"time\": \"" + scheduleTime + "\"}";

        
        String expected="[{\"error\":{\"address\":\"/schedules/time\",\"description\":\"invalid value, 2011-03-30T14:24:40, for parameter, time\",\"type\":7}}]";
        response = httpTester.doPutOrPost(url, jsonToPut, "POST");
        assertTrue(TestUtils.jsonsArrayEqual(response, expected));
        
        noSchedules = bridgeConfiguration.getSchedules().size();
        assertEquals(noSchedules, 1);   // Schedule not created so should remain as 1
        
        
        // Test 2 - Create a schedule with a date 2 seconds in the future to turn all Lights off (using the default Group 0 command).
        scheduleTime = TestUtils.getDateSecondsInFuture(2);  // Returns a formatted date string, 5 seconds in the future.
        jsonToPut = "{\"name\": \"Wake up\",\"description\": \"My wake up alarm\",\"command\": {\"address\": \"/groups/0/action\",\"method\": \"PUT\",\"body\": {\"on\": false}},\"time\": \"" + scheduleTime + "\"}";
        
        expected = "[{\"success\":{\"id\":\"2\"}}]";
        response = httpTester.doPutOrPost(url, jsonToPut, "POST");
        assertTrue(TestUtils.jsonsArrayEqual(response, expected));
        
        noSchedules = bridgeConfiguration.getSchedules().size();
        assertEquals(noSchedules, 2);  // Now have 2 schedules
        
        Thread.sleep(2500);  // Sleep 2.5 seconds.  Which gives us time for schedule to execute.
        
        noSchedules = bridgeConfiguration.getSchedules().size();
        assertEquals(noSchedules, 1);  // Schedule is deleted after completion.       

    }  


    @Test
    public void testSchedulesAPI_3_3() throws Exception {
        // 3.3 Get schedule attributes
        System.out.println("Testing Schedules API: 3.3. Get schedules attributes   (http://developers.meethue.com/3_schedulesapi.html)" );
        String url = baseURL + "newdeveloper/schedules/1";
        String response="";
        String expected="{\"time\":\"2012-10-29T12:00:00\",\"description\":\"\",\"name\":\"schedule\",\"command\":{\"body\":{\"scene\":null,\"on\":true,\"xy\":null,\"bri\":null,\"transitiontime\":null},\"address\":\"/api/newdeveloper/groups/0/action\",\"method\":\"PUT\"}}";

        response = httpTester.doGet(url);
        assertTrue(TestUtils.jsonsEqual(response, expected));       
    }

    @Test
    public void testSchedulesAPI_3_4() throws Exception {
        // 2.4  Set group attributes
        System.out.println("Testing Schedules API: 3.4. Set schedule attributes   (http://developers.meethue.com/3_schedulesapi.html)" );
        String url = baseURL + "newdeveloper/schedules/1";
        String response="";
        String jsonToPut = "{\"name\": \"Wake up\"}";
        

        String expected  = "[{\"success\": {\"/schedules/1/name\": \"Wake up\"}}]";
        
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        assertTrue(TestUtils.jsonsArrayEqual(response, expected));   
        
        // Check Schedule Object
        PHBridgeConfiguration bridgeConfiguration = testEmulator.getModel().getBridgeConfiguration();  
        String newScheduleName = bridgeConfiguration.getSchedules().get("1").getName();
        assertEquals(newScheduleName, "Wake up");
    }
       
    @Test
    public void testSchedulesAPI_3_5() throws Exception {
        // 3.5  Delete Schedule
        System.out.println("Testing Schedules API: 3.5. Delete schedule   (http://developers.meethue.com/3_schedulesapi.html)" );
        String url = baseURL + "newdeveloper/schedules/1";
        
        PHBridgeConfiguration bridgeConfiguration = testEmulator.getModel().getBridgeConfiguration();        
        int noSchedules = bridgeConfiguration.getSchedules().size();
        
        assertEquals(noSchedules, 1);
        String response="";

        response= httpTester.doDelete(url);
        
        assertEquals(response, "{\"success\":\"/schedules/1 deleted\"}");

        noSchedules = bridgeConfiguration.getSchedules().size();
        assertEquals(noSchedules, 0);   
    }

}

