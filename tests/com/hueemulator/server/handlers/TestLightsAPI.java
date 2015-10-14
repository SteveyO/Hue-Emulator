package com.hueemulator.server.handlers;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hueemulator.emulator.HttpTester;
import com.hueemulator.emulator.TestEmulator;
import com.hueemulator.lighting.utils.TestUtils;


public class TestLightsAPI  extends TestCase {

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
    public void testLightsAPI_1_1() throws Exception {
        //  // 1.1 Get all Lights
        System.out.println("Testing Lights API: 1.1. Get all lights   (http://developers.meethue.com/1_lightsapi.html)" );
        String url = baseURL + "newdeveloper/lights";
        String response="";
        String expected="{\"3\":{\"uniqueid\":\"00:17:88:01:00:d4:12:08-0c\",\"name\":\"Hue Lamp 3\",\"state\":{\"bri\":254,\"effect\":\"none\",\"sat\":254,\"reachable\":true,\"alert\":\"none\",\"hue\":65136,\"colormode\":\"hs\",\"on\":true,\"ct\":201,\"xy\":[0.346,0.3568]},\"modelid\":\"LCT001\",\"swversion\":\"65003148\",\"pointsymbol\":{\"3\":\"none\",\"2\":\"none\",\"1\":\"none\",\"7\":\"none\",\"6\":\"none\",\"5\":\"none\",\"4\":\"none\",\"8\":\"none\"},\"type\":\"Extended color light\"},\"2\":{\"uniqueid\":\"00:17:88:01:00:d4:12:08-0b\",\"name\":\"Hue Lamp 2\",\"state\":{\"bri\":254,\"effect\":\"none\",\"sat\":144,\"reachable\":true,\"alert\":\"none\",\"hue\":23536,\"colormode\":\"hs\",\"on\":true,\"ct\":201,\"xy\":[0.346,0.3568]},\"modelid\":\"LCT001\",\"swversion\":\"65003148\",\"pointsymbol\":{\"3\":\"none\",\"2\":\"none\",\"1\":\"none\",\"7\":\"none\",\"6\":\"none\",\"5\":\"none\",\"4\":\"none\",\"8\":\"none\"},\"type\":\"Extended color light\"},\"1\":{\"uniqueid\":\"00:17:88:01:00:d4:12:08-0a\",\"name\":\"Hue Lamp 1\",\"state\":{\"bri\":254,\"effect\":\"none\",\"sat\":254,\"reachable\":true,\"alert\":\"none\",\"hue\":4444,\"colormode\":\"hs\",\"on\":true,\"ct\":0,\"xy\":[0,0]},\"modelid\":\"LCT001\",\"swversion\":\"65003148\",\"pointsymbol\":{\"3\":\"none\",\"2\":\"none\",\"1\":\"none\",\"7\":\"none\",\"6\":\"none\",\"5\":\"none\",\"4\":\"none\",\"8\":\"none\"},\"type\":\"Extended color light\"}}";

        response = httpTester.doGet(url);

        assertTrue(TestUtils.jsonsEqual(expected, response));  
    }

