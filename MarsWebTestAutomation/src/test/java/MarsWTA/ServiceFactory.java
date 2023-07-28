package MarsWTA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;

import com.jayway.jsonpath.JsonPath;

public class ServiceFactory {
	public String GetWebField;
	public String GetWebFieldVal;
	public String GetCommand;
	public String GetFindBy;
	public boolean CommandStat;
	public String ActualResult;
	WebDriver driver = null;
	
	public String StoreValServiceResCode;
	public static String StoreValServiceResJSON;
	public static String StoreResponseCode;
	
	public String StoreValServiceTokenResCode;
	public static String StoreValServiceTokenJSON;
		
	public String PullValJSON(String OverrideGetWebField)
	{
		String Str = "";
		try {
			Str = JsonPath.read(StoreValServiceResJSON, OverrideGetWebField).toString();
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return Str;
	}
	
	public String PullResponseCode() {
		return StoreResponseCode;	
	}
	public void SetVal(String lCommand,String lWebFieldVal, String lFindBy, String lWebField)
	{
				this.GetCommand = lCommand;	
				this.GetWebFieldVal = lWebFieldVal; 
				this.GetFindBy = lFindBy; // Method
				this.GetWebField = lWebField; // URI
				this.CommandStat = false;
				this.ActualResult = "";
	}
	
	
	public String checkToken(String val) {
		
		String newVal = val;
		if(val.equalsIgnoreCase("Bearer Token()")) {
			newVal = "Bearer " + StoreValServiceTokenJSON;
		}
		
		return newVal;
	}
	
	public HttpURLConnection RequestProperty(HttpURLConnection con, String ReqHeader, String[] ReqHeaderArr, String POST_PARAMS) {
		
	   	    String[] ReqHeaderArrSplit = null;
	   	    if(!ReqHeader.isEmpty()) {
	   	    	//single header
	   	    	ReqHeaderArrSplit = ReqHeader.split("=");
	   	    	con.setRequestProperty(ReqHeaderArrSplit[0], checkToken(ReqHeaderArrSplit[1]));
	   	    	con = withBodyParam(con,POST_PARAMS);
	   	    }else {
	   	    	// multiple header
	   	    	
	   	    	if(ReqHeaderArr!=null) { // if has a request header
	   	   	    	for(String h: ReqHeaderArr) {
	   	   	    		ReqHeaderArrSplit = h.split("=");
	   	   	    	con.setRequestProperty(ReqHeaderArrSplit[0], checkToken(ReqHeaderArrSplit[1]));
	   	   	    	}
	   	   	    	con = withBodyParam(con,POST_PARAMS);
	   	    	}
	   	    	
	   	    }
	   	    
		return con;
	} 
	
	public HttpURLConnection withBodyParam(HttpURLConnection con, String POST_PARAMS) {
		
		try {
			con.setDoOutput(true);
  	   	    OutputStream os = con.getOutputStream();
  	   	    os.write(POST_PARAMS.getBytes());
  	   	    os.flush();
  	   	    os.close();
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
		 return con;
	}

   	public void StoreValJSONService(){

   		try {
   	   		
    		// generating request details
			String ReqBody = GetWebFieldVal.trim();
   			String ReqHeader = "";
   			String[] ReqHeaderArr = null;
   			
   			if(ReqBody.contains("|")) { // if has a request header
   				String[] splitGetWebFieldVal = ReqBody.split("\\|");
   				ReqBody = splitGetWebFieldVal[1];
   				
   				if(splitGetWebFieldVal[0].contains(",")) {
   					// multiple header
   					ReqHeaderArr = splitGetWebFieldVal[0].split("\\,");
   				}else
   				{
   					// single header
   					ReqHeader = splitGetWebFieldVal[0];
   				}

   			}
   			

   	   	   		final String POST_PARAMS = ReqBody;
   	   	   	    URL urlForGetRequest = new URL(this.GetWebField.trim());
   	   	   	    String readLine = null;
   	   	   	    HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
   	   	   	    connection.setRequestMethod(GetFindBy);

   	   	   	    connection = RequestProperty(connection,ReqHeader,ReqHeaderArr,POST_PARAMS);
   	   	   	    
   	   		   	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
   	   		   	StringBuffer response = new StringBuffer();
   	   		   	
   	   		   	while ((readLine = in .readLine()) != null) {
   	   		   		response.append(readLine);
   	   		   	} in .close();
 
    			StoreResponseCode = String.valueOf(connection.getResponseCode());
    			StoreValServiceResJSON = response.toString();
    			this.ActualResult = response.toString();
    			this.CommandStat = true;
			
   		}catch(Exception e)
   		{
   			//e.printStackTrace();
   		}
   		
   	}
    
   	public void StoreValTokenService(){
   		try {
   	   		StoreValServiceTokenJSON = "";
   	   		final String POST_PARAMS = this.GetWebFieldVal.trim();
   	   	    URL urlForGetRequest = new URL(this.GetWebField.trim());
   	   	    String readLine = null;
   	   	    HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
   	   	    connection.setRequestMethod(GetFindBy);
   	   	    connection.setRequestProperty("Content-Type", "application/json");
   	   	    connection.setDoOutput(true);
   	   	    OutputStream os = connection.getOutputStream();
   	   	    os.write(POST_PARAMS.getBytes());
   	   	    os.flush();
   	   	    os.close();
   	   	    int responseCode = connection.getResponseCode();
   	   	    this.StoreValServiceTokenResCode = String.valueOf(responseCode);
   	   	    
   	   	    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
   		   	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
   		   	        StringBuffer response = new StringBuffer();
   		   	        while ((readLine = in .readLine()) != null) {
   		   	            response.append(readLine);
   		   	        } in .close();
   		   	        
   		   	   	    StoreValServiceTokenJSON = JsonPath.read(response.toString(), "$.access_token");
   		   	   	    this.CommandStat = true;
   	   	    }	   
   	   	this.ActualResult = StoreValServiceTokenJSON;
   	   	TimeUnit.SECONDS.sleep(2);
   		}catch(Exception e)
   		{
   			e.printStackTrace();
   		}
   		
   	}
   	
	public String GetCommandStat()
	{
		String r = "Fail";
		if(this.CommandStat)
		{
			r= "Pass";
		}
		return r;
	}
	
	public String GetActualResult() {
		return this.ActualResult;
	}
   	/*
	public String GetJSONRequestBody()
	{
		String[] st = this.GetWebFieldVal.trim().split("\\|");
		//System.out.println("Body: " + st[0].toString());
		return st[0].toString();
	}

	public String GetJSONLocator()
	{
		String[] st = this.GetWebFieldVal.trim().split("\\|");
		//System.out.println("Locator: " + st[1].toString());
		return st[1].toString();
	}
	*/
}
