package com.SearchModel.Query_and_Ranker;


import java.net.MalformedURLException;
import java.sql.*;

public class FinalUrls{

    //Set is an collection of objects that doesn't take duplicate values
    public static Connection con;
    public static Statement st;
    public static Statement st2;

    public static Statement st3;

    public static ResultSet rs;
    public static ResultSet rs_LoopForRanked;


    public static void main(String[] args) throws MalformedURLException {

        con=null;
        try {
        	Class.forName("com.mysql.jdbc.Driver");
        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
        	st = con.createStatement();
        	st2 = con.createStatement();
        	st3 = con.createStatement();
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }    
        
        String query = "SELECT * FROM indexedurls";
    
		// Delete the un necessary links from elindexer table 
		String query_urlstoberanked="SELECT * FROM urlstoberanked";
		String url_Final=null;
		String QueryDeleteSelected;
		query = "SELECT * FROM indexedurls";
		int foundCount=0;
		 try {
				rs =  st.executeQuery(query);
				while(rs.next()){
					url_Final=rs.getString("URLs");
					rs_LoopForRanked=st2.executeQuery(query_urlstoberanked);
					while(rs_LoopForRanked.next())
					{
						if(url_Final.equals(rs_LoopForRanked.getString("URLs"))) {
							foundCount++;
							break;
						}
					}
					System.out.println(foundCount);
					if(foundCount == 0) {
					System.out.println("entered here");
					QueryDeleteSelected="DELETE FROM indexedurls WHERE URLs = '"+url_Final+"' ";
					st3.executeUpdate(QueryDeleteSelected);
					
					QueryDeleteSelected="DELETE FROM indexertable1 WHERE URLs = '"+url_Final+"' ";
					st3.executeUpdate(QueryDeleteSelected);
				
					}
					foundCount=0;
				}
					
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}

          
    }
       
          
    }
