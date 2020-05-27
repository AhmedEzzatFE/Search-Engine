package com.mightyjava.Query_and_Ranker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;

public class WebCrawler implements Runnable{

	//Set is an collection of objects that doesn't take duplicate values
	public static Set<URL> webLinks = new HashSet<>();
	public static Integer maxThreads;
	public URL threadURL;
	public static String Allow = "Allow:";
	public static String DISALLOW = "Disallow:";
	public static Connection con;
	public static Statement st;
	public static ResultSet rs;
	public static Integer counter=0;
	public static Long start= 0L;
	public static Long end= 0L;

	public WebCrawler(URL firstURL) {
		Set<URL> initialSet = new HashSet<>();
		initialSet.add(firstURL);
		crawling(initialSet);
	}

	//another constructor for the threads
	public WebCrawler(String firstURL) {
		try {
			this.threadURL=new URL(firstURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Set<URL> initialSetThread = new HashSet<>();
		initialSetThread.add(this.threadURL);
		crawling(initialSetThread);
		System.out.println(System.currentTimeMillis() - start);
		//System.out.println("Finished Thread");
		//System.out.println(webLinks);
		//System.out.println("Finished Thread, webLinks' size:");
		//System.out.println(webLinks.size());
		//System.out.println(counter);
	}

	public static boolean checkingRobot(URL url) {
		//getHost() returns the host of a specified url
		//URL = https:// www.geeksforgeeks.org
		//Host =  www.geeksforgeeks.org
		String Host = url.getHost();
		// forming the URL of the robots.txt file because this is how it can be accessed in any
		// website (if it exists)
		String Robot = "http://" + Host + "/robots.txt";
		URL urlOfRobot;
		try {
			urlOfRobot = new URL(Robot);
		} catch (MalformedURLException e) {
			// couldn't make the URL
			//System.out.println("I failed");
			return false;
		}

		String Commands;
		try {
			//InputStream is used to represent an input stream of bytes
			//openStream() initiates a new TCP connection to the server that
			//the URL resolves to. An HTTP GET request is then sent over
			//the connection. If all goes right (i.e., 200 OK), the server
			//sends back the HTTP response message that carries the data payload
			//that is served up at the specified URL.
			InputStream urlOfRobotStream = urlOfRobot.openStream();
			//Reading the bytes (file) from the InputStream that the openStream()
			//method returns in order to retrieve the data payload into the program.
			byte forReading[] = new byte[1000];
			//read() reads next byte of data from the Input Stream,
			//this is why we use an array of
			//bytes instead of int
			//If no byte is available because the end of the stream has been reached,
			//the value -1 is returned.
			int numberOfCharRead = urlOfRobotStream.read(forReading);
			//String(byte[] bytes)
			//Constructs a new String by decoding the specified
			//subarray of bytes using the platform's default charset.
			Commands = new String(forReading);
			// if the file is more than what array b can take
			while (numberOfCharRead != -1) {
				numberOfCharRead = urlOfRobotStream.read(forReading);
				if (numberOfCharRead != -1) {
					String newCommands = new String(forReading);
					Commands += newCommands;
				}
			}
			//close() closes the input stream and
			//releases system resources associated with this stream to Garbage Collector.
			urlOfRobotStream.close();
			//System.out.println(strCommands);
		} catch (IOException e) {
			// if there is no robots.txt file, it is OK to search
			return true;
		}
		//The getFile() function is a part of URL class. The function getFile() returns
		//the file name of a specified URL. The getFile() function returns the path
		//and the query of the URL.
		//Url: https://www.javatpoint.com/java-threadpoolexecutor
		//File name in given url is : /java-threadpoolexecutor
		//https://www.youtube.com/signup
		//Url: /signup
		String URL = url.getFile();
		int indexD = 0;
		int indexA = 0;
		//The indexOf() method returns the first index at which
		//a given element can be found in the array, or -1 if it is not present.
		//Checking what's allowed if there is any
		while ((indexA = Commands.indexOf(Allow, indexA)) != -1) {
			indexA += Allow.length();
			//Path will be the whole string after "Allow:"
			String Path = Commands.substring(indexA);
			//StringTokenizer class breaks a string into tokens.
			//It is simple way to break string.
			StringTokenizer tokenizer = new StringTokenizer(Path);

			//if there are no more tokens, leave
			if (!tokenizer.hasMoreTokens())
				break;
			//goodPath will be the part  after "Allow:"
			//ex: Allow: /comment
			//goodPath will be "/comment"
			String goodPath = tokenizer.nextToken();
			if (URL.toString().equals(goodPath)) {
				//System.out.println("I failed3");
				return true;
			}


			if(goodPath.contains("*")) {
				//* in robot.txt should mean any word
				//ex: /*/users can be /channel/users
				//so to do this, I need to replace * with .*
				String badPathOptimizedForStar=goodPath.replace("*",".*");
				// creates a pattern to be searched
				Pattern p = Pattern.compile(badPathOptimizedForStar);
				//Search above pattern in URL
				Matcher m = p.matcher(URL);
				//test whether the regular expression matches the pattern.
				boolean b = m.find();
				if(b==true)
				{
					//System.out.println("I failed4");
					return true;
				}
			}
		}
		//The indexOf() method returns the first index at which
		//a given element can be found in the array, or -1 if it is not present.
		while ((indexD = Commands.indexOf(DISALLOW, indexD)) != -1) {
			indexD += DISALLOW.length();
			//Path will be the whole string after "Disallow:"
			String Path = Commands.substring(indexD);
			//StringTokenizer class breaks a string into tokens.
			//It is simple way to break string.
			StringTokenizer tokenizer = new StringTokenizer(Path);

			//if there are no more tokens, leave
			if (!tokenizer.hasMoreTokens())
				break;
			//badPath will be the part  after "Disallow:"
			//ex: Disallow: /comment
			//badPath will be "/comment"
			String badPath = tokenizer.nextToken();
			// if the URL starts with a disallowed path, it is not safe
			if (URL.indexOf(badPath) == 0) {
				return false;
			}


			if(badPath.contains("*")) {
				//* in robot.txt should mean any word
				//ex: /*/users can be /channel/users
				//so to do this, I need to replace * with .*
				String badPathOptimizedForStar=badPath.replace("*",".*");
				// creates a pattern to be searched
				Pattern p = Pattern.compile(badPathOptimizedForStar);
				//Search above pattern in URL
				Matcher m = p.matcher(URL);
				//test whether the regular expression matches the pattern.
				boolean b = m.find();
				if(b==true)
				{
					//System.out.println("I failed4");
					return false;
				}
			}
		}

		return true;
	}

	private void crawling(Set<URL> URLs) {
		//first, we remove the urls that were already visited; so as not to add them again
		try
		{
			URLs.removeAll(webLinks);
		}
		catch (UnsupportedOperationException e)
		{
			System.out.println(e.getMessage());
		}

		if(!URLs.isEmpty()) {
			synchronized(webLinks) {
				if(webLinks.size()>=5000) {
					return;
				}
				String query="";
				//Don't take anything from google like support.google.com \\. to make it specifically
				//search for the '.' before google, it should just be \. but java requires another \
				//to represent one '\'
				String unwanted = ".*\\.google.com";
				Pattern p = Pattern.compile(unwanted);
				for(URL url : URLs) {
					Matcher m = p.matcher(url.getHost());
					if(!(m.find())) {
						if(webLinks.add(url)==true) {
							try {
								counter++;
								query = "INSERT INTO `crawlertableurls1` (`URLs`) VALUES ('" + url.toString() + "')";
								st.executeUpdate(query);
							}catch(Exception e){
								System.out.println(e.getMessage());
							}
						}
					}
				}
				if(webLinks.size()>=5000) {
					return;
				}
				//webLinks.addAll(URLs);
			}

			Set<URL> newURLsFromEachCall = new HashSet<>();
			try {
				for(URL url : URLs) {

					//Document is a class from Jsoup that creates an html document
					//It takes a url which would be the url of this document
					//this essentially gets the url and puts its data in a document
					//The connect(url) method makes a connection to the url
					//and get() method return the html of the requested url.
					Document document = Jsoup.connect(url.toString()).get();
					//Elements is from jsoup and document.select("a[href]") returns elements
					//with a with href
					Elements linksFromAnchorTagsOnPage = document.select("a[href]");
					for(Element e : linksFromAnchorTagsOnPage) {
						String URLAsText = e.attr("abs:href");
						URL discoveredURL = new URL(URLAsText);
						if(checkingRobot(discoveredURL)) {
							newURLsFromEachCall.add(discoveredURL);

							synchronized(webLinks) {
								if(webLinks.size()>=5000) {
									return;
								}
							}
							synchronized(maxThreads) {
								if(maxThreads>0) {
									maxThreads--;
									new Thread (new WebCrawler(URLAsText)).start();
								}
							}
						}
					}

				}
			} catch(final Exception | Error ignored) {}
			crawling(newURLsFromEachCall);

		}

	}


	public static void main(String[] args) throws MalformedURLException {
		System.out.println("Enter max number of threads");
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		maxThreads=s.nextInt();
		System.out.println("You entered max threads: " + maxThreads);

		con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
			st = con.createStatement();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		//Retrieve what's already in the database
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
		//new URL is taken from java url
		start = System.currentTimeMillis();
		new WebCrawler(new URL("https://www.google.com/search?client=firefox-b-d&q=sports"));
		System.out.println(System.currentTimeMillis() - start);
		//System.out.println("Finished");
		//System.out.println(webLinks);
		//System.out.println(webLinks.size());
		//The url below is used to check for regex matching
		//https://www.youtube.com/user/manosman97/community

		//The url below can be passed to WebCrawler to crawl 5000 links
		//https://www.youtube.com/watch?v=TcOWwIQDpnE&list=RDTcOWwIQDpnE&start_radio=1
	}



}
