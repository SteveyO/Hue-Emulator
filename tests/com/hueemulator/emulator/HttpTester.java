package com.hueemulator.emulator;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpTester {
 
 // HTTP GET request
 public String doGet(String url) {
   
  URL obj;
  HttpURLConnection con=null;
  
  try {
   obj = new URL(url);
   con = (HttpURLConnection) obj.openConnection();
   con.setRequestMethod("GET");
   con.setRequestProperty("Cache-Control", "no-cache");
  } catch (MalformedURLException e) {
   e.printStackTrace();
  } 
   catch (IOException e) {
   e.printStackTrace();
  }
 
  //add request header

  String inputLine;
  StringBuffer response = new StringBuffer();
  BufferedReader in;
  
  try {
   in = new BufferedReader(new InputStreamReader(con.getInputStream()));
   while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
   }
   in.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
 
  return response.toString();
 
 } 
 
 // HTTP POST request
 public String doPutOrPost(String url, String jsonString, String requestMethod) throws Exception {
   
  URL obj;
  HttpURLConnection con=null;
  
  try {
   obj = new URL(url);
   con = (HttpURLConnection) obj.openConnection();
   con.setRequestMethod(requestMethod);
  } catch (MalformedURLException e) {
   e.printStackTrace();
  } 
   catch (IOException e) {
   e.printStackTrace();
  }
 
  //add request header
  con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
  con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
  
 
  // Send post request
  con.setDoOutput(true);
  DataOutputStream wr = new DataOutputStream(con.getOutputStream());
  wr.writeBytes(jsonString);
  wr.flush();
  wr.close();
 
  int responseCode = con.getResponseCode();
 
  BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream()));
  String inputLine;
  StringBuffer response = new StringBuffer();
 
  while ((inputLine = in.readLine()) != null) {
   response.append(inputLine);
  }
  in.close();
 
  return response.toString();
 
 } 

   // HTTP DELETE request
    public String doDelete(String url) {
        
        URL obj;
        HttpURLConnection con=null;
        
        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Cache-Control", "no-cache");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }   
         catch (IOException e) {
            e.printStackTrace();
        }
 
        //add request header

        String inputLine;
        StringBuffer response = new StringBuffer();
        BufferedReader in;
        
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return response.toString();
 
    }
 
}
