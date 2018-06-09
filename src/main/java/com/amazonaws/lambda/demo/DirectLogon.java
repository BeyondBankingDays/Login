package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DirectLogon {

  JSONParser parser = new JSONParser();

  public String logon(JSONObject event) {

    String url = "https://beyondbanking.openbankproject.com/my/logins/direct";
    String username = null;
    String password = null;
    String consumerKey = null;
    URL obj = null;
    StringBuffer response = new StringBuffer();


    System.out.println("Reached inside ");

    HttpURLConnection con;
    try {
      obj = new URL(url);
      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");

      System.out.println(event.get("body"));

      if (event.get("body") != null) {
        JSONObject body;
        try {
          body = (JSONObject) parser.parse((String) event.get("body"));
          System.out.println("body" + body.toJSONString());

          if (body.get("username") != null) {
            username = (String) body.get("username");
          }
          if (body.get("password") != null) {
            password = (String) body.get("password");
          }
          System.out.println(username + password);

          AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
          DynamoDB dynamoDB = new DynamoDB(client);

          Table table = dynamoDB.getTable("User_Offerings");


          Item item = table.getItem("userName", username);

          consumerKey = item.get("consumer_key").toString();

          System.out.println("ConsumerKey" + consumerKey);


          con.setRequestProperty("Authorization", "DirectLogin username=\"" + username + "\",password=\"" + password
              + "\",consumer_key=\"" + consumerKey + "\"");
          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();
          System.out.println("response" + response.toString());

          JSONObject responseToken = new JSONObject();
          responseToken = (JSONObject) parser.parse(response.toString());

          if (responseToken.get("token") != null) {
            item.withString("userTokenId", responseToken.get("token").toString());
            item.withLong("joinedDate", new Date().getTime()-2678421);
          }
          
          table = dynamoDB.getTable("User_ID_Mapping");
          
          Item item1 = new Item();
          item1.withPrimaryKey("tokenKey",responseToken.get("token").toString()).withString("userName", username);

          table.putItem(item1);


        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } // optional default is
        catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }



      }



    } catch (IOException e) {

    }
    return response.toString();
  }

}
