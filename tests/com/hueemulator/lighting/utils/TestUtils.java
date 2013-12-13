package com.hueemulator.lighting.utils;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Collection of utilities to assist in testing.
 */
public class TestUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  /**
   * Loads the contents of the test fixture specified at the given path.
   *
   * @param path specifies the file to load the contents of
   * @return String is the file contents
   * @throws IOException
   */
  public static String loadTestFixture(String path) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    return sb.toString();
  }

  /**
   * Tests two JSON strings for equality by performing a deep comparison.
   *
   * @param json1 represents a JSON object to compare with json2
   * @param json2 represents a JSON object to compare with json1
   * @return true if the JSON objects are equal, false otherwise
   */
  public static boolean jsonsEqual(String json1, String json2) throws Exception {
    Object obj1Converted = convertJsonElement(new JSONObject(json1));
    Object obj2Converted = convertJsonElement(new JSONObject(json2));
    return obj1Converted.equals(obj2Converted);
  }

  /**
   * Tests two JSON strings for equality by performing a deep comparison.
   *
   * @param json1 represents a JSON object to compare with json2
   * @param json2 represents a JSON object to compare with json1
   * @return true if the JSON objects are equal, false otherwise
   */
  public static boolean jsonsArrayEqual(String json1, String json2) throws Exception {
    Object obj1Converted = convertJsonElement(new JSONArray(json1));
    Object obj2Converted = convertJsonElement(new JSONArray(json2));
    return obj1Converted.equals(obj2Converted);
  }
  
  /**
   * Tests the DOMs represented by two XML strings for equality by performing
   * a deep comparison.
   *
   * @param xml1 represents the XML DOM to compare with xml2
   * @param xml2 represents the XML DOM to compare with xml1
   *
   * return true if the represented DOMs are equal, false otherwise
   */
  public static boolean xmlsEqual(String xml1, String xml2) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();

    Document doc1 = db.parse(new InputSource(new StringReader(xml1)));
    Document doc2 = db.parse(new InputSource(new StringReader(xml2)));

    Set<Object> childSet1 = getChildSet(doc1.getDocumentElement(), "");
    Set<Object> childSet2 = getChildSet(doc2.getDocumentElement(), "");

    return childSet1.equals(childSet2); // comparing sets does all the hard work :)
  }

  // ---------------------------- PRIVATE HELPERS -----------------------------

  /*
   * Recursive utility to convert a JSONObject to an Object composed of Sets,
   * Maps, and the target types (e.g. Integer, String, Double).  Used to do a
   * deep comparison of two JSON objects.
   *
   * @param Object is the JSON element to convert (JSONObject, JSONArray, or target type)
   *
   * @return an Object representing the appropriate JSON element
   */
  @SuppressWarnings("unchecked")
  private static Object convertJsonElement(Object elem) throws JSONException {
    if (elem instanceof JSONObject) {
      JSONObject obj = (JSONObject) elem;
      Iterator<String> keys = obj.keys();
      Map<String, Object> jsonMap = new HashMap<String, Object>();
      while (keys.hasNext()) {
        String key = keys.next();
        jsonMap.put(key, convertJsonElement(obj.get(key)));
      }
      return jsonMap;
    } else if (elem instanceof JSONArray) {
      JSONArray arr = (JSONArray) elem;
      Set<Object> jsonSet = new HashSet<Object>();
      for (int i = 0; i < arr.length(); i++) {
        jsonSet.add(convertJsonElement(arr.get(i)));
      }
      return jsonSet;
    } else {
      return elem;
    }
  }

  /*
   * Recursive utility to represent an XML Document as a Set.
   *
   * @param node is the root node to map to a Set
   * @param basePath is the path to the root node
   *
   * @return Set<Object> represents the XML Document as a Set
   */
  private static Set<Object> getChildSet(Node node, String basePath) {
    Set<Object> childSet = new HashSet<Object>();
    if (!node.hasChildNodes() && !node.getTextContent().trim().equals("")) {
      childSet.add(basePath + ":" + node.getTextContent());
    } else {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        childSet.add(getChildSet(children.item(i), basePath + "/" + node.getNodeName()));
      }
    }
    return childSet;
  }
  
  
  // remove duplicates
  public static JSONArray removeDuplicates(JSONArray originalArray, boolean removeErrors) {
      
      List currentObjs = new ArrayList();
      JSONArray newArray = new JSONArray();
      boolean includeLine=true;
      
      for (int i=0; i< originalArray.length(); i++) {
          Object obj = originalArray.get(i);
          String jsonString =  originalArray.getJSONObject(i).toString();
          
          includeLine=true;
          
          if (removeErrors && jsonString.startsWith("{\"error\":{")) {
              includeLine=false;
          }
          
          if (!currentObjs.contains(obj)) {
              if (includeLine) {
                  newArray.put(obj);
              }
          }
          
          currentObjs.add(obj);
      }
      
      return newArray;
  }
  
  public static String getDateSecondsInFuture(int noSeconds) {
      Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
      calendar.add(Calendar.SECOND, noSeconds);
      
      return dateFormat.format(calendar.getTime());
  }
  
}
