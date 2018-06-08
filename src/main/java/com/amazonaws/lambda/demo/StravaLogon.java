package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class StravaLogon {

  public String logon(JSONObject event) {

    String url = "https://www.strava.com/api/v3/athlete";
    StringBuffer response = new StringBuffer();

    URL obj = null;

    HttpURLConnection con;
    try {
      obj = new URL(url);
      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");

      AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
      DynamoDB dynamoDB = new DynamoDB(client);

      Table table = dynamoDB.getTable("User_Offerings");
      Item item = table.getItem("userName", "Payitoff@01");

      System.out.println("stravaID" + item.get("stravaId").toString());

      con.setRequestProperty("Authorization", "Bearer " + item.get("stravaId").toString());

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      System.out.println("response" + response.toString());

    } catch (Exception e) {

    }
    return response.toString();

  }

}
