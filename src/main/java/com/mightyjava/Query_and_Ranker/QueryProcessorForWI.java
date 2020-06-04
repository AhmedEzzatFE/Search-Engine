package com.mightyjava.Query_and_Ranker;

import com.mightyjava.Query_and_Ranker.Ranker.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProcessorForWI {
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
			"%","^","&","*","(",")","-","_","=","+","/","\\",">","<",";",":","\'","{","}","`","[","]","\""};
	String QueryWI;
	String Location;
	int id;
	Connection con;
	Statement st;
	Statement st_ToGetThePrevRank;
	Statement st_Count; // to delete the ranker table

	Statement st_TruncateRT; // to delete the ranker table
	Statement st_OrdereRT; // to delete the ranker table
	Statement st_Old; // to delete the ranker table
	Statement st_InsertFinalRank; // to delete the ranker table
	ResultSet rs;
	ResultSet rs_ToGetThePrevRank;

	ResultSet rs_Count; // to count the documents have a single word
	ResultSet rs_InsertRT; // for insert
	ResultSet rs_ReadOldPopularity; // for insert
	int UrlsCount;
	List<URL> webLinks = new ArrayList<>();
	String tempUrl =null;
	String word ;
	int deleteRankedurls;
	long StartTotal;
	long EndTotal;
	long StartTemp;
	long EndTemp;
	long Start;
	long End;

	public QueryProcessorForWI(String query, String Location, int id, int delete){
		this.QueryWI=query;
		this.Location=Location;
		this.id=id;
		this.deleteRankedurls=delete;
	}
	public void Processor() throws IOException {
		StartTotal=System.nanoTime();
		String[] words = QueryWI.replace("\"", "").split(" ");
		boolean PhraseSearching=false;
		List<String> tokens = new ArrayList<>();

		if(QueryWI.charAt(0)=='"' && QueryWI.charAt(QueryWI.length()-1)=='"'  ) {
			PhraseSearching=true;
			word = QueryWI.replace("\"", "");
			tokens.add(word.toLowerCase());

		}
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
			st_ToGetThePrevRank= con.createStatement();
			st_TruncateRT= con.createStatement(); // to empty the ranked table
			st_OrdereRT= con.createStatement(); // to empty the ranked table
			st_Old = con.createStatement();
			st_InsertFinalRank= con.createStatement();
			st_Count=con.createStatement();

		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		String query;
		String deleteQuery = "TRUNCATE TABLE rankedurls1";
		String queryForInsert;
		String query_ReadOld="SELECT * FROM `oldpopularity`";
		String UpdateQuery;
		String CountQuery;
		int SearchedBefore=0;
		int count=0;
		String text;
		String patternString;
		Pattern pattern;
		Matcher matcher;
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
		double PreviousRank=0.0;
		String PreviousDesc="";
		String Desc="";

		try {
			Start = System.nanoTime();
			if(this.deleteRankedurls==1){
				st_TruncateRT.executeUpdate(deleteQuery); // empty the ranked Table
				deleteQuery = "TRUNCATE TABLE userqueries";
				st_TruncateRT.executeUpdate(deleteQuery); // empty the userQueries
			}
			End = System.nanoTime() - Start;
			System.out.println(End + "To delete the rankedUrls and Userqueries Tables");

			Start = System.nanoTime();

			CountQuery = "SELECT COUNT(*) FROM userqueries WHERE id = '"+id+"' AND Query= '"+QueryWI+"' AND image='"+0+"'";
			rs_Count = st_Count.executeQuery(CountQuery);
			while(rs_Count.next()){
				SearchedBefore = Integer.parseInt(rs_Count.getString("COUNT(*)"));
			}
			End = System.nanoTime() - Start;
			System.out.println(End + "To check if the user searched this query before (Count the userqueries table for matched ones)");

			Start = System.nanoTime();
			queryForInsert = "INSERT INTO `userqueriestrends` (`Query`,`Country`)"
					+ " VALUES ('" + QueryWI + "','"
					+ Location + "')";
			st.executeUpdate(queryForInsert);
			End = System.nanoTime() - Start;
			System.out.println(End + "To insert in the userqueriestrends the searched query )");

			if(SearchedBefore == 0)
			{
				Start = System.nanoTime();
				queryForInsert = "INSERT INTO `userqueries` (`Query`,`Country`,`id`,`image`)"
						+ " VALUES ('" + QueryWI + "','"
						+ Location + "','"+id+"','"+0+"')";
				st.executeUpdate(queryForInsert);
				End = System.nanoTime() - Start;
				System.out.println(End + "To insert in the userqueries the searched query )");

				List<Double> Geo_Date = new ArrayList<>();
				List<String> Titles = new ArrayList<>();

				Start = System.nanoTime();

				query= "SELECT * FROM `indexedurls`";
				rs=st.executeQuery(query);
				while(rs.next())
				{
					Titles.add(rs.getString("Title"));
					Geographic_and_DatePublished G_D= new Geographic_and_DatePublished(Location,(rs.getString("URLExtension")),rs.getString("DatePublished"));
					Geo_Date.add(G_D.GeographicDateScore());
					webLinks.add(new URL (rs.getString("URLs")));

				}
				End = System.nanoTime() - Start;
				System.out.println(End + "To Select all the links in indexed urls + Calculating the Geographic and Date Rank + Getting the Titles and Add it to a List and Save the Urls )");
				List<Double> Popularity = new ArrayList<>();
				double TotalNumberOfDocuments=0;

				Start = System.nanoTime();
				rs =  st.executeQuery(query_ReadOld);
				while(rs.next())
				{
					TotalNumberOfDocuments++;
					Popularity.add(Double.parseDouble(rs.getString("Popularity")));
				}
				End = System.nanoTime() - Start;
				System.out.println(End + "To Select all the links in oldPopularity + Getting the Popularity Add it to a List and count the no. of documents )");

				int Popularity_Geo_Date_Title_Count=0;
				double Popularity_Geo_Date=0.0;

				Start = System.nanoTime();

				for(URL url : webLinks) {

					Popularity_Geo_Date=Popularity.get(Popularity_Geo_Date_Title_Count)+Geo_Date.get(Popularity_Geo_Date_Title_Count);
					queryForInsert = "INSERT INTO `rankedurls1` (`Urls`,`Rank`,`description`,`Title`,`id`,`searchQuery`,`image`)"
							+ " VALUES ('" + url + "','"
							+ Popularity_Geo_Date + "','"+" "+"','"
							+ Titles.get(Popularity_Geo_Date_Title_Count) + "','"
							+ id + "','"+ QueryWI +"','"+0+"')";
					st_InsertFinalRank.executeUpdate(queryForInsert);
					Popularity_Geo_Date_Title_Count++;
				}
				End = System.nanoTime() - Start;
				System.out.println(End + "Inserting All the Links I will going to rank and putting the rank of the Popularity ,Date, Geographic Location + Inserting the Title of each Url )");

//	          	List<Double> RelvanceRank = new ArrayList<>();
				Start = System.nanoTime();

				for (String word : FinalQuery) {
					StartTemp = System.nanoTime();
					CountQuery = "SELECT COUNT(*) FROM indexertable1 WHERE Words = '"+word+"'";
					rs_Count = st_Count.executeQuery(CountQuery);
					while(rs_Count.next()){
						DocCounter = Double.parseDouble(rs_Count.getString("COUNT(*)"));
					}
					EndTemp = System.nanoTime() - StartTemp;
					System.out.println(EndTemp + "Counting the Number Of Documents the Word " + word + "excist in");

					if(DocCounter > 1500 ){
						query = "SELECT * FROM indexertable1 WHERE Words = '"+word+"' AND Occurrences > '"+ 15 +"' AND TitleOccurrences > '"+ 0 +"'";

					}
					else if(DocCounter > 1200 ){
						query = "SELECT * FROM indexertable1 WHERE Words = '"+word+"' AND Occurrences > '"+ 10 +"' AND TitleOccurrences > '"+ 0 +"'";

					}
					else if(DocCounter > 800 ){
						query = "SELECT * FROM indexertable1 WHERE Words = '"+word+"' AND Occurrences > '"+ 8 +"' AND TitleOccurrences > '"+ 0 +"'";

					}

					else if(DocCounter > 200 ){
						query = "SELECT * FROM indexertable1 WHERE Words = '"+word+"' AND Occurrences > '"+ 3 +"'";

					}
					else {
						query = "SELECT * FROM indexertable1 WHERE Words = '"+word+"'";
					}
					rs =  st.executeQuery(query);

					StartTemp = System.nanoTime();
					while(rs.next()){
						tempUrl=rs.getString("URLs");

						DocCounter=2;
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
						query = "SELECT * FROM rankedurls1 WHERE Urls = '"+tempUrl+"' AND id= '"+id+"' AND searchQuery= '"+this.QueryWI+"' AND image='"+0+"'";
						rs_ToGetThePrevRank=st_ToGetThePrevRank.executeQuery(query);
						while(rs_ToGetThePrevRank.next())
						{
							PreviousRank=Double.parseDouble(rs_ToGetThePrevRank.getString("Rank"));
							PreviousDesc=rs_ToGetThePrevRank.getString("description");
						}
						TotalRank=PreviousRank+FinalScore_Score;
						Desc=rs.getString("Sentence");
						System.out.println();
						Desc.concat(PreviousDesc + Desc);
						UpdateQuery="UPDATE `rankedurls1` SET `Rank`= '"+TotalRank +"', `description`= '"+ Desc +"' WHERE Urls = '"+tempUrl+"' AND id='"+id+"' AND image='"+0+"' AND searchQuery= '"+this.QueryWI+"'";
						st_InsertFinalRank.executeUpdate(UpdateQuery);
						TF_Score=0.0;
						IDF_Score=0.0;
						TF_IDF_Score=0.0;
						FinalScore_Score=0.0;

					}
				}
				rs.close();
				End = System.nanoTime() - Start;
				System.out.println(End + "Time Of the Actual Ranking");

				Start = System.nanoTime();

				if(PhraseSearching == true)
				{   query="SELECT * FROM rankedurls1 WHERE id='"+id+"' AND image= '"+0+"' AND searchQuery= '"+this.QueryWI+"' ORDER BY Rank DESC ";
					rs =  st.executeQuery(query);
					while(rs.next())
					{
						tempUrl=rs.getString("Urls");
						Document document = Jsoup.connect(tempUrl).get();
						text = document.body().text().toLowerCase();
						patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";
						pattern = Pattern.compile(patternString);
						matcher = pattern.matcher(text);

						while (matcher.find()) {
							query = "SELECT * FROM rankedurls1 WHERE Urls = '"+tempUrl+"' AND id= '"+id+"' ";
							rs_ToGetThePrevRank=st_ToGetThePrevRank.executeQuery(query);
							while(rs_ToGetThePrevRank.next())
							{
								PreviousRank=Double.parseDouble(rs_ToGetThePrevRank.getString("Rank"));
							}
							TotalRank=PreviousRank+100;
							UpdateQuery="UPDATE `rankedurls1` SET `Rank`= '"+TotalRank +"' WHERE Urls = '"+tempUrl+"' AND id= '"+id+"' AND image= '"+0+"' AND searchQuery= '"+this.QueryWI+"'";
							st_InsertFinalRank.executeUpdate(UpdateQuery);

						}
						count++;
						if(count == 20)
						{
							break;
						}
					}
				}
				End = System.nanoTime() - Start;
				System.out.println(End + "Phrase Searching");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EndTotal = System.nanoTime() - StartTotal;
		System.out.println(EndTotal + "Total time of both The Query Processor and the ranker");
	}


}