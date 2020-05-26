package com.mightyjava.Query_and_Ranker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.*;



public class Indexer {
    public static Set<URL> webLinks = new HashSet<>();
    public static Set<URL> indexedWebLinks = new HashSet<>();
    public static Connection con;
    public static Statement st;
    public static ResultSet rs;
    public static String[] wordsToIgnore={" ","","a","about", "above", "after", "again",
            "against", "ain", "all", "am", "an", "and", "any", "are", "aren",
            "aren't", "as", "at", "b", "be", "because", "been", "before", "being",
            "below", "between", "both", "but", "by", "c", "can", "could", "couldn",
            "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn",
            "doesn't", "doing", "don", "don't", "down", "during", "e", "each", "f", "few",
            "for", "from", "furr", "g", "h", "had", "hadn", "hadn't", "has", "hasn",
            "hasn't", "have", "haven", "haven't", "having", "he", "he'd", "he'll",
            "he's","her", "here","here's" ,"hers", "herself", "him", "himself",
            "his", "how","how's" ,"i","i'd", "i'll", "i'm", "i've","if", "in",
            "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "j", "just", "k",
            "l","ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn",
            "mustn't", "my", "myself", "n", "needn", "needn't", "no", "nor", "not",
            "now", "o", "of", "off", "on", "once", "only", "or", "other", "ought",
            "our", "ours", "ourselves", "out", "over", "own", "p", "q", "r", "re", "s", "same",
            "shan", "shan't", "she",  "she'd", "she'll", "she's", "should",
            "should've", "shouldn", "shouldn't", "so", "some", "such", "t",
            "than", "that", "that'll","that's", "the", "their", "theirs", "them",
            "themselves", "then", "there","there's","these", "they", "they'd",
            "they'll", "they're", "they've", "this", "those", "through", "to",
            "too", "u", "under", "until", "up", "v", "ve", "very", "w", "was", "wasn", "wasn't",
            "we", "we'd", "we'll", "we're", "we've", "were", "weren", "weren't",
            "what", "what's","when", "when's" , "where", "where's","which",
            "while", "who", "who's","whom", "why", "will", "why's", "with",
            "won", "won't","would","wouldn", "wouldn't","x", "y", "you", "you'd",
            "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves","z",
            "!","@","#","$","%","^","&","*","(",")","-","_","=","+","/","\\",">","<",";",
            ":","\'","{","}","`","[","]"};
    //This class will implement comparable because I want
    //to sort objects according to no. of occurrences using the compareTo() method
    public static class Word {
        String word;
        int noOfOccurrences;
        int noOfOccurrencesHeader1;
        int noOfOccurrencesHeader2;
        int noOfOccurrencesHeader3;
        int noOfOccurrencesHeader4;
        int noOfOccurrencesHeader5;
        int noOfOccurrencesHeader6;
        int noOfOccurrencesTitle;
        int noOfOccurrencesBold;
        String paragraph;

        Word()
        {
            noOfOccurrences=0;
            noOfOccurrencesHeader1=0;
            noOfOccurrencesHeader2=0;
            noOfOccurrencesHeader3=0;
            noOfOccurrencesHeader4=0;
            noOfOccurrencesHeader5=0;
            noOfOccurrencesHeader6=0;
            noOfOccurrencesTitle=0;
            noOfOccurrencesBold=0;
            paragraph="";
        }
        //Overriding the original function in Java.lang.object because equals() method
        //was overridden
        //This method returns the hash code value
        //for the word member of the object on which this method is invoked.
        //The value will be used in HashMap later
        @Override
        public int hashCode() { return word.hashCode(); }

        //Overriding the original function in Java.lang.object
        //compares word member in obj with word member in the calling object
        @Override
        public boolean equals(Object obj) { return word.equals(((Word)obj).word); }


    }

    public static void main(String[] args) throws IOException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost/projectdb1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
            st = con.createStatement();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //Retrieve what's in the crawler database
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

