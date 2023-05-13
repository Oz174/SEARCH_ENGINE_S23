package project.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import project.backend.org.tartarus.snowball.ext.porterStemmer;

public class TextProcessor {
    private HashSet<String> stopWords;

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

    public void ProcessElements(String Elements, String Tag) throws IOException {
        ArrayList<String> words = new ArrayList<String>();
        String filename = "indices.txt";
        FileWriter writer;
        File file = new File(filename);
        if(!file.exists()){writer = new FileWriter(filename);}
        else{writer = new FileWriter(filename, true);}
        while (Elements != null) {
            String[] tokens = Elements.split("\\s+");
            for (String token : tokens) {
                // check if the token can be parsed to an integer
                try {
                    Integer.parseInt(token);
                    continue;
                } catch (NumberFormatException e) {
                }
                String word = token.toLowerCase().replaceAll("[^a-z]", "");
                if (!stopWords.contains(word)) {
                    words.add(word);
                }
            }
            break;
        }
        Iterator<String> i = words.iterator();
        porterStemmer stemmer = new porterStemmer();   
        while (i.hasNext()) {
            String prev_word = i.next();
            if(prev_word == "") continue;
            writer.write(prev_word + "," + words.indexOf(prev_word));
            stemmer.setCurrent(prev_word);
            stemmer.stem();
            prev_word = stemmer.getCurrent();
            writer.write("," + prev_word + "," + Tag);
            writer.write("\n");
        }
        writer.close();
    }
    // for html files , save the html tags in an external data structure and ignore
    // the unnecessary tags

    public static void main(String args[]) throws IOException, FileNotFoundException {
        TextProcessor textprocessor = new TextProcessor();
        textprocessor.ProcessElements("jsoup.txt", "h1");
    }
}