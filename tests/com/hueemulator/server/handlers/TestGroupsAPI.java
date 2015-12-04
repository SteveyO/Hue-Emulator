package com.hueemulator.server.handlers;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.hueemulator.emulator.HttpTester;
import com.hueemulator.emulator.TestEmulator;
import com.hueemulator.lighting.utils.TestUtils;
import com.hueemulator.model.PHBridgeConfiguration;


public class TestGroupsAPI extends TestCase {

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
    public void testGroupsAPI_2_1() throws Exception{
        // 2.1 Get all Groups
        System.out.println("Testing Groups API: 2.1. Get all groups  (http://developers.meethue.com/2_groupsapi.html)" );
        String url = baseURL + "newdeveloper/groups";
        String response="";
        String expected="{\"1\":{\"lights\":[\"1\",\"2\"],\"name\":\"Group 1\",\"action\":{\"bri\":254,\"effect\":\"none\",\"sat\":144,\"hue\":33536,\"on\":true,\"colormode\":\"xy\",\"ct\":201,\"xy\":[0.346,0.3568]}}}";

        response = httpTester.doGet(url);
        assertTrue(TestUtils.jsonsEqual(response, expected));

    }    

    @Test
    public void testGroupsAPI_2_2() throws Exception{
        // 2.2 Create Group
        System.out.println("Testing Groups API: 2.2. Create group  (http://developers.meethue.com/2_groupsapi.html)" );
        String url = baseURL + "newdeveloper/groups";
        String response="";

        String jsonToPut = "{\"lights\": [\"1\",\"2\"],\"name\": \"Test Group\"}";
        String expected="[{\"success\":{\"id\":\"/groups/2\"}}]";
        response = httpTester.doPutOrPost(url, jsonToPut, "POST");
        assertTrue(TestUtils.jsonsArrayEqual(response, expected));

    }  


    @Test
    public void testGroupsAPI_2_3() throws Exception {
        // 2.3 Get group attributes
        System.out.println("Testing Groups API: 2.3. Get group attributes   (http://developers.meethue.com/2_groupsapi.html)" );
        String url = baseURL + "newdeveloper/groups/1";
        String response="";
        String expected="{\"name\":\"Group 1\",\"action\":{\"bri\":254,\"effect\":\"none\",\"sat\":144,\"reachable\":null,\"alert\":null,\"hue\":33536,\"colormode\":\"xy\",\"on\":true,\"ct\":201,\"xy\":[0.346,0.3568]},\"lights\":[\"1\",\"2\"]}";

        response = httpTester.doGet(url);
        assertTrue(TestUtils.jsonsEqual(response, expected));       
    }

    @Test
    public void testGroupsAPI_2_4() throws Exception {
        // 2.4  Set group attributes
        System.out.println("Testing Groups API: 2.4. Set group attributes   (http://developers.meethue.com/2_groupsapi.html)" );
        String url = baseURL + "newdeveloper/groups/1";
        String response="";
        String jsonToPut = "{\"name\":\"Bedroom\",\"lights\":[\"1\"]}";
        String expected  = "[{\"success\":{\"/groups/1/lights\":[\"1\"]}},{\"success\":{\"/groups/1/name\":\"Bedroom\"}}]";
        
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        assertTrue(TestUtils.jsonsArrayEqual(response, expected));       
    }
    
    @Test
    public void testGroupsAPI_2_5() throws Exception {
        // 2.5  Set group state
        System.out.println("Testing Groups API: 2.5. Set group state   (http://developers.meethue.com/2_groupsapi.html)" );
        String url = baseURL + "newdeveloper/groups/0/action";
        String response="";
        String jsonToPut = "{\"on\": true, \"hue\": 42000}";
        String expected  = "[{\"success\":{\"/groups/0/action/on\":true}},{\"success\":{\"/groups/0/action/hue\":42000}}]";
           
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        assertTrue(TestUtils.jsonsArrayEqual(response, expected));       
    }
    
    @Test
    public void testGroupsAPI_2_6() throws Exception {
        // 2.6  Delete Group
        System.out.println("Testing Groups API: 2.6. Delete group   (http://developers.meethue.com/2_groupsapi.html)" );
        String url = baseURL + "newdeveloper/groups/1";
        
        PHBridgeConfiguration bridgeConfiguration = testEmulator.getModel().getBridgeConfiguration();        
        int noGroups = bridgeConfiguration.getGroups().size();
        assertEquals(noGroups, 1);
        String response="";

        response= httpTester.doDelete(url);
        
        assertEquals(response, "[{\"success\":\"/groups/1 deleted\"}]");

        noGroups = bridgeConfiguration.getGroups().size();
        assertEquals(noGroups, 0);
        
 //       assertTrue(TestUtils.jsonsArrayEqual(response, expected));       
    }

    

}
