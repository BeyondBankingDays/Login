package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class LambdaFunctionHandler implements RequestStreamHandler {

  JSONParser parser = new JSONParser();

  DirectLogon directLogon = new DirectLogon();
  AuthLogon authLogon = new AuthLogon();
  StravaLogon strataLogon = new StravaLogon();
  TestLogon testLogon = new TestLogon();

  @SuppressWarnings("unchecked")
  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

    LambdaLogger logger = context.getLogger();
    logger.log("Loading Java Lambda handler of ProxyWithStream");

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();

    String pathparam = null;

    try {
      JSONObject event = (JSONObject) parser.parse(reader);
      System.out.println("Print event" + event);

      /*
       * if (event.get("queryStringParameters") != null) { JSONObject qps = (JSONObject)
       * event.get("queryStringParameters"); if (qps.get("name") != null) { name = (String) qps.get("name"); } }
       * 
       * if (event.get("pathParameters") != null) { JSONObject pps = (JSONObject) event.get("pathParameters"); if
       * (pps.get("proxy") != null) { city = (String) pps.get("proxy"); } }
       * 
       * if (event.get("headers") != null) { JSONObject hps = (JSONObject) event.get("headers"); if (hps.get("day") !=
       * null) { day = (String) hps.get("day"); } }
       * 
       * if (event.get("body") != null) { JSONObject body = (JSONObject) parser.parse((String) event.get("body")); if
       * (body.get("time") != null) { time = (String) body.get("time"); } }
       */

      if (event.get("pathParameters") != null) {
        JSONObject pps = (JSONObject) event.get("pathParameters");
        if (pps.get("proxy") != null) {
          pathparam = (String) pps.get("proxy");
        }
      }

      System.out.println("Pathparam" + pathparam);

      String statusCode = "200";

      if (pathparam == null) {
        statusCode = "400";
        responseJson.put("exception", "pathparam empty");
      } else {
        System.out.println("Here");

        switch (pathparam) {
          case "direct":
            responseJson.put("body", directLogon(event));
            break;
          case "strava":
            responseJson.put("body", strataLogon(event));
            break;
          case "auth":
            responseJson.put("body", authLogon(event));
            break;
          case "test":
            responseJson.put("body", testLogon(event));
            break;
          default:
            statusCode = "400";
            responseJson.put("exception", "invalid path param");
            break;
        }

      }

      responseJson.put("isBase64Encoded", false);
      responseJson.put("statusCode", statusCode);

      /*
       * String greeting = "Good " + time + ", " + name + " of " + city + ". "; if (day != null && day != "") greeting
       * += "Happy " + day + "!";
       * 
       * JSONObject responseBody = new JSONObject();
       * 
       * // responseBody.put("input", event.toJSONString()); responseBody.put("message", greeting);
       * 
       * JSONObject headerJson = new JSONObject();
       * 
       * responseJson.put("statusCode", responseCode); responseJson.put("headers", headerJson); responseJson.put("body",
       * responseBody.toString());
       */

    } catch (ParseException e) {
      responseJson.put("statusCode", "400");
      responseJson.put("exception", e);
    }

    logger.log(responseJson.toJSONString());
    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toJSONString());
    writer.close();

  }


  private Object testLogon(JSONObject event) {

    return testLogon.logon(event);

  }


  private String authLogon(JSONObject event) {

    return authLogon.logon(event);

  }


  private String strataLogon(JSONObject event) {

    return strataLogon.logon(event);

  }


  private String directLogon(JSONObject event) {

    return directLogon.logon(event);

  }


  /*
   * private void callHttpEndPoint() throws IOException { // TODO Auto-generated method stub
   * 
   * String url =
   * "http://api.ipinfodb.com/v3/ip-city/?key=d64fcfdfacc213c7ddf4ef911dfe97b55e4696be3532bf8302876c09ebd06b&ip=74.125.45.100&format=json";
   * URL obj = new URL(url); HttpURLConnection con = (HttpURLConnection) obj.openConnection(); // optional default is
   * GET con.setRequestMethod("GET"); // add request header con.setRequestProperty("User-Agent", "Mozilla/5.0");
   * BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
   * System.out.println("Reached here");
   * 
   * String inputLine; StringBuffer response = new StringBuffer(); while ((inputLine = in.readLine()) != null) {
   * response.append(inputLine); } in.close(); // print in String System.out.println(response.toString());
   * 
   * }
   * 
   * 
   * private String callAmazonDB() {
   * 
   * AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build(); DynamoDB dynamoDB = new DynamoDB(client);
   * 
   * Table table = dynamoDB.getTable("Offerings");
   * 
   * 
   * Item item1 = table.getItem("offerId", "Dec_1"); return item1.toJSONPretty();
   * 
   * }
   */

}
