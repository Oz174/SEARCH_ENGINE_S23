package project.backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import project.backend.org.tartarus.snowball.ext.porterStemmer;

public class TextProcessor {
    private HashSet<String> stopWords;
    public porterStemmer stemmer = new porterStemmer();
    public ArrayList<String> queries = new ArrayList<String>();

    public TextProcessor() throws IOException, FileNotFoundException {
        stopWords = new HashSet<String>();
        // add common stop words to the set
        BufferedReader stopwords = new BufferedReader(new FileReader("stopwords.txt"));
        String line;
        while ((line = stopwords.readLine()) != null) {
            stopWords.add(line);
        }
        stopwords.close();
    }


    private void processToken(String token, int doc_id, String Tag, int tag_counter, int word_pos) {
        try {
            Integer.parseInt(token);
            return;
        } catch (NumberFormatException e) {
        }
        String word = token.toLowerCase().replaceAll("[^a-z]", "");
        if (!stopWords.contains(word)) {
            if (word.equals("") ||  word.contains("www") )
                return;
            String literal = word;
            stemmer.setCurrent(word);
            stemmer.stem();
            word = stemmer.getCurrent();
            queries.add("(" + doc_id + ",\'" + literal + "\',\'" + word + "\',\'" + Tag + "\',"
                    + tag_counter + "," + word_pos + ")");
        }
    }

    public void ProcessElements(Elements Elements, String Tag, int doc_id) throws IOException {
        int tag_counter = 0;
        for (Element E : Elements) {
            int word_pos = 0;
            tag_counter++;
            String[] tokens = new String[0];
            tokens = E.ownText().split("\\s+");
            for (String token : tokens) {
                word_pos++;
                processToken(token, doc_id, Tag, tag_counter, word_pos);
            }
        }
    }

    public void ProcessKeywords(Elements keywords, int doc_id){
        if (keywords.isEmpty())
            return;
        // .first() will return the first <meta > tag with attribute keywords ... 
        // so the tag_counter will always be 1 , since the size of elements will be 1
        // so do you want to change it to accomodate multiple meta keywords tags , or just assume it's mostly one 
        // meta attribute keywords everywhere ??
        String[] keywords_list = keywords.first().attr("content").split(",\\s*");
        int keyword_number = 0;
        for (String keyword_phrase : keywords_list){
            int word_pos = 0;
            keyword_number++;
            String[] tokens = keyword_phrase.split("\\s+");
            for (String token : tokens){
                word_pos++;
                processToken(token, doc_id, "keyword", keyword_number, word_pos);
            }
        }
    }

}

