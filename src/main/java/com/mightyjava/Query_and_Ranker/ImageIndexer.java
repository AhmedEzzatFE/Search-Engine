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
	 public static String[] wordsToIgnore={" ","","a","about", "above", "after", "again",
	     		"against", "ain", "all", "am", "an", "and", "any", "are", "aren",
	     		"aren't", "as", "at", "b", "be", "because", "been", "before", "being",
	     		"below", "between", "both", "but", "by", "c", "can", "could", "couldn",
	     		"couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn",
	     		"doesn't", "doing", "don", "don't", "down", "during", "e", "each", "f", "few",
	     		"for", "from", "furr", "g", "h", "had", "hadn", "hadn't", "has", "hasn",
	     		"hasn't", "have", "haven", "haven't", "having", "he", "he'd", "he'll",
	     		"he's","her", "here","here's" ,"hers", "herself", "him", "himself",
	     		"his", "how","how's" ,"i","i'd", "i'll", "i'm", "i've","if", "in",
	     		"into", "is", "isn", "isn't", "it", "it's", "its", "itself", "j", "just", "k",
	     		"l","ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn",
	     		"mustn't", "my", "myself", "n", "needn", "needn't", "no", "nor", "not",
	     		"now", "o", "of", "off", "on", "once", "only", "or", "other", "ought",
	     		"our", "ours", "ourselves", "out", "over", "own", "p", "q", "r", "re", "s", "same",
	     		"shan", "shan't", "she",  "she'd", "she'll", "she's", "should",
	     		"should've", "shouldn", "shouldn't", "so", "some", "such", "t",
	     		"than", "that", "that'll","that's", "the", "their", "theirs", "them",
	     		"themselves", "then", "there","there's","these", "they", "they'd",
	     		"they'll", "they're", "they've", "this", "those", "through", "to",
	     		"too", "u", "under", "until", "up", "v", "ve", "very", "w", "was", "wasn", "wasn't",
	     		"we", "we'd", "we'll", "we're", "we've", "were", "weren", "weren't",
	     		"what", "what's","when", "when's" , "where", "where's","which",
	     		"while", "who", "who's","whom", "why", "will", "why's", "with",
	     		"won", "won't","would","wouldn", "wouldn't","x", "y", "you", "you'd",
	     		"you'll", "you're", "you've", "your", "yours", "yourself", "yourselves","z",
	     		"!","@","#","$","%","^","&","*","(",")","-","_","=","+","/","\\",">","<",";",
	     		":","\'","{","}","`","[","]"}; 
	//This class will implement comparable because I want 
	//to sort objects according to no. of occurrences using the compareTo() method
	

	 public static void main(String[] args) throws IOException {
		 
		 try {
	        	Class.forName("com.mysql.jdbc.Driver");
	        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
	        	st = con.createStatement();
	        }catch(Exception e){
	        	System.out.println(e.getMessage());
        }    
		 
     	//Retrieve what's in the crawler database
        String query = "SELECT * FROM crawlertableurls";
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
        porterStemmer stemmer = new porterStemmer();
        porterStemmer stemmer2 = new porterStemmer();
        
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
			        		websiteTitle.replace("\'", " ");
			        		websiteTitle.replace("\"", " ");
			        		websiteTitle.replace(",", " ");
			        		websiteTitle.toLowerCase();
			        		alt.replace("\'", " ");
		        			alt.replace("\"", " ");
		        			alt.replace(",", " ");
		        			alt.toLowerCase();
			        		 try {
			        			 StringTokenizer token = new StringTokenizer(websiteTitle);
			        			 while (token.hasMoreTokens()) 
			        			 {
			        				 if (!(Arrays.asList(wordsToIgnore).contains(token.nextToken())))
			        				 {
			        					 stemmer.setCurrent(token.nextToken());
						                 stemmer.stem();
						                 String stemmedWord= stemmer.getCurrent();
						                 
						                 StringTokenizer token2 = new StringTokenizer(alt);
					        			 while (token2.hasMoreTokens()) 
					        			 {
					        				 if (!(Arrays.asList(wordsToIgnore).contains(token2.nextToken())))
					        				 {
					        					 stemmer2.setCurrent(token2.nextToken());
								                 stemmer2.stem();
								                 String stemmedWord2= stemmer2.getCurrent();

								                 query = "INSERT INTO `image` (`SRC`,`Title_Url`,`Title_image`,"
								 	        		 		+ "`Alt_image`)"
								 	        		 		+ " VALUES ('" + src + "','" 
								 	        		 		+ stemmedWord + "','"
								 	        		 		+ title + "','" 
								 	        		 		+ stemmedWord2 + "')"; 
													st.executeUpdate(query);
													query = "INSERT INTO `indexedimagesurls` (`URLs`) VALUES ('" + url + "')";
														st.executeUpdate(query);
					        				 }
					        				 
					        			 }
			        				 }
			        			 }
			        			
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				        	
			        	}
			        	else
			        	{
			        		alt="0";
			        		websiteTitle.replace("\'", " ");
			        		websiteTitle.replace("\"", " ");
			        		websiteTitle.replace(",", " ");
			        		websiteTitle.toLowerCase();
			        		title.replace("\'", " ");
			        		title.replace("\"", " ");
			        		title.replace(",", " ");
			        		title.toLowerCase();
			        		try {
			        			 StringTokenizer token = new StringTokenizer(websiteTitle);
			        			 while (token.hasMoreTokens()) 
			        			 {
			        				 if (!(Arrays.asList(wordsToIgnore).contains(token.nextToken())))
			        				 {
			        					 stemmer.setCurrent(token.nextToken());
						                 stemmer.stem();
						                 String stemmedWord= stemmer.getCurrent();
						                 
						                 StringTokenizer token2 = new StringTokenizer(title);
					        			 while (token2.hasMoreTokens()) 
					        			 {
					        				 if (!(Arrays.asList(wordsToIgnore).contains(token2.nextToken())))
					        				 {
					        					 stemmer2.setCurrent(token2.nextToken());
								                 stemmer2.stem();
								                 String stemmedWord2= stemmer2.getCurrent();

								                 query = "INSERT INTO `image` (`SRC`,`Title_Url`,`Title_image`,"
								 	        		 		+ "`Alt_image`)"
								 	        		 		+ " VALUES ('" + src + "','" 
								 	        		 		+ stemmedWord + "','"
								 	        		 		+ stemmedWord2 + "','" 
								 	        		 		+ alt + "')"; 
													st.executeUpdate(query);
													query = "INSERT INTO `indexedimagesurls` (`URLs`) VALUES ('" + url + "')";
														st.executeUpdate(query);
					        				 }
					        				 
					        			 }
			        				 }
			        			 }
			        			
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			        	}
			        	
		        	}
		        	
		        }
	
	        }catch(final Exception | Error ignored) {}
	       
        }


    }

   
}