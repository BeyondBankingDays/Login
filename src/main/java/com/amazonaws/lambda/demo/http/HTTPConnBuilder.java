package com.amazonaws.lambda.demo.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPConnBuilder {

	public String build(String username, String password, String consumerKey) throws IOException {
		 URL obj = null;
		 String url = "https://beyondbanking.openbankproject.com/my/logins/direct";
		 
		 HttpURLConnection con;
		 
		 StringBuffer response = new StringBuffer();
		
		
		obj = new URL(url);
	      con = (HttpURLConnection) obj.openConnection();
	      con.setRequestMethod("POST");
	      con.setRequestProperty("Content-Type", "application/json");
	      
	      con.setRequestProperty("Authorization", "DirectLogin username=\"" + username + "\",password=\"" + password
	              + "\",consumer_key=\"" + consumerKey + "\"");
	          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

	          String inputLine;
	          while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	          }
	          
	          in.close();
	      
		// TODO Auto-generated method stub
		return response.toString();
	}
	
	

}
