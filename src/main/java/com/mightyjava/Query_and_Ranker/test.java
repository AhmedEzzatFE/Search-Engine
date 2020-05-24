package com.mightyjava.Query_and_Ranker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;


public class test {

	public static void main(String[] args) {
		String text = "I will come and meet Google Chrome the woods Google Chrome";

		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter the Search Query");
	    String SearchQuery = myObj.nextLine();  // Read user input
	    String words = SearchQuery.replace("\"", "");
		List<String> tokens = new ArrayList<>();
		tokens.add(words);
		
		String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
		    System.out.println(matcher.group(1));
		}
	}

}