        //Retrieve the URLs that have already been indexed
        query = "SELECT * FROM indexedurls";
        try {
            rs =  st.executeQuery(query);
            while(rs.next()){
                indexedWebLinks.add(new URL(rs.getString("URLs")));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        webLinks.removeAll(indexedWebLinks);


        for(URL url : webLinks) {
            //The try catch block is here because sometimes Jsoup.connect(url.toString()).get()
            //can throw an exception
            try {

                //creating a HashMap where String is a key and Word object is a value
                //HashMap doesn’t allow duplicate keys but allows duplicate values.
                //That means A single key can’t contain more than 1 value but more than
                //1 key can contain a single value.
                //HashMap allows null key also but only once and multiple null values.
                Map<String, Word> wordsMap = new HashMap<String, Word>();

                Document document = Jsoup.connect(url.toString()).get();




                //for date modified
                //Document document = Jsoup.connect("https://experienceleaguecommunities.adobe.com/t5/adobe-experience-manager/published-time-for-assets-and-sites-on-publish-instance/qaq-p/175732/comment-id/6949").get();
                //for pubdate
                //Document document = Jsoup.connect("http://html5doctor.com/the-time-element/").get();
                //for uk extension
                //Document document = Jsoup.connect("https://amp.webcreationuk.co.uk/dt/wsd?gclid=EAIaIQobChMI8aXdmPKl6QIVUeJ3Ch2iDQEsEAAYAyAAEgJQTfD_BwE").get();
                String Host = url.getHost();
                String region="0";
                String checkRegion=Host.substring(Host.length()-2);
                if(checkRegion.equals("ae"))
                {
                    region="United Arab Emirates";
                }
                else if(checkRegion.equals("ar"))
                {
                    region="Argentina";
                }
                else if(checkRegion.equals("au"))
                {
                    region="Australia";
                }
                else if(checkRegion.equals("be"))
                {
                    region="Belgium";
                }
                else if(checkRegion.equals("bg"))
                {
                    region="Bulgaria";
                }
                else if(checkRegion.equals("br"))
                {
                    region="Brazil";
                }
                else if(checkRegion.equals("ca"))
                {
                    region="Canada";
                }
                else if(checkRegion.equals("cn"))
                {
                    region="People's Republic of China";
                }
                else if(checkRegion.equals("de"))
                {
                    region="Germany";
                }
                else if(checkRegion.equals("eg"))
                {
                    region="Egypt";
                }
                else if(checkRegion.equals("es"))
                {
                    region="Spain";
                }
                else if(checkRegion.equals("eu"))
                {
                    region="European Union";
                }
                else if(checkRegion.equals("fr"))
                {
                    region="France";
                }
                else if(checkRegion.equals("gr"))
                {
                    region="Greece";
                }
                else if(checkRegion.equals("in"))
                {
                    region="India";
                }
                else if(checkRegion.equals("it"))
                {
                    region="Italy";
                }
                else if(checkRegion.equals("ru"))
                {
                    region="Russia";
                }
                else if(checkRegion.equals("tr"))
                {
                    region="Turkey";
                }
                else if(checkRegion.equals("uk"))
                {
                    region="United Kingdom";
                }
                else if(checkRegion.equals("us"))
                {
                    region="United States of America";
                }

                //document.select("meta[itemprop=dateModified]") will select all meta-elements with
                //attribute itemprop and attribute value datePublished.
                //.first(); will only take the first one from all the elements that are found
                //meta is used if the website creator wants to hide this information
                //time is used if the website creator wants to show this information which is irrational
                Element meta = document.select("meta[itemprop=datePublished]").first();
                Element time1 = document.select("time[itemprop=datePublished]").first();
                Element time2 = document.select("time[pubdate]").first();
                //Element meta = doc.select("meta[itemprop=datePublished]").first();
                //getting the content attribute from the selected element
                //getting the datetime attribute from the selected element
                String date="0";
                if(meta!=null)
                {
                    date = meta.attr("content");
                    if(!(date.equals("")))
                    {
                        Integer x=1950;
                        for(int i=0;i<100;i++)
                        {
                            x+=1;
                            if(date.contains(x.toString()))
                            {
                                date=x.toString();
                            }

                        }
                    }
                    else
                    {
                        date="0";
                    }

                }
                if(time1!=null)
                {

                    date = time1.attr("datetime");
                    if(!(date.equals("")))
                    {
                        Integer x=1950;
                        for(int i=0;i<100;i++)
                        {
                            x+=1;
                            if(date.contains(x.toString()))
                            {
                                date=x.toString();
                            }

                        }
                    }
                    else
                    {
                        date="0";
                    }

                }
                if(time2!=null)
                {
                    date = time2.attr("datetime");
                    if(!(date.equals("")))
                    {
                        Integer x=1950;
                        for(int i=0;i<100;i++)
                        {
                            x+=1;
                            if(date.contains(x.toString()))
                            {
                                date=x.toString();
                            }

                        }
                    }
                    else
                    {
                        date="0";
                    }


                }




                //Getting the combined text and all its children from
                //the page body, excluding the HTML
                //these strings would be empty if no headers or bold words were found
                String text = document.body().text();
                ////////////////////////////////////////////////////////////////////
                Elements hTags = document.select("h1, h2, h3, h4, h5, h6");
                Elements h1Tags = hTags.select("h1");
                String textH1 = h1Tags.text().toLowerCase();
                Elements h2Tags = hTags.select("h2");
                String textH2 = h2Tags.text().toLowerCase();
                Elements h3Tags = hTags.select("h3");
                String textH3 = h3Tags.text().toLowerCase();
                Elements h4Tags = hTags.select("h4");
                String textH4 = h4Tags.text().toLowerCase();
                Elements h5Tags = hTags.select("h5");
                String textH5 = h5Tags.text().toLowerCase();
                Elements h6Tags = hTags.select("h6");
                String textH6 = h6Tags.text().toLowerCase();
                ////////////////////////////////////////////////////////////////////
                String textTitle = document.title().toLowerCase();
                ////////////////////////////////////////////////////////////////////
                Elements boldTags = document.getElementsByTag("b");
                String textBold = boldTags.text().toLowerCase();

                //BufferedReader reads text from a character-input stream,
                //buffering characters so as to provide for the efficient
                //reading of characters, arrays, and lines.
                //Create BufferedReader so the words can be counted
                //An InputStreamReader is a bridge from byte streams to character streams.
                //It reads bytes and decodes them into characters using a specified charset.
                //ByteArrayInputStream contains bytes to be read from the Input Stream.
                //getbytes() function in java is used to convert a string into sequence of
                //bytes and returns an array of bytes.
                //It takes no arguments and used default charset to encode the string into bytes.
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes())));
                String line="";

                BufferedReader readerHeader1 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH1.getBytes())));
                String lineHeader1="";

                BufferedReader readerHeader2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH2.getBytes())));
                String lineHeader2="";

                BufferedReader readerHeader3 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH3.getBytes())));
                String lineHeader3="";

                BufferedReader readerHeader4 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH4.getBytes())));
                String lineHeader4="";

                BufferedReader readerHeader5 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH5.getBytes())));
                String lineHeader5="";

                BufferedReader readerHeader6 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textH6.getBytes())));
                String lineHeader6="";

                BufferedReader readerTitle = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textTitle.getBytes())));
                String lineTitle="";

                BufferedReader readerBold = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textBold.getBytes())));
                String lineBold="";

                //readLine() returns a String containing the contents of the line, not including any
                //line-termination characters,
                //or null if the end of the stream has been reached
                //A line is considered to be terminated by any one of a line feed (‘\n’),
                //a carriage return (‘\r’) (enter),
                //or a carriage return followed immediately by a linefeed.
                porterStemmer stemmer = new porterStemmer();

                while ((line = reader.readLine()) != null) {
                    if((lineHeader1 = readerHeader1.readLine()) != null) {
                        //string split() method breaks a given string around
                        //matches of the given regular expression. we can add a second parameter as a limit,
                        //but if we don"t, then 0 is the default which means as many times
                        //as the regex is found
                        //ex: String str = "geekss@for@geekss";
                        //String[] arrOfStr = str.split("@");
                        // for (String a : arrOfStr)
                        //System.out.println(a);
                        //output:
                        //geekss
                        //for@geekss


                        //String[] words = lineHeader1.split(" ");
                        //[] is a character set meaning that w search for any of the characters
                        //inside it and ^ means starting with, but, [^0-9] means anything
                        //except 012345679
                        //+ means one or more, so it can be used to split at ?????
                        //which comes a lot
                        //source:https://www.youtube.com/watch?v=sa-TUpSx1JA

                        String[] words = lineHeader1.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                //get() takes a key and returns its corresponding value in the hash map
                                //put() inserts a key and a value to the hash map or if the key already exists,
                                //the previous value gets replaced by the new value
                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesHeader1++;
                            }

                        }
                    }

                    if((lineHeader2 = readerHeader2.readLine()) != null) {


                        //String[] words = lineHeader2.split(" ");
                        String[] words = lineHeader2.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesHeader2++;
                            }

                        }
                    }

                    if((lineHeader3 = readerHeader3.readLine()) != null) {

                        //String[] words = lineHeader3.split(" ");
                        String[] words = lineHeader3.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesHeader3++;
                            }

                        }
                    }

                    if((lineHeader4 = readerHeader4.readLine()) != null) {


                        //String[] words = lineHeader4.split(" ");
                        String[] words = lineHeader4.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    if(stemmedWord.equals("other"))
                                        wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesHeader4++;
                            }

                        }
                    }

                    if((lineHeader5 = readerHeader5.readLine()) != null) {


                        //String[] words = lineHeader5.split(" ");
                        String[] words = lineHeader5.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesHeader5++;
                            }

                        }
                    }

                    if((lineHeader6 = readerHeader6.readLine()) != null) {


                        //String[] words = lineHeader6.split(" ");
                        String[] words = lineHeader6.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesHeader6++;
                            }

                        }
                    }

                    if((lineTitle = readerTitle.readLine()) != null) {


                        //String[] words = lineTitle.split(" ");
                        String[] words = lineTitle.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);
                                }
                                wordObject.noOfOccurrencesTitle++;
                            }

                        }
                    }

                    if((lineBold = readerBold.readLine()) != null) {


                        //String[] words = lineBold.split(" ");
                        String[] words = lineBold.split("[^A-Za-z0-9]+");
                        for (String word : words) {
                            if ("".equals(word)) {
                                continue;
                            }

                            if (!(Arrays.asList(wordsToIgnore).contains(word))) {
                                stemmer.setCurrent(word);
                                stemmer.stem();
                                String stemmedWord= stemmer.getCurrent();

                                Word wordObject = wordsMap.get(stemmedWord);
                                if (wordObject == null) {
                                    wordObject = new Word();
                                    wordObject.word = stemmedWord;
                                    wordsMap.put(stemmedWord, wordObject);

                                }
                                wordObject.noOfOccurrencesBold++;
                            }

                        }
                    }

                    //to be used for the paragraph
                    line=line.replace("\""," ");
                    line=line.replace("\'"," ");
                    ///////////
                    //String[] words = line.split(" ");
                    String[] words = line.split("[^A-Za-z0-9]+");
                    for (String word : words) {
                        System.out.println(word);
                    }
                    for (String word : words) {
                        if ("".equals(word)) {
                            continue;
                        }

                        String word2=word.toLowerCase();
                        if (!(Arrays.asList(wordsToIgnore).contains(word2))) {
                            stemmer.setCurrent(word2);
                            stemmer.stem();
                            String stemmedWord= stemmer.getCurrent();
                            Word wordObject = wordsMap.get(stemmedWord);
                            if (wordObject == null) {
                                wordObject = new Word();
                                wordObject.word = stemmedWord;

                                int startIndex=0;
                                int endIndex=0;
                                int after=50;
                                int before=50;
                                int temp1=line.length();
                                int temp2=line.indexOf(word);
                                int temp3=temp2+word.length();
                                while(temp2-before<0)
                                {
                                    before-=1;
                                }
                                while(temp3+after>temp1-1)
                                {
                                    after-=1;
                                }
                                startIndex=temp2-before;
                                endIndex=temp3+after;
                                String temp4="..."+ line.substring(startIndex, endIndex) +"...";
                                wordObject.paragraph=temp4;


                                wordsMap.put(stemmedWord, wordObject);
                            }
                            wordObject.noOfOccurrences++;
                        }


                    }
                }
                //Closes the stream and releases any system resources associated with it.
                //Once the stream has been closed, further read(), ready(), mark(), reset(),
                //or skip() invocations will throw an IOException. Closing a previously closed
                //stream has no effect.
                reader.close();

                int counterForNumberOfWords=wordsMap.size();

                //System.out.println(wordsMap.values());
                for (Map.Entry<String, Word> word : wordsMap.entrySet()) {
                    try {
                        //
                        query = "INSERT INTO `indexertable1` (`URLs`,`Words`,`Occurrences`,"
                                + "`H1Occurrences`,`H2Occurrences`,`H3Occurrences`,`H4Occurrences`,"
                                + "`H5Occurrences`,`H6Occurrences`,`TitleOccurrences`,`BoldOccurrences`"
                                + ",`NumberOfWordsInThisLink`,`Sentence`)"
                                + " VALUES ('" + url.toString() + "','"
                                + word.getValue().word + "','"
                                + word.getValue().noOfOccurrences + "','"
                                + word.getValue().noOfOccurrencesHeader1 + "','"
                                + word.getValue().noOfOccurrencesHeader2 + "','"
                                + word.getValue().noOfOccurrencesHeader3 + "','"
                                + word.getValue().noOfOccurrencesHeader4 + "','"
                                + word.getValue().noOfOccurrencesHeader5 + "','"
                                + word.getValue().noOfOccurrencesHeader6 + "','"
                                + word.getValue().noOfOccurrencesTitle + "','"
                                + word.getValue().noOfOccurrencesBold + "','"
                                + counterForNumberOfWords + "','"
                                + word.getValue().paragraph + "')";

                        st.executeUpdate(query);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                if(counterForNumberOfWords>0)
                {
                    //splitter for query processor
//                    try {
//                        query = "INSERT INTO `indexertable1` (`URLs`,`Words`,`Occurrences`,"
//                                + "`H1Occurrences`,`H2Occurrences`,`H3Occurrences`,`H4Occurrences`,"
//                                + "`H5Occurrences`,`H6Occurrences`,`TitleOccurrences`,`BoldOccurrences`"
//                                + ",`NumberOfWordsInThisLink`,`Sentence`)"
//                                + " VALUES ('" + "A new url is comming" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "','"
//                                + "0" + "')";
//                        st.executeUpdate(query);
//                    } catch (SQLException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
                    textTitle=textTitle.replace("\"", " ");
                    textTitle=textTitle.replace("\'", " ");
                    try {
                        query = "INSERT INTO `indexedurls` (`URLs`,`URLExtension`,"
                                + "`DatePublished`,`Title`)"
                                + " VALUES ('" + url.toString() + "','"
                                + region + "','"
                                + date + "','"
                                + textTitle + "')";
                        st.executeUpdate(query);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }catch(final Exception | Error ignored) {}

        }


    }


}