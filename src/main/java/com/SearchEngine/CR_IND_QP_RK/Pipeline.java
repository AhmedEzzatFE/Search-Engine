package com.SearchEngine.CR_IND_QP_RK;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;



public class Pipeline {

	private static StanfordCoreNLP stanfordCoreNLP;
	private static  Properties  properties;
	private static String propertiesName = "tokenize,ssplit,pos,lemma,ner";
	
	private Pipeline() {
		
	}
	static {
		properties= new Properties();
		properties.setProperty("annotators", propertiesName);
	}
	public static StanfordCoreNLP getPipeline() {
		if(stanfordCoreNLP == null)
		{
			stanfordCoreNLP= new StanfordCoreNLP(properties);
		}
		return stanfordCoreNLP;
	}
}
