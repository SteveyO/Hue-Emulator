package com.hueemulator.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final String USER_AGENT = "Mozilla/5.0";
    
 public static boolean isInRange(int value, int start, int end) {
     if (value >= start && value <=end) {
         return true;
     }
     return false;
 }

 // It looks like names over 36 characers are chopped and . . . appended afterwards.
 public static String chopName(String name) {
  if (name==null || name.length() <= 36) {
   return name;
  }
  else {
   return name.substring(0,36) + "...";
  }
 }
 
 /**
  * 
  * This method is used by the Schedules API.  Its purpose is twofold.  Firstly to check the date is in the correct format,
  * and secondly to check the date is after the current date.
  * 
  * @param date
  * @return
  */
 public static boolean isDateValid(String dateString) {                                                     
    
     
     Date parsedDate;
     try {
             parsedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
             return false;
        }

     
     Date todaysDate = new Date();
     
     if (parsedDate.before(todaysDate)) {
         return false;
     }
     
     return true;
 }
 
 public static Date stringToDate(String dateString) {
        Date parsedDate;
         try {
              parsedDate = dateFormat.parse(dateString);
         } catch (ParseException e) {
              return new Date();
         }
         
         return parsedDate;
 }
 
 /**
  * This method is used for Schedules.  When the schedule time occurs, a http call is sent to the bridge passing the JSON Command.
  * 
  * @param httpMethod
  * @param url
  * @param httpBody
  * @throws IOException
  */
 public static void doHttpCall(String ipAddress, String httpMethod, String url, JSONObject httpBody) throws IOException {

     String fullUrl = "http://" + ipAddress + "/api/newdeveloper" + url;
     System.out.println("\nSubmitting URL: " + fullUrl);
        URL obj = new URL(fullUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
        //add reuqest header
        con.setRequestMethod(httpMethod.toUpperCase());
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        
        con.setRequestProperty("Content-Type", "application/json");
     
        if (httpMethod.equalsIgnoreCase("PUT") || httpMethod.equalsIgnoreCase("POST")) {
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(httpBody.toString());
            wr.flush();
            wr.close();
        }
 
        con.getResponseCode();
 }
 
 /** 
  * Returns the current date in the Date Format used by the Bridge.
  * @return
  */
 public static String getCurrentDate() {
	 return dateFormat.format(new Date());
 }
 
 /**
  * Method checks if valid json (as name suggests).  This is called before every http call to check the user has passed valid json. 
  * @param test
  * @return
  */
 public static boolean isJSONValid(String test)
 {
     boolean valid = false;
     try {
         new JSONObject(test);
         valid = true;
     }
     catch(JSONException ex) { 
         valid = false;
     }
     return valid;
 }
 
 public static String loadDescriptionFile(String fileName) throws IOException {
     InputStream is = Utils.class.getResourceAsStream(fileName);
     if (is==null) {
         System.out.println("Is is null: " + fileName);
     }
     BufferedReader br =new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      
      try {
          String line = br.readLine();

          while (line != null) {
              sb.append(line);
              sb.append('\n');
              line = br.readLine();
          }
         
      } finally {
          br.close();
      }
      
      return sb.toString();
 }
 
 // As from December 2015/January 2016 you will no longer be able to create custom whitelist entries in a Hue Bridge, and the randomly generated one must be used instead.
 // THis change will attempt to replicate the Bridge Logic.
 public static String generateRandomUsername() {
     String validChars = "1234567890abcef";
     String username="";
     Random rand = new Random();
     
     for (int i=0; i < 31; i++) {
         username += validChars.charAt(rand.nextInt(validChars.length()));
     }
          
     return username;
 }
 
 // Called when adding a new Light
 public static String generateRandomUniqueId()
 {
     String[] validChars = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
     Random rd = new Random();
     rd.nextInt(15);
     String result="";

     for(int i=0;i<6;i++)
     {
         String a = validChars[rd.nextInt(15)];
         String b = validChars[rd.nextInt(15)];
         result+=a+b;
         if(i<5)
         {
             result+=":";
         }
         

     }
         result += ":"+validChars[rd.nextInt(10)]+validChars[rd.nextInt(10)]+":"+validChars[rd.nextInt(10)]+validChars[rd.nextInt(10)]+"-"+validChars[rd.nextInt(15)]+validChars[rd.nextInt(15)];
     return result;
 }
 
}
