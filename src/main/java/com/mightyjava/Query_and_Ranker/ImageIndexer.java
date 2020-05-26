package com.mightyjava.Query_and_Ranker;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tartarus.snowball.ext.porterStemmer;

import java.sql.*;



public class ImageIndexer {
	 public static Set<URL> webLinks = new HashSet<>();
	 public static Set<URL> indexedWebLinks = new HashSet<>();
	 public static Connection con;
	 public static Statement st;
	 public static ResultSet rs;

	

	 public static void main(String[] args) throws IOException {
		 
		 try {
	        	Class.forName("com.mysql.jdbc.Driver");
	        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
	        	st = con.createStatement();
	        }catch(Exception e){
	        	System.out.println(e.getMessage());
        }    
		 
     	//Retrieve what's in the crawler database
        String query = "SELECT * FROM crawlertableurls1";
        try {
			rs =  st.executeQuery(query);
			while(rs.next()){
				webLinks.add(new URL(rs.getString("URLs")));
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //Retrieve the URLs that have already been indexed
        query = "SELECT * FROM indexedimagesurls";
        try {
			rs =  st.executeQuery(query);
			while(rs.next()){
				indexedWebLinks.add(new URL(rs.getString("URLs")));
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        webLinks.removeAll(indexedWebLinks);

        for(URL url : webLinks) {
	        //The try catch block is here because sometimes Jsoup.connect(url.toString()).get() 
        	//can throw an exception
	        try {
	        	
		        Document document = Jsoup.connect(url.toString()).get();
		        Elements images = document.body().select("img[src]");
		        String websiteTitle = document.title();
		        for(Element image:images)
		        {
		        	String src="0";
		        	String title="0";
		        	String alt="0";
		        	src=image.attr("src");
		        	title=image.attr("title");
		        	alt=image.attr("alt");
		        	if(!(alt.equals("") && title.equals("")))
		        	{
		        		//https://stackoverflow.com/questions/41575891/jsoup-extract-title-from-img-class
				        ///posts/41575891/ivc/92cf
			        	//was used to resolve noscript img tag
//			        	if(!(src.startsWith("https")))
//			        	{
//			        		src="0";
//			        	}
			        	if(title.equals(""))
			        	{
			        		title="0";
			        		String temp1=websiteTitle.toLowerCase();
			        		String temp2=temp1.replace("\'", "");
			        		String temp3=temp2.replace("\"", "");
			        		String temp4=alt.toLowerCase();
			        		String temp5=temp4.replace("\'", "");
			        		String temp6=temp5.replace("\"", "");
			        		 try {

				                 query = "INSERT INTO `image` (`SRC`,`Title_Url`,`Title_image`,"
				 	        		 		+ "`Alt_image`)"
				 	        		 		+ " VALUES ('" + new URL(src) + "','" 
				 	        		 		+ temp3 + "','"
				 	        		 		+ title + "','" 
				 	        		 		+ temp6 + "')"; 
									st.executeUpdate(query);

								} catch (SQLException e) {
							        System.out.println(websiteTitle);
							        System.out.println(alt);
							        System.out.println("aaaaaaaaaaaaaa");
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				        	
			        	}
			        	else
			        	{
			        		alt="0";
			        		String temp1=websiteTitle.toLowerCase();
			        		String temp2=temp1.replace("\'", "");
			        		String temp3=temp2.replace("\"", "");
			        		String temp4=title.toLowerCase();
			        		String temp5=temp4.replace("\'", "");
			        		String temp6=temp5.replace("\"", "");
			        		try {
	
				                 query = "INSERT INTO `image` (`SRC`,`Title_Url`,`Title_image`,"
				 	        		 		+ "`Alt_image`)"
				 	        		 		+ " VALUES ('" + new URL(src) + "','" 
				 	        		 		+ temp3 + "','"
				 	        		 		+ temp6 + "','" 
				 	        		 		+ alt + "')"; 
									st.executeUpdate(query);
									

								} catch (SQLException e) {
									System.out.println(websiteTitle);
							        System.out.println(title);
							        System.out.println("bbbbbbbbbb");
									e.printStackTrace();
								}
			        	}
			        	
		        	}
		        	
		        }

	        	query = "INSERT INTO `indexedimagesurls` (`URLs`) VALUES ('" + url + "')";
				st.executeUpdate(query);
	
	        }catch(final Exception | Error ignored) {}
	       
        }


    }

   
}