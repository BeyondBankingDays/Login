package com.amazonaws.lambda.demo.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.lambda.demo.logon.DirectLogon;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class LambdaFunctionHandler implements RequestStreamHandler {

  JSONParser parser = new JSONParser();

  DirectLogon directLogon = new DirectLogon();

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
      
      pathparam = getPathParam(event);

      String statusCode = "200";

      if (pathparam == null) {
        statusCode = "400";
        responseJson.put("exception", "pathparam empty");
      } else {
            responseJson.put("body", directLogon(event));      }

      responseJson.put("isBase64Encoded", false);
      responseJson.put("statusCode", statusCode);

    } catch (ParseException e) {
      responseJson.put("statusCode", "400");
      responseJson.put("exception", e);
    }

    logger.log(responseJson.toJSONString());
    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toJSONString());
    writer.close();

  }


  private String getPathParam(JSONObject event) {
	  String pathparam = null;
	  if (event.get("pathParameters") != null) {
	        JSONObject pps = (JSONObject) event.get("pathParameters");
	        if (pps.get("proxy") != null) {
	          pathparam = (String) pps.get("proxy");
	        }
	      }

	     return pathparam;


}


private String directLogon(JSONObject event) {

    return directLogon.logon(event);

  }
}