    @Test
    public void testLightsAPI_1_4() throws Exception {
 
        // 1.4 Get light attributes and state
        System.out.println("Testing Lights API: 1.4. Get light attributes and state   (http://developers.meethue.com/1_lightsapi.html)" );
        String url = baseURL + "newdeveloper/lights/1";
        String response="";
        String expected="{\"uniqueid\":\"00:17:88:01:00:d4:12:08-0a\",\"name\":\"Hue Lamp 1\",\"state\":{\"bri\":254,\"effect\":\"none\",\"sat\":254,\"reachable\":true,\"alert\":\"none\",\"hue\":4444,\"colormode\":\"hs\",\"on\":true,\"ct\":0,\"xy\":[0,0]},\"modelid\":\"LCT001\",\"swversion\":\"65003148\",\"pointsymbol\":{\"3\":\"none\",\"2\":\"none\",\"1\":\"none\",\"7\":\"none\",\"6\":\"none\",\"5\":\"none\",\"4\":\"none\",\"8\":\"none\"},\"type\":\"Extended color light\"}";

        response = httpTester.doGet(url);       
        assertTrue(TestUtils.jsonsEqual(expected, response)); 

        // Get Light 2
        url = baseURL + "newdeveloper/lights/2";
        response="";
        expected="{\"uniqueid\":\"00:17:88:01:00:d4:12:08-0b\",\"name\":\"Hue Lamp 2\",\"state\":{\"bri\":254,\"effect\":\"none\",\"sat\":144,\"reachable\":true,\"alert\":\"none\",\"hue\":23536,\"colormode\":\"hs\",\"on\":true,\"ct\":201,\"xy\":[0.346,0.3568]},\"modelid\":\"LCT001\",\"swversion\":\"65003148\",\"pointsymbol\":{\"3\":\"none\",\"2\":\"none\",\"1\":\"none\",\"7\":\"none\",\"6\":\"none\",\"5\":\"none\",\"4\":\"none\",\"8\":\"none\"},\"type\":\"Extended color light\"}";

        response = httpTester.doGet(url);
        System.out.println(response);
        assertTrue(TestUtils.jsonsEqual(expected, response));        
    }


    @Test
    public void testLightsAPI_1_5() throws Exception {
        String url = baseURL + "newdeveloper/lights/2";

        String jsonToPut =  "{\"name\":\"Bedroom Light\"}";
        String response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        String expected = "[{\"success\":{\"/lights/2/name\":\"Bedroom Light\"}}]";

        assertTrue(TestUtils.jsonsArrayEqual(expected, response));

        // Name should be a String between 0 and 32 characters..   
        // Test an invalid name longer than 32 chars.
        jsonToPut =  "{\"name\":\"A really long name, longer than 32 characters\"}";
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        expected = "[{\"error\":{\"address\":\"/lights/2/name\",\"description\":\"invalid value, A really long name, longer than 32 c..., for parameter, name\",\"type\":7}}]";

        assertTrue(TestUtils.jsonsArrayEqual(expected, response));

    }

    @Test
    public void testLightsAPI_1_6() throws Exception {
        // 1.6 Set Light State
        System.out.println("Testing Lights API: 1.6. Set light state   (http://developers.meethue.com/1_lightsapi.html)" );
        String url = baseURL + "newdeveloper/lights/2/state";

        String jsonToPut="{\"hue\": 50000 }";  

        String expected="[{\"success\":{\"/lights/2/state/hue\":50000}}]";  // {"success":{"/lights/1/state/hue":50000}}

        String response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        assertTrue(TestUtils.jsonsArrayEqual(expected, response));   

        jsonToPut =  "{\"hue\": 20000,\"on\": false,\"bri\": 220}";
        expected = "[{\"success\":{\"/lights/2/state/bri\":220}},{\"success\":{\"/lights/2/state/hue\":20000}},{\"success\":{\"/lights/2/state/on\":false}}]";
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");

        assertTrue(TestUtils.jsonsArrayEqual(expected, response));   

        // Try to Modify the Hue of a light turned off.
        jsonToPut = "{\"hue\": 4444}";

        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        expected = "[{\"error\":{\"address\":\"/lights/2/state/hue\",\"description\":\"parameter, hue, is not modifiable. Device is set to off.\",\"type\":201}}]";

        assertTrue(TestUtils.jsonsArrayEqual(expected, response));   
        
        // Turn the Light Back on.
        jsonToPut = "{\"on\": true}";
        expected = "[{\"success\":{\"/lights/2/state/on\":true}}]";
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");

        assertTrue(TestUtils.jsonsArrayEqual(expected, response));   
        
        // Test setting Hue to an Invalid Value.
        jsonToPut = "{\"hue\": 66666}";
        expected = "[{\"error\":{\"address\":\"/lights/2/state/hue\",\"description\":\"invalid value, 66666 , for parameter, hue\",\"type\":7}}]";
        response= httpTester.doPutOrPost(url, jsonToPut, "PUT");
        assertTrue(TestUtils.jsonsArrayEqual(expected, response));   
    }

}
