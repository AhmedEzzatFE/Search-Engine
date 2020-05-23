package com.mightyjava.Query_and_Ranker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.tartarus.snowball.ext.porterStemmer;

import com.mightyjava.Query_and_Ranker.Ranker.FinalScore;
import com.mightyjava.Query_and_Ranker.Ranker.IDF;
import com.mightyjava.Query_and_Ranker.Ranker.ScoreTf_Idf;
import com.mightyjava.Query_and_Ranker.Ranker.TF;

public class QueryProcessorForWI {
	public static Connection con;
	 public static Statement st;
	 public static Statement st2;
	 public static Statement st_TruncateRT; // to delete the ranker table
	 public static Statement st_OrdereRT; // to delete the ranker table
	 public static Statement st_Old; // to delete the ranker table
	 public static Statement st_InsertFinalRank; // to delete the ranker table
	 public static ResultSet rs;
	 public static ResultSet rs_2; // to count the documents have a single word
	 public static ResultSet rs_InsertRT; // for insert 
	 public static ResultSet rs_ReadOldPopularity; // for insert 
	 public static int UrlsCount;
	 public static String tempUrl;
	 public static String temp2Url;
	 public static String[] wordsToIgnore={" ","","a","about", "above", "after", "again",
     		"against", "ain", "all", "am", "an", "and", "any", "are", "aren",
     		"aren't", "as", "at", "be", "because", "been", "before", "being",
     		"below", "between", "both", "but", "by", "can", "could", "couldn",
     		"couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn",
     		"doesn't", "doing", "don", "don't", "down", "during", "each", "few",
     		"for", "from", "furr", "had", "hadn", "hadn't", "has", "hasn",
     		"hasn't", "have", "haven", "haven't", "having", "he", "he'd", "he'll",
     		"he's","her", "here","here's" ,"hers", "herself", "him", "himself",
     		"his", "how","how's" ,"i","i'd", "i'll", "i'm", "i've","if", "in",
     		"into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just",
     		"ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn",
     		"mustn't", "my", "myself", "needn", "needn't", "no", "nor", "not",
     		"now", "o", "of", "off", "on", "once", "only", "or", "other", "ought",
     		"our", "ours", "ourselves", "out", "over", "own", "re", "s", "same",
     		"shan", "shan't", "she",  "she'd", "she'll", "she's", "should",
     		"should've", "shouldn", "shouldn't", "so", "some", "such", "t",
     		"than", "that", "that'll","that's", "the", "their", "theirs", "them",
     		"themselves", "then", "there","there's","these", "they", "they'd",
     		"they'll", "they're", "they've", "this", "those", "through", "to",
     		"too", "under", "until", "up", "ve", "very", "was", "wasn", "wasn't",
     		"we", "we'd", "we'll", "we're", "we've", "were", "weren", "weren't",
     		"what", "what's","when", "when's" , "where", "where's","which",
     		"while", "who", "who's","whom", "why", "will", "why's", "with",
     		"won", "won't","would","wouldn", "wouldn't", "y", "you", "you'd",
     		"you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
		  String QueryWI;
		  public QueryProcessorForWI(String query){
			  QueryWI=query;
		  }
		  public boolean Processor() {
			  String[] words = QueryWI.split(" ");
			  porterStemmer stemmer = new porterStemmer();
			  // List to add the Query after Removing the Stopping words
			  List<String> semiFinalQuery = new ArrayList<>();
			  // List to add the Query after stemming
			  List<String> FinalQuery = new ArrayList<>();
			  String stemmedWord = null;
			  // if true, so the word isnt a stopping word and it will be added to the semiFinalQuery
			  boolean addable=true; 

			  // looping on the query word by word, then looping on the Stopping words, if the query 
			  //word is a stopping word it won't be added to the list
			  for (int i=0; i<words.length;i++) {
				  for (String StoppingWords : wordsToIgnore) {
		              if (words[i].equals(StoppingWords)) {
		            	  addable=false;
		            	  break;
		              }	  
		          }  
				  if(addable== true) {
					  semiFinalQuery.add(words[i]);
				  }
				  addable=true;
			  }
	          for (String word : semiFinalQuery) {
	              if ("".equals(word)) {
	                  continue;
	              }
	              stemmer.setCurrent(word);
	              stemmer.stem();
	              stemmedWord = stemmer.getCurrent();
	              stemmedWord=stemmedWord.toLowerCase();
	              FinalQuery.add(stemmedWord);

	          }
	    	  System.out.println(FinalQuery);

			 try {
		        	Class.forName("com.mysql.jdbc.Driver");
		        	con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
		        	st = con.createStatement();
		        	st2= con.createStatement();
		        	st_TruncateRT= con.createStatement(); // to empty the ranked table
		        	st_OrdereRT= con.createStatement(); // to empty the ranked table
		        	st_Old = con.createStatement();
		        	st_InsertFinalRank= con.createStatement();

		        }catch(Exception e){
		        	System.out.println(e.getMessage());
	        }    
			String deleteQuery = "TRUNCATE TABLE rankedurlsezza";
	        String query = "SELECT * FROM indexertableezza";
	        String queryForInsert;
	        String queryIndexedUrls = "SELECT * FROM indexedurls";
			String query_ReadOld="SELECT * FROM `oldpopularity`";


	        try {
	        	rs =  st.executeQuery(queryIndexedUrls);
	        	double TotalNumberOfDocuments=0;
	        	
	        	while(rs.next())
	        	{
	        		TotalNumberOfDocuments++;
	        	}
				rs =  st.executeQuery(query);
	        	List<String> AllUrls = new ArrayList<>();
				st_TruncateRT.executeUpdate(deleteQuery); // empty the ranked Table
	            TF tf;
	            IDF idf;
	            ScoreTf_Idf tf_idf;
	            FinalScore final_score;
				
				double TF_Score=0.0;
				double IDF_Score=0.0;
				double TF_IDF_Score=0.0;
				double FinalScore_Score=0.0;
				double DocCounter =0.0; // to count how many documents have the word 
				double TotalRank=0.0; // for every link
	        	List<Double> RelvanceRank = new ArrayList<>();

				while(rs.next()){
				
					if(!("A new url is comming".equals(rs.getString("URLs"))))
					{
						tempUrl=rs.getString("URLs");
				          for (String word : FinalQuery) {
				              if (word.equals(rs.getString("Words"))) {	
				            	 rs_2 =  st2.executeQuery(query);
				            	 while(rs_2.next()) {
				            		 if (word.equals(rs_2.getString("Words"))) {
				            			 DocCounter++;
				            		 }
				            	 }
			            		tf=new TF(Double.parseDouble(rs.getString("Occurrences")),Double.parseDouble(rs.getString("NumberOfWordsInThisLink")));
								TF_Score=tf.getNormalizedTf();
				            	idf=new IDF(TotalNumberOfDocuments,DocCounter);
				            	IDF_Score=idf.getIDf();
				            	tf_idf= new ScoreTf_Idf(TF_Score,IDF_Score);
				            	TF_IDF_Score=tf_idf.getScore_TF_IDF();
				            	final_score=new FinalScore(TF_IDF_Score,Double.parseDouble(rs.getString("TitleOccurrences")),
				            			Double.parseDouble(rs.getString("H1Occurrences")),
				            			Double.parseDouble(rs.getString("H2Occurrences")),
				            			Double.parseDouble(rs.getString("H3Occurrences")),
				            			Double.parseDouble(rs.getString("H4Occurrences")),
				            			Double.parseDouble(rs.getString("H5Occurrences")),
				            			Double.parseDouble(rs.getString("H6Occurrences")),
				            			Double.parseDouble(rs.getString("BoldOccurrences")));
				            	FinalScore_Score=final_score.getFinalScore();
				            	TotalRank=TotalRank+FinalScore_Score;
				            	TF_Score=0.0;
				    			IDF_Score=0.0;
				    			TF_IDF_Score=0.0;
				    			FinalScore_Score=0.0;
				    			DocCounter =0.0; 
				            	
				              }
				          }
					}
					// insert the urls and its rank in the ranked urls
	 
					else if(("A new url is comming".equals(rs.getString("URLs")))) {
						if(tempUrl!= null) {
								RelvanceRank.add(TotalRank);
								AllUrls.add(tempUrl);
								TotalRank=0;	
								tempUrl=null;
						}
					}
					
				}
				int Counter=0;
				double Final_Rank=0.0;
	        	List<Double> FinalList = new ArrayList<>();
				// Read the old Popularity and added to the the Relvance Rank
				rs_ReadOldPopularity=st_Old.executeQuery(query_ReadOld);
				UrlsCount=0;
				while(rs_ReadOldPopularity.next())
	        	{
					Final_Rank=RelvanceRank.get(Counter)+Double.parseDouble(rs_ReadOldPopularity.getString("Popularity"));
					FinalList.add(Final_Rank);
					Counter++;
	        	}
		
				for (int i = 0; i < TotalNumberOfDocuments-1; i++)
		        {
		            // Find the minimum element in unsorted array
		            int max_idx = i;
		            for (int j = i+1; j < TotalNumberOfDocuments; j++)
		            {
		                if (FinalList.get(j) > FinalList.get(max_idx))
		                	max_idx = j;
		            }

		            // Swap the found minimum element with the first
		            // element
		            double temp = FinalList.get(max_idx);
		            FinalList.set(max_idx, FinalList.get(i));
		            FinalList.set(i, temp);
		            String tempString=AllUrls.get(max_idx);
		            AllUrls.set(max_idx, AllUrls.get(i));
		            AllUrls.set(i, tempString);

		        }
				rs_ReadOldPopularity=st_Old.executeQuery(query_ReadOld);
				UrlsCount=0;
				Counter=0;
				while(rs_ReadOldPopularity.next())
	        	{

					queryForInsert = "INSERT INTO `rankedurlsezza` (`URLs`,`Rank`)"
	        		 		+ " VALUES ('" + AllUrls.get(UrlsCount) + "','" 
	        		 		+ FinalList.get(Counter) + "')";
					st_InsertFinalRank.executeUpdate(queryForInsert);
					UrlsCount++;
					Counter++;
	        	}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
	        

	    }


	}