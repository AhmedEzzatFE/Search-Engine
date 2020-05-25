package com.mightyjava.Query_and_Ranker;

import java.util.List;

public class Ranker {

	public static class TF{
		double NoOfOccurence;
		double DocumentLength;
		
		TF(double x,double y){
			NoOfOccurence=x;
			DocumentLength=y;
		}
		
		public double getNormalizedTf() {
			if(DocumentLength==0)
			{
				return 0;
			}
			else {
				return NoOfOccurence/DocumentLength;

			}
		}
	}
	public static class IDF{
		double TotalNumberOfDocuments;
		double NumberOfDocumentsTheTermExistIn;
		
		IDF(double x,double y){
			TotalNumberOfDocuments=x;
			NumberOfDocumentsTheTermExistIn=y;
		}
		
		public double getIDf() {
			if(NumberOfDocumentsTheTermExistIn==0 || TotalNumberOfDocuments==0 )
			{
				return 0;
			}
			else {
	        	
				return Math.log((TotalNumberOfDocuments)/NumberOfDocumentsTheTermExistIn);
			}
			
			
		}
	}
	public static class ScoreTf_Idf{
		double TF;
		double IDF;
		
		ScoreTf_Idf(double x,double y){
			TF=x;
			IDF=y;
		}
		
		public double getScore_TF_IDF() {
			return TF*IDF;
		}
	}
	
	public static class FinalScore{
		double Tf_Idf;
		double Title; //10
		double H1; // 9
		double H2; //8
		double H3; //7
		double H4; //6
		double H5; //5
		double H6; //4
		double Bold; //3
		
		
		FinalScore(double tfIdf , double title,double h1 ,double h2 ,double h3 ,double h4,double h5 ,double h6 ,double bold){
			 Tf_Idf=tfIdf;
			 Title=title;
			 H1=h1;
	    	 H2=h2;
	    	 H3=h3;
	    	 H4=h4;
	    	 H5=h5;
	    	 H6=h6;
	    	 Bold=bold;
		}
		
		public double getFinalScore() {
			return (Tf_Idf+20.0*Title+10.0*H1+2.5*H2+1.5*H3+1.3*H4+1.2*H5+1.0*H6+1.7*Bold);
		}
	}
	public static class Popularity_Relevance{
		double popularity;
		double relevance;
		
		
		Popularity_Relevance(double Popu , double Relv){
			popularity=Popu;
			relevance=Relv;
		}
		
		public double getFinalScore() {
			return (popularity+relevance);
		}
	}
	public static class Geographic_and_DatePublished{
		String Location;
		String Extension;
		int Date;
		
		
		Geographic_and_DatePublished(String loc , String ex, String date ){
			Location=loc.toLowerCase();
			Extension=ex.toLowerCase();
			Date=Integer.parseInt(date);
		}
		
		public double GeographicDateScore() {
			double total=0;
			if(Extension.equals(Location) && !Extension.equals("0")) {
				total=2.5;
				if(Date != 0 && Date >= 2015) {
					total+= 1.5;
					return total;
				}
				else if(Date != 0 && Date < 2015 && Date >=2000) {
					total+= 0.75;
					return total;
				}
				else if(Date != 0 && Date < 2000 && Date >=1950) {
					total+= 0.25;
					return total;
				}
				else {
					return total;
				}
			}
			else {
				if(Date != 0 && Date >= 2015) {
					total+= 1.5;
					return total;
				}
				else if(Date != 0 && Date < 2015 && Date >=2000) {
					total+= 0.75;
					return total;
				}
				else if(Date != 0 && Date < 2000 && Date >=1950) {
					total+= 0.25;
					return total;
				}
				else {
					return total;
				}
			}
		}
	}

	
	
	public static class ImageRanker{
		List<String> Words ;
		String Title_image;
		String Alt;
		String Title_url;
		
		
		
		ImageRanker(List<String> word , String title_url, String title_image,String alt ){
			Words=word;
			Title_image=title_image.toLowerCase();
			Title_url=title_url.toLowerCase();
			Alt=alt.toLowerCase();

		}
		
		public double ImageScore() {
			double totalRank=0;
	          for (String word : Words) {
	        		if(Title_url.contains(word))
	    			{
	    				totalRank+=7;
	    				
	    			}
	    			if(!Title_image.equals("0") && Title_image.contains(word) ) {
	    				totalRank+=10;
	    			}
	    			if(!Alt.equals("0") && Alt.contains(word) ) {
	    				totalRank+=15;
	    			}
	          }
	 
		
			return totalRank;
		}
	}
}
	


