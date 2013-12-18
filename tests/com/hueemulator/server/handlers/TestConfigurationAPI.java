package com.hueemulator.server.handlers;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;

import com.hueemulator.emulator.HttpTester;
import com.hueemulator.emulator.TestEmulator;

public class TestConfigurationAPI extends TestCase {
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
    
    public void testBadJSON() throws Exception {
        String jsonToPut = "{badJson here . . \"lights\": [\"1\",\"2\"],\"name\": \"Test Group\"}";
        String response="";
        String expected="[{\"error\":{\"address\":\"/api/newdeveloper/groups\",\"description\":\"body contains invalid json\",\"type\":\"2\"}}]";
        String url = baseURL + "newdeveloper/groups";
        response = httpTester.doPutOrPost(url, jsonToPut, "POST");
        
        assertEquals(response, expected);
    }
}
