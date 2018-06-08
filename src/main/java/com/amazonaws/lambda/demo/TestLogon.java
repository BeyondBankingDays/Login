package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TestLogon {

  JSONParser parser = new JSONParser();
  private static DecimalFormat df2 = new DecimalFormat(".##");

  public Object logon(JSONObject event) {

    double runValue = 0;
    double rideValue = 0;
    double swimValue = 0;

    JSONObject jsonObject = new JSONObject();

    String url = "https://www.strava.com/api/v3/athlete/activities?before=1525705726";
    StringBuffer response = new StringBuffer();

    URL obj = null;
    String authorization =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyIiOiIifQ.DZ4RcoBjLjDLINbhwnUDVCe0PHzCaqRcXn-alZRLpdA";

    HttpURLConnection con;
    try {
      obj = new URL(url);
      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");

      /*
       * if (event.get("headers") != null) { JSONObject pps = (JSONObject) event.get("headers"); if
       * (pps.get("Authorization") != null) { authorization = (String) pps.get("Authorization"); } }
       */

      System.out.println("authID" + authorization);

      con.setRequestProperty("Authorization", "Bearer ba8287c7e90e222df21494cdccfddee4630fbd33");

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      System.out.println("Reached here as well111111");
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      System.out.println("Reached here as well");
      System.out.println("response" + response.toString());
      in.close();


      JSONArray msg = (JSONArray) parser.parse(response.toString());

      System.out.println("Size of" + msg.size());

      Iterator<JSONObject> iterator = msg.iterator();

      while (iterator.hasNext()) {
        JSONObject temp = iterator.next();
        if (temp.get("type").toString().equalsIgnoreCase("run")) {
          runValue += Double.parseDouble(temp.get("distance").toString());
        } else if (temp.get("type").toString().equalsIgnoreCase("ride")) {
          rideValue += Double.parseDouble(temp.get("distance").toString());
        } else if (temp.get("type").toString().equalsIgnoreCase("swim")) {
          swimValue += Double.parseDouble(temp.get("distance").toString());
        }

      }

      jsonObject.put("run_distance", df2.format(runValue));
      jsonObject.put("ride_distance", df2.format(rideValue));
      jsonObject.put("swim_distance", df2.format(swimValue));
      jsonObject.put("measure", "metres");

    } catch (Exception e) {

    }

    return jsonObject.toJSONString();

  }

}
