package com.mightyjava.Query_and_Ranker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class TrendsWI {
	public static Connection con;
	public static Statement st;
	public static ResultSet rs;
	
	public static Statement st_temp;
	public static ResultSet rs_temp;
	
	public void GetTrends() throws SQLException {
		
		 try {
	        	Class.forName("com.mysql.jdbc.Driver");
	        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
	        	st = con.createStatement();
	        	st_temp = con.createStatement();


	        }catch(Exception e){
	        	System.out.println(e.getMessage());
 }   
		 
		StanfordCoreNLP stanfordCoreNLP= Piepline.getPipeline();

			
		String query = "SELECT * FROM `userqueries`"; 
     rs =  st.executeQuery(query);

     String UserQuery;
     String Country;
     int Counter = 0;
     int PreviousCount=0;
     int FinalCount;
     while(rs.next()) {
     	UserQuery=rs.getString("Query");
     	Country=rs.getString("Country");
     	CoreDocument coreDocument = new CoreDocument (UserQuery); 
 		stanfordCoreNLP.annotate(coreDocument);
 		List <CoreLabel> coreLabels = coreDocument.tokens();
 		
 		for(CoreLabel coreLabel : coreLabels) {
 			
 			String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
 			System.out.println(coreLabel.originalText() + "---" + ner);
 			if(ner.equals("PERSON")){
 				query = "SELECT COUNT(*) FROM trends WHERE name = '"+coreLabel.originalText()+"' AND location = '"+Country+"'";
 				rs_temp = st_temp.executeQuery(query);
		            while(rs_temp.next()){
		            	Counter = Integer.parseInt((rs_temp.getString("COUNT(*)")));
		            }
		            if(Counter==0) {
		            	query = "INSERT INTO `trends` (`name`,`Count`,`location`)"
		        		 		+ " VALUES ('" + coreLabel.originalText() + "','" 
		        		 					   + 1 +"' , '"
		        		 					   + Country+ "')";
		            	st_temp.executeUpdate(query);
		    			System.out.println("Not Found Before");

		            }
		            else if(Counter !=0)
		            {
		            	query = "SELECT * FROM trends WHERE name = '"+coreLabel.originalText()+"' AND location = '"+Country+"'";
		            	rs_temp=st_temp.executeQuery(query);
		            	while(rs_temp.next())
		            	{
		            		PreviousCount=Integer.parseInt(rs_temp.getString("Count"));
		            	}
		            	FinalCount=PreviousCount+1;
		            	query="UPDATE `trends` SET `Count`= '"+FinalCount +"' WHERE name = '"+coreLabel.originalText()+"' AND location = '"+Country+"'";	
		            	st_temp.executeUpdate(query);
		            }
		            
 			}
 		}
     	
     }
     
     query= "TRUNCATE TABLE userqueries";
    	st.executeUpdate(query); // empty the ranked Table

     
	}
}
