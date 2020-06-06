package com.SearchEngine.CR_IND_QP_RK;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.sql.*;
import java.util.List;
import java.util.Random;


public class TrendsWI {
	Connection con;
	Statement st;
	ResultSet rs;
	int id;
	Statement st_temp;
	ResultSet rs_temp;
	long Start;
	long End;

	long StartTotal;
	long EndTotal;

	public TrendsWI(int id)
	{
		this.id=id;
	}
	public void GetTrends() throws SQLException {

		StartTotal=System.nanoTime();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			st = con.createStatement();
			st_temp = con.createStatement();


		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		StanfordCoreNLP stanfordCoreNLP= Pipeline.getPipeline();


		String query = "SELECT * FROM `userqueriestrends`";
		rs =  st.executeQuery(query);

		String UserQuery;
		String Country;
		int Counter = 0;
		int PreviousCount=0;
		int FinalCount;
		int upperbound = 2500;
		int int_random;
		Random rand = new Random();
		while(rs.next()) {
			Start=System.nanoTime();
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
						 //instance of random class
						int_random = rand.nextInt(upperbound);
						query = "INSERT INTO `trends` (`name`,`Count`,`location`,`id`,`primaryid`)"
								+ " VALUES ('" + coreLabel.originalText() + "','"
								+ 1 +"' , '"
								+ Country+ "','"+id+"','"+int_random+"')";
						st_temp.executeUpdate(query);

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
					End = System.nanoTime() - Start;
					System.out.println(End + "Total time per query");
				}
			}

		}
		query= "TRUNCATE TABLE userqueriestrends";
		st.executeUpdate(query); // empty the  Table

		EndTotal = System.nanoTime() - StartTotal;
		System.out.println(EndTotal + "Total time to calculate the trends");

	}
}