package com.mightyjava.Query_and_Ranker;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tartarus.snowball.ext.porterStemmer;

import java.sql.*;



public class Indexer {
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
     		"you'll", "you're", "you've", "your", "yours", "yourself", "yourselves","z"};   
	//This class will implement comparable because I want 
	//to sort objects according to no. of occurrences using the compareTo() method
	 public static class Word {
	        String word;
	        int noOfOccurrences;
	        int noOfOccurrencesHeader1;
	        int noOfOccurrencesHeader2;
	        int noOfOccurrencesHeader3;
	        int noOfOccurrencesHeader4;
	        int noOfOccurrencesHeader5;
	        int noOfOccurrencesHeader6;
	        int noOfOccurrencesTitle;
	        int noOfOccurrencesBold;
	        
	        Word()
	        {
	 	         noOfOccurrences=0;
	 	         noOfOccurrencesHeader1=0;
	 	         noOfOccurrencesHeader2=0;
	 	         noOfOccurrencesHeader3=0;
	 	         noOfOccurrencesHeader4=0;
	 	         noOfOccurrencesHeader5=0;
	 	         noOfOccurrencesHeader6=0;
	 	         noOfOccurrencesTitle=0;
	 	         noOfOccurrencesBold=0;
	        }
	        //Overriding the original function in Java.lang.object because equals() method
	        //was overridden
		    //This method returns the hash code value 
		    //for the word member of the object on which this method is invoked.
	        //The value will be used in HashMap later
	        @Override
	        public int hashCode() { return word.hashCode(); }

	        //Overriding the original function in Java.lang.object
	        //compares word member in obj with word member in the calling object
	        @Override
	        public boolean equals(Object obj) { return word.equals(((Word)obj).word); }


	    }

	 public static void main(String[] args) throws IOException {
		 
		 try {
	        	Class.forName("com.mysql.jdbc.Driver");
	        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
	        	st = con.createStatement();
	        }catch(Exception e){
	        	System.out.println(e.getMessage());
        }    
		 
		//Retrieve what's in the crawler database
        String query = "SELECT * FROM crawlertableurlezza";
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
        query = "SELECT * FROM indexedurls";
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
	        	
	        	//creating a HashMap where String is a key and Word object is a value
	    		//HashMap doesn�t allow duplicate keys but allows duplicate values.
	    		//That means A single key can�t contain more than 1 value but more than
	    		//1 key can contain a single value.
	    		//HashMap allows null key also but only once and multiple null values.
	            Map<String, Word> wordsMap = new HashMap<String, Word>();
	            
		        Document document = Jsoup.connect(url.toString()).get();
		        //Getting the combined text and all its children from 
		        //the page body, excluding the HTML
		        String text = document.body().text().toLowerCase();
	        	 ////////////////////////////////////////////////////////////////////
		        // Group of all h-Tags
		        Elements hTags = document.select("h1, h2, h3, h4, h5, h6");
		        // Group of all h1-Tags
		        Elements h1Tags = hTags.select("h1");
		        String textH1 = h1Tags.text().toLowerCase();
		        // Group of all h2-Tags
		        Elements h2Tags = hTags.select("h2");
		        String textH2 = h2Tags.text().toLowerCase();
		        // Group of all h3-Tags
		        Elements h3Tags = hTags.select("h3");
		        String textH3 = h3Tags.text().toLowerCase();
		        // Group of all h4-Tags
		        Elements h4Tags = hTags.select("h4");
		        String textH4 = h4Tags.text().toLowerCase();
		        // Group of all h5-Tags
		        Elements h5Tags = hTags.select("h5");
		        String textH5 = h5Tags.text().toLowerCase();
		        // Group of all h6-Tags
		        Elements h6Tags = hTags.select("h6");
		        String textH6 = h6Tags.text().toLowerCase();
		        ////////////////////////////////////////////////////////////////////
		        String textTitle = document.title();
		        ////////////////////////////////////////////////////////////////////
		        Elements boldTags = document.getElementsByTag("b");
		        String textBold = boldTags.text().toLowerCase();
		        
		        //BufferedReader reads text from a character-input stream,
		        //buffering characters so as to provide for the efficient
		        //reading of characters, arrays, and lines.
		        //Create BufferedReader so the words can be counted
		        //An InputStreamReader is a bridge from byte streams to character streams.
		        //It reads bytes and decodes them into characters using a specified charset.
		        //ByteArrayInputStream contains bytes to be read from the Input Stream.
		        //getbytes() function in java is used to convert a string into sequence of
		        //bytes and returns an array of bytes.
		        //It takes no arguments and used default charset to encode the string into bytes.
		        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes())));
		        String line="";
		        
		        BufferedReader readerHeader1 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH1.getBytes())));
		        String lineHeader1="";
		        
		        BufferedReader readerHeader2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH2.getBytes())));
		        String lineHeader2="";
		        
		        BufferedReader readerHeader3 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH3.getBytes())));
		        String lineHeader3="";
		        
		        BufferedReader readerHeader4 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH4.getBytes())));
		        String lineHeader4="";
		        
		        BufferedReader readerHeader5 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH5.getBytes())));
		        String lineHeader5="";
		        
		        BufferedReader readerHeader6 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH6.getBytes())));
		        String lineHeader6="";
		        
		        BufferedReader readerTitle = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textTitle.getBytes())));
		        String lineTitle="";
		        
		        BufferedReader readerBold = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textBold.getBytes())));
		        String lineBold="";
		        
		        //readLine() returns a String containing the contents of the line, not including any 
		        //line-termination characters, 
		        //or null if the end of the stream has been reached
		        //A line is considered to be terminated by any one of a line feed (�\n�),
		        //a carriage return (�\r�) (enter),
		        //or a carriage return followed immediately by a linefeed.
		        porterStemmer stemmer = new porterStemmer();
		
		        while ((line = reader.readLine()) != null) {
		        	if((lineHeader1 = readerHeader1.readLine()) != null) {
		        		//string split() method breaks a given string around
			        	//matches of the given regular expression. we can add a second parameter as a limit,
			        	//but if we don"t, then 0 is the default which means as many times 
			        	//as the regex is found
			        	//ex: String str = "geekss@for@geekss"; 
			            //String[] arrOfStr = str.split("@"); 
			        	// for (String a : arrOfStr) 
			            //System.out.println(a); 
			        	//output:
			        	//geekss
			        	//for@geekss
		        		String[] words = lineHeader1.split("[^a-z0-9��������������������]+");
		                 for (String word : words) {
		                     if ("".equals(word)) {
		                         continue;
		                     }
		                     
		                     if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
				                //get() takes a key and returns its corresponding value in the hash map
				                //put() inserts a key and a value to the hash map or if the key already exists,
				                //the previous value gets replaced by the new value
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
			                     wordObject.noOfOccurrencesHeader1++;
				        	 }
		        			   
		                 }
		        	}
		        	
		        	if((lineHeader2 = readerHeader2.readLine()) != null) {
		        		String[] words = lineHeader2.split("[^a-z0-9��������������������]+");
		                for (String word : words) {
		                    if ("".equals(word)) {
		                        continue;
		                    }
		                    
		                    if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
			                     wordObject.noOfOccurrencesHeader2++;
				        	 }

		                }
		        	}
		        	
		        	if((lineHeader3 = readerHeader3.readLine()) != null) {
		        		String[] words = lineHeader3.split("[^a-z0-9��������������������]+");
		                for (String word : words) {
		                    if ("".equals(word)) {
		                        continue;
		                    }
		                    
		                    if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
			                     wordObject.noOfOccurrencesHeader3++;
				        	 }

		                }
		        	}
		        	
		        	if((lineHeader4 = readerHeader4.readLine()) != null) {
		        		String[] words = lineHeader4.split("[^a-z0-9��������������������]+");
		                for (String word : words) {
		                    if ("".equals(word)) {
		                        continue;
		                    }
		                    
		                    if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                     	if(stemmedWord.equals("other"))
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
			                     wordObject.noOfOccurrencesHeader4++;
				        	 }

		                }
		        	}
		        	
		        	if((lineHeader5 = readerHeader5.readLine()) != null) {
		        		String[] words = lineHeader5.split("[^a-z0-9��������������������]+");
		                for (String word : words) {
		                    if ("".equals(word)) {
		                        continue;
		                    }
		                    
		                    if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
			                     wordObject.noOfOccurrencesHeader5++;
				        	 }

		                }
		        	}
		        	
		        	if((lineHeader6 = readerHeader6.readLine()) != null) {
		        		String[] words = lineHeader6.split("[^a-z0-9��������������������]+");
		                for (String word : words) {
		                    if ("".equals(word)) {
		                        continue;
		                    }
		                    
		                    if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
			                     wordObject.noOfOccurrencesHeader6++;
				        	 }

		                }
		        	}
		        	
		        	if((lineTitle = readerTitle.readLine()) != null) {
		        		String[] words = lineTitle.split("[^a-z0-9��������������������]+");
		                   for (String word : words) {
		                       if ("".equals(word)) {
		                           continue;
		                       }
			                    
		                       if (!(Arrays.asList(wordsToIgnore).contains(word))) {
			                    	 stemmer.setCurrent(word);
					                 stemmer.stem();
					                 String stemmedWord= stemmer.getCurrent();
					                 
				                     Word wordObject = wordsMap.get(stemmedWord);
				                     if (wordObject == null) {
				                     	wordObject = new Word();
				                     	wordObject.word = stemmedWord;
				                        wordsMap.put(stemmedWord, wordObject);
				                     }
				                       wordObject.noOfOccurrencesTitle++;
					        	 }
		
		                   }
		           	}
		        	
		        	if((lineBold = readerBold.readLine()) != null) {
		        		String[] words = lineBold.split("[^a-z0-9��������������������]+");
		                for (String word : words) {
		                    if ("".equals(word)) {
		                        continue;
		                    }
		                    
		                    if (!(Arrays.asList(wordsToIgnore).contains(word))) {
		                    	 stemmer.setCurrent(word);
				                 stemmer.stem();
				                 String stemmedWord= stemmer.getCurrent();
				                 
			                     Word wordObject = wordsMap.get(stemmedWord);
			                     if (wordObject == null) {
			                     	wordObject = new Word();
			                     	wordObject.word = stemmedWord;
			                        wordsMap.put(stemmedWord, wordObject);
			                     }
				                    wordObject.noOfOccurrencesBold++;
				        	 }

		                }
		        	}
		        	
		        	String[] words = line.split("[^a-z0-9��������������������]+");
		            for (String word : words) {
		                if ("".equals(word)) {
		                    continue;
		                }
	                    
		                
		                if (!(Arrays.asList(wordsToIgnore).contains(word))) {
	                    	 stemmer.setCurrent(word);
			                 stemmer.stem();
			                 String stemmedWord= stemmer.getCurrent();
			                 
		                     Word wordObject = wordsMap.get(stemmedWord);
		                     if (wordObject == null) {
		                     	wordObject = new Word();
		                     	wordObject.word = stemmedWord;
		                        wordsMap.put(stemmedWord, wordObject);
		                     }
		                     wordObject.noOfOccurrences++;
			        	 }
		                	                  
		                
		            }
		        }
		        //Closes the stream and releases any system resources associated with it.
		        //Once the stream has been closed, further read(), ready(), mark(), reset(),
		        //or skip() invocations will throw an IOException. Closing a previously closed
		        //stream has no effect.
		        reader.close();
		
		        int counterForNumberOfWords=wordsMap.size();
		        
		        //System.out.println(wordsMap.values());
		        for (Map.Entry<String, Word> word : wordsMap.entrySet()) {
	        		 try {
	        			 //
	        		 query = "INSERT INTO `indexertableezza` (`URLs`,`Words`,`Occurrences`,"
	        		 		+ "`H1Occurrences`,`H2Occurrences`,`H3Occurrences`,`H4Occurrences`,"
	        		 		+ "`H5Occurrences`,`H6Occurrences`,`TitleOccurrences`,`BoldOccurrences`"
	        		 		+ ",`NumberOfWordsInThisLink`)"
	        		 		+ " VALUES ('" + url.toString() + "','" 
	        		 		+ word.getValue().word + "','"
	        		 		+ word.getValue().noOfOccurrences + "','" 
	        		 		+ word.getValue().noOfOccurrencesHeader1 + "','" 
	        		 		+ word.getValue().noOfOccurrencesHeader2 + "','" 
	        		 		+ word.getValue().noOfOccurrencesHeader3 + "','" 
	        		 		+ word.getValue().noOfOccurrencesHeader4 + "','" 
	        		 		+ word.getValue().noOfOccurrencesHeader5 + "','" 
	        		 		+ word.getValue().noOfOccurrencesHeader6 + "','" 
	        		 		+ word.getValue().noOfOccurrencesTitle + "','" 
	        		 		+ word.getValue().noOfOccurrencesBold + "','"
	        		 		+ counterForNumberOfWords + "')";
  	    			 
						st.executeUpdate(query);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
				}
		        //splitter for query processor
		        try {
	        		 query = "INSERT INTO `indexertableezza` (`URLs`,`Words`,`Occurrences`,"
	        		 		+ "`H1Occurrences`,`H2Occurrences`,`H3Occurrences`,`H4Occurrences`,"
	        		 		+ "`H5Occurrences`,`H6Occurrences`,`TitleOccurrences`,`BoldOccurrences`"
	        		 		+ ",`NumberOfWordsInThisLink`)"
	        		 		+ " VALUES ('" + "A new url is comming" + "','" 
	        		 		+ "0" + "','"
	        		 		+ "0" + "','" 
	        		 		+"0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "','" 
	        		 		+ "0" + "')";
						st.executeUpdate(query);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        
		        try {
	        		 query = "INSERT INTO `indexedurls` (`URLs`)"
	        		 		+ " VALUES ('" + url.toString() +"')";
						st.executeUpdate(query);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	
	        }catch(final Exception | Error ignored) {}
	       
        }


    }

   
}