package MarsWTA;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
 
public class DatabaseFactory {
public String GetWebField;
public String GetWebFieldVal;
public String GetCommand;
public String GetFindBy;
public boolean CommandStat;
public String ActualResult;
public static HashMap<String, String> Result = new HashMap<String, String>();	
public static Connection conn;	

public void SetVal(String lCommand,String lWebFieldVal, String lFindBy, String lWebField)
{
			this.GetCommand = lCommand;	
			this.GetWebFieldVal = lWebFieldVal; // URI
			this.GetFindBy = lFindBy;// Method
			this.GetWebField = lWebField; 
			this.CommandStat = false;
			this.ActualResult = "";
}

public void ResultValEqual() {
	
	try {
		String[] splitGetWebField = GetWebField.split("\\,");
		String AllRowVal = Result.get(splitGetWebField[0].trim().replace("GetRowColVal(", ""));
		int ColIdx  = Integer.parseInt(splitGetWebField[1].trim().replace(")", ""));
		String RowCelVal = "";
		
		if(AllRowVal.contains("|")) {
			String[] splitAllRowVal = AllRowVal.split("\\|");	
			RowCelVal = splitAllRowVal[ColIdx-1];
		}else {
			RowCelVal = AllRowVal;
		}
		
		ActualResult = RowCelVal;
		if(RowCelVal.equals(GetWebFieldVal)) {
			CommandStat = true;
		}
		
	}catch(Exception e) {
		e.printStackTrace();
	}
	
}

public void ResultValContains() {
	
	try {
		String[] splitGetWebField = GetWebField.split("\\,");
		String AllRowVal = Result.get(splitGetWebField[0].trim().replace("GetRowColVal(", ""));
		int ColIdx  = Integer.parseInt(splitGetWebField[1].trim().replace(")", ""));
		String RowCelVal = "";
		
		if(AllRowVal.contains("|")) {
			String[] splitAllRowVal = AllRowVal.split("\\|");	
			RowCelVal = splitAllRowVal[ColIdx-1];
		}else {
			RowCelVal = AllRowVal;
		}
		
		ActualResult = RowCelVal;
		if(RowCelVal.contains(GetWebFieldVal)) {
			CommandStat = true;
		}
		
	}catch(Exception e) {
		e.printStackTrace();
	}
	
}

public void UpdateVal() {
	try {
		Statement stmt = conn.createStatement();
		int rows = stmt.executeUpdate(GetWebField);
		
		if(rows>0) {
			CommandStat = true;
		}
		ActualResult = rows + " row/s updated";
	}catch(Exception e) {
		e.printStackTrace();
	}
}

public void StoreValResult() {
	   try{
		      String[] splitGetWebFieldVal = null;
		      int GetWebFieldValLength = 0;
		      int ResultCounter = 1;
		      String ResultPerRow = "";
		      if(GetWebFieldVal.contains(";")) { // columns
		    	  splitGetWebFieldVal = GetWebFieldVal.split(";");  
		    	  GetWebFieldValLength = splitGetWebFieldVal.length;
		      }
		      
		      if(Result.size()!=0) {
				Result.clear();
		      }
				
		      Statement stmt = conn.createStatement();
		      ResultSet rs = stmt.executeQuery(GetWebField);
		      	      
			      // Getting query result
			      while(rs.next()){
			         if(GetWebFieldValLength>0) { //  more than 1 columns
			        	  ResultPerRow = "";
			        	  for(int i=0; i<GetWebFieldValLength; i++) {
			        		  	if(i==GetWebFieldValLength-1) {
			        		  		ResultPerRow += rs.getString(splitGetWebFieldVal[i]);
			        		  	}else {
			        		  		ResultPerRow += rs.getString(splitGetWebFieldVal[i]) + "|";
			        		  	}	
			        	  }
			         }else {                      // single column
			        	  ResultPerRow = rs.getString(GetWebFieldVal);
			         }
			         
			          ResultPerRow = ResultPerRow.replace("||", "|Empty()|").replace("null", "Empty()").replace("NULL", "Empty()");
			          Result.put(String.valueOf(ResultCounter), ResultPerRow);
		        	  ResultCounter++;
		        	  
			      }
			      rs.close();
			      
			      if(!ResultPerRow.isEmpty()) {
				        // Generating Actual Result
				      	 String PrintResult = ""; 
				         if(GetWebFieldValLength>0) {
				   
				        	  PrintResult = "\nRow\t";
				        	  for(int i=0; i<GetWebFieldValLength; i++) {   // Header
				        		  
				        		  if(i==GetWebFieldValLength-1) {
				        			  PrintResult+= splitGetWebFieldVal[i];
				        		  }else {
				        			  PrintResult+= splitGetWebFieldVal[i] + "|";
				        		  }
				        		
				        	  }
				        	  PrintResult += "\n";
				        	  
				        	  for(int i=1; i<=Result.size(); i++) {
				        		PrintResult+=i + "\t" + Result.get(String.valueOf(i))  + "\n"; // content		            	 
				        	  }
				              
				         }else {
				        	
				        	PrintResult = "\nRow\t" + GetWebFieldVal + "\n"; // header
				        	for(int i=1; i<=Result.size(); i++) {
				        		PrintResult+=i + "\t" + Result.get(String.valueOf(i))  + "\n"; // content
				        	}
				        	
				         }
				         
				         ActualResult = PrintResult;
				         CommandStat = true;

			      }else {
				         ActualResult = "No Result";
				         CommandStat = false;			    	  
			      }
			      
		      
		      
		   }catch(SQLException se){
		      se.printStackTrace();
		   }
}
    

public void ConnectSQLServer() {
	  conn = null;
	  
      try {
    	  DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
    	  String dbURL = null;
    	  //GetWebFieldVal = "WindowsAuth()";
    	  String[] FieldColumn = GetWebField.split(";");
    	  
    	  if(GetWebFieldVal.equalsIgnoreCase("WindowsAuth()")) 
    	  {
    		  dbURL= "jdbc:sqlserver://" + FieldColumn[0] + ":1433;databaseName=" + FieldColumn[1] + ";integratedSecurity=true;";
    	  }else if(GetWebFieldVal.equalsIgnoreCase("SQLServerAuth()")) 
    	  {
    		  dbURL = "jdbc:sqlserver://" + FieldColumn[0] + "\\sqlexpress;user=" + FieldColumn[1] + ";password=" + FieldColumn[2];
    	  }
    	  
    	  conn = DriverManager.getConnection(dbURL);
          ActualResult = "Can't Connect";
          if (conn != null) {
              DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
              //DisplayDBMetaData(dm);
              //StoreColumnNames(dm);
              ActualResult = "Connected";
              CommandStat = true;
          }

      } catch (SQLException ex) {
          ex.printStackTrace();
      } 
      
	}

public void DisplayDBMetaData(DatabaseMetaData dm) {
	try {
	    System.out.println("Driver name: " + dm.getDriverName());
	    System.out.println("Driver version: " + dm.getDriverVersion());
	    System.out.println("Product name: " + dm.getDatabaseProductName());
	    System.out.println("Product version: " + dm.getDatabaseProductVersion());			
	}catch(SQLException ex) {
		ex.printStackTrace();
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

}
//https://www.codejava.net/java-se/jdbc/how-to-read-database-meta-data-in-jdbc