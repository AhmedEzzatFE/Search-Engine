package com.SearchModel.Query_and_Ranker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;

public class ForPopularity{

    //Set is an collection of objects that doesn't take duplicate values
    public static Set<URL> webLinks = new HashSet<>();
    public static Integer maxThreads;
    public URL threadURL;
    public static Connection con;
    public static Statement st;
    public static ResultSet rs;

    public static Integer counter=0;

    public static class Link {
        URL mainLink;
        URL insideLink;
        
        Link(URL url, URL discoveredURL)
        {
        	mainLink=url;
			insideLink=discoveredURL;
        }
    }
    
    public static void main(String[] args) throws MalformedURLException {
    	List<Link> LinkAndLinkesInsideIt = new ArrayList<Link>();

        con=null;
        try {
        	Class.forName("com.mysql.jdbc.Driver");
        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
        	st = con.createStatement();
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }    
        
        //Retrieve what's already in the database
        String query = "SELECT * FROM indexedurls";
        try {
			rs =  st.executeQuery(query);
			while(rs.next()){
				webLinks.add(new URL(rs.getString("URLs")));
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        Document document;
        for(URL url : webLinks) {
			try {
				document = Jsoup.connect(url.toString()).get();
				Elements linksFromAnchorTagsOnPage = document.select("a[href]");
	            for(Element e : linksFromAnchorTagsOnPage) {
	                String URLAsText = e.attr("abs:href");
	                URL discoveredURL = new URL(URLAsText);
	                Link x=new Link(url,discoveredURL);
	            	LinkAndLinkesInsideIt.add(x);
	            }  
			} catch (final Exception | Error ignored) {}
        }
        
    	try {
        	query = "TRUNCATE TABLE urlspointingtourl";
			st.executeUpdate(query);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        
    	try {
        	query = "TRUNCATE TABLE numberoflinksinurl";
			st.executeUpdate(query);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
      
      //For finding the urls (in the database) that point to a url (in the database)
      for(URL url : webLinks) {
      	//to make everything consistent
        int validation=0;
      	for(int i=0;i<LinkAndLinkesInsideIt.size();i++) {		
            if(LinkAndLinkesInsideIt.get(i).insideLink.toString().equals(url.toString()) && !(LinkAndLinkesInsideIt.get(i).mainLink.toString().equals(url.toString())))
            {
    			 try {
    				query = "INSERT INTO `urlspointingtourl` (`URLs`,`PointingToIt`)"
	        		 		+ " VALUES ('" + url + "','" 
	        		 		+ LinkAndLinkesInsideIt.get(i).mainLink + "')";
  	    			 
					st.executeUpdate(query);
					validation++;
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
            }
	 
		}    
      	if(validation==0)
      	{
      		try {
    				query = "INSERT INTO `urlspointingtourl` (`URLs`,`PointingToIt`)"
	        		 		+ " VALUES ('" + url.toString() + "','" 
	        		 		+ url.toString() + "')";
	  	    			 
					st.executeUpdate(query);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
      	}
      	
      	try {
				query = "INSERT INTO `urlspointingtourl` (`URLs`,`PointingToIt`)"
	        		 		+ " VALUES ('" + "A new url is comming" + "','" 
	        		 		+ "0" + "')";
	    			 
				st.executeUpdate(query);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
      	
      }
      
    //For finding the number of urls (in the database) inside a url (in the database)
    for(URL url : webLinks) {
    	int counter=0;
    	for(int i=0;i<LinkAndLinkesInsideIt.size();i++) {	
    		if(LinkAndLinkesInsideIt.get(i).mainLink.toString().equals(url.toString())) {
    			for(URL url2 : webLinks) {
		            if(url2.toString().equals(LinkAndLinkesInsideIt.get(i).insideLink.toString()))
		            {
		            	counter++;
		            }
                }
    		}
    	}
		
		try {
			query = "INSERT INTO `numberoflinksinurl` (`URLs`,`NumberOfLinks`)"
	        	 		+ " VALUES ('" + url.toString() + "','" 
	        	 		+ counter + "')";
	    		 
				st.executeUpdate(query);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			query = "INSERT INTO `urlstoberanked` (`URLs`)"
	        	 		+ " VALUES ('" + url.toString() + "')"; 
				st.executeUpdate(query);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		 
		}

          

       
          
    }



}