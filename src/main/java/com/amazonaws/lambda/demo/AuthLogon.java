package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;

public class AuthLogon {

  public String logon(JSONObject event) {
    String url = "https://beyondbanking.openbankproject.com/obp/v3.0.0/users/current";
    StringBuffer response = new StringBuffer();

    URL obj = null;
    String authorization = null;

    HttpURLConnection con;
    try {
      obj = new URL(url);
      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");

      if (event.get("headers") != null) {
        JSONObject pps = (JSONObject) event.get("headers");
        if (pps.get("Authorization") != null) {
          authorization = (String) pps.get("Authorization");
        }
      }

      System.out.println("authID" + authorization);

      con.setRequestProperty("Authorization", "DirectLogin token=" + authorization);

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      System.out.println("Reached here as well");
      System.out.println("response" + response.toString());
      in.close();


    } catch (Exception e) {

    }
    return response.toString();

  }

}
