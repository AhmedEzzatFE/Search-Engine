package com.mightyjava.Query_and_Ranker;


import com.mightyjava.Query_and_Ranker.Ranker.ImageRanker;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;







public class ImageQueryProcessor {

	public static Connection con;
	public static Statement st;

	public static Statement st_TruncateRT; // to delete the ranker table
	public static Statement st_InsertFinalRank; // to delete the ranker table
	public static ResultSet rs;
	public static ResultSet rs_ToGetThePrevRank;




	public static String tempUrl =null;
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
			"you'll", "you're", "you've", "your", "yours", "yourself", "yourselves","!","@","#","$",
			"%","^","&","*","(",")","-","_","=","+","/","\\",">","<",";",":","\'","{","}","`","[","]"};


	public static void main(String[] args) throws IOException {

		int id=2;
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
		String SearchQuery = myObj.nextLine();  // Read user input
		String[] words = SearchQuery.split(" ");
		// List to add the Query after Removing the Stopping words
		// List to add the Query after stemming
		List<String> FinalQuery = new ArrayList<>();
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
				FinalQuery.add(words[i]);
			}
			addable=true;
		}

		double TotalRank=0.0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			st = con.createStatement();
			st_TruncateRT= con.createStatement(); // to empty the ranked table
			st_InsertFinalRank= con.createStatement();

		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		String query = " ";
		try {
//    	query ="TRUNCATE TABLE rankedurls1";
//       	st_TruncateRT.executeUpdate(query); // empty the ranked Table
			query = "SELECT * FROM image";
			rs =  st.executeQuery(query);
			while(rs.next()){
				tempUrl=rs.getString("SRC");
				ImageRanker IR = new ImageRanker(FinalQuery,rs.getString("Title_Url"),rs.getString("Title_image"),rs.getString("Alt_image"));
				TotalRank= IR.ImageScore();

				query="INSERT INTO `rankedurls1` (`Urls`,`Rank`,`description`,`Title`,`id`,`searchQuery`,`image`)"
						+ " VALUES ('" + tempUrl + "','"
						+ TotalRank + "','"+ " " +"', '"+" "+"','"+ id+"','"+SearchQuery+"','"+1+"')";
				st_InsertFinalRank.executeUpdate(query);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
}