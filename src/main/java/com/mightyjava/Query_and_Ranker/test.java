package com.mightyjava.Query_and_Ranker;

import java.util.ArrayList;
import java.util.List;

public class test {

	public static void main(String[] args) {
		String Title_url="football - news, transfers, fixtures, scores, pictures";
		List<String> tokens= new ArrayList<>();
		tokens.add("football");
		int total =0;
		for (String word : tokens) {
			if(Title_url.contains(word))
			{
//				System.out.println("word");
				total+=100;
				System.out.println(total);
			}
		}
	}

}
