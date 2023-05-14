package project.backend;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import project.backend.org.tartarus.snowball.ext.porterStemmer;

public class TextProcessor {
    private HashSet<String> stopWords;
    private HashMap<String, Integer> words;
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

    public int getIndexofKey(String keyToFind) {
        int index = -1;
        int currentIndex = 0;

        // Iterate over the entries in the map
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            // Check if the current key is the one we're looking for
            if (entry.getKey().equals(keyToFind)) {
                // If it is, store its index and break out of the loop
                index = currentIndex;
                break;
            }

            // Increment the current index
            currentIndex++;
        }
        return index;
    }

    public void ProcessElements(Elements Elements, String Tag , int doc_id) throws IOException {
        words = new HashMap<String, Integer>();
        int tag_counter = 0;
        for (Element E : Elements) {
            tag_counter++;
            String[] tokens = new String[0];
            tokens = E.text().split("\\s+");
            for (String token : tokens) {
                // check if the token can be parsed to an integer
                try {
                    Integer.parseInt(token);
                    continue;
                } catch (NumberFormatException e) {
                }
                String word = token.toLowerCase().replaceAll("[^a-z]", "");
                if (!stopWords.contains(word)) {
                    words.put(word, tag_counter);
                }
            }
            break;
        }
        Iterator<Map.Entry<String, Integer>> i = words.entrySet().iterator();
        porterStemmer stemmer = new porterStemmer();
        
        while (i.hasNext()) {
            Map.Entry<String, Integer> entry = i.next();
            String word = entry.getKey();
            if (word == "")
                continue;
            String literal = word;
            stemmer.setCurrent(word);
            stemmer.stem();
            word = stemmer.getCurrent();
            queries.add("(" + doc_id + ",\'" + literal +"\',\'" + word + "\',\'" + Tag +"\'," + entry.getValue() + "," + getIndexofKey(literal) +")");
        }
    }

}
