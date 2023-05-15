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

    public void ProcessElements(Elements Elements, String Tag, int doc_id) throws IOException {
        int tag_counter = 0;
        porterStemmer stemmer = new porterStemmer();
        for (Element E : Elements) {
            int word_pos = 0;
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
                    if (word.equals(""))
                        continue;
                    word_pos++;
                    String literal = word;
                    stemmer.setCurrent(word);
                    stemmer.stem();
                    word = stemmer.getCurrent();
                    queries.add("(" + doc_id + ",\'" + literal + "\',\'" + word + "\',\'" + Tag + "\',"
                            + tag_counter + "," + word_pos + ")");
                }
            }
            break;
        }
    }

}
