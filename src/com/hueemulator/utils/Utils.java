package com.hueemulator.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 
}
