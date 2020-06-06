package com.mightyjava.Query_and_Ranker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForPopularity{

	//Set is an collection of objects that doesn't take duplicate values
	public static List<URL> webLinks = new ArrayList<>();
	public static Connection con;
	public static Statement st;
	public static ResultSet rs;

	public static Integer counter2=0;
	public static Integer counter3=0;

	public static class Link {
		URL mainLink;
		URL insideLink;

		Link(URL url, URL discoveredURL)
		{
			mainLink=url;
			insideLink=discoveredURL;
		}
	}
	public static Long start1= 0L;
	public static Long start2= 0L;
	public static Long start3= 0L;;
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
			System.out.println("aaaa");
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

		start1 = System.currentTimeMillis();
		//For finding the urls (in the database) that point to a url (in the database)
		for(URL url : webLinks) {
			//to make everything consistent
			int validation=0;
			int superValidation=0;
			for(int i=0;i<LinkAndLinkesInsideIt.size();i++) {
				if(LinkAndLinkesInsideIt.get(i).insideLink.toString().equals(url.toString()) && !(LinkAndLinkesInsideIt.get(i).mainLink.toString().equals(url.toString())))
				{
					try {

						query = "INSERT INTO `urlspointingtourl` (`URLs`,`PointingToIt`)"
								+ " VALUES ('" + url + "','"
								+ LinkAndLinkesInsideIt.get(i).mainLink + "')";

						st.executeUpdate(query);
						validation++;
						superValidation++;
						//System.out.println("inserting 1");
					} catch (final Exception | Error ignored) {}
				}

			}
			if(validation==0)
			{
				try {
					query = "INSERT INTO `urlspointingtourl` (`URLs`,`PointingToIt`)"
							+ " VALUES ('" + url + "','"
							+ url + "')";

					st.executeUpdate(query);
					superValidation++;
					//System.out.println("inserting 2");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(superValidation>0)
			{
				try {

					query = "INSERT INTO `urlspointingtourl` (`URLs`,`PointingToIt`)"
							+ " VALUES ('" + "A new url is comming" + "','"
							+ counter3.toString() + "')";
					st.executeUpdate(query);
					//System.out.println(counter3);
					counter3++;
					//System.out.println("inserting 3");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		//System.out.println(System.currentTimeMillis() - start1);
		//start2 = System.currentTimeMillis();
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
						+ " VALUES ('" + url + "','"
						+ counter + "')";
				st.executeUpdate(query);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				query = "INSERT INTO `urlstoberanked` (`URLs`)"
						+ " VALUES ('" + url + "')";
				st.executeUpdate(query);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//System.out.println(System.currentTimeMillis() - start2);
		//System.out.println(System.currentTimeMillis() - start1);
	}
}