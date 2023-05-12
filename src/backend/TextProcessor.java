package backend;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.tartarus.snowball.ext.porterStemmer;

public class TextProcessor{
    private HashSet<String> stopWords;

    public TextProcessor() throws IOException , FileNotFoundException{
        stopWords = new HashSet<String>();
        // add common stop words to the set
        BufferedReader stopwords = new BufferedReader(new FileReader("stopwords.txt"));
        String line;
        while((line = stopwords.readLine()) != null){
            stopWords.add(line);
        }
        stopwords.close();
    }

    public ArrayList<String> processFile(String filename) throws IOException {
        ArrayList<String> words = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\s+");
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
        }
        reader.close();
        return words;
    }

    // implement a port stemmer on the arrayList generated from the processed file
    public ArrayList<String> stem(ArrayList<String> words){
        ArrayList<String> stemmedWords = new ArrayList<String>();
        porterStemmer stemmer = new porterStemmer();
        for(String word : words){
            stemmer.setCurrent(word);
            stemmer.stem();
            stemmedWords.add(stemmer.getCurrent().toString());
        }
        return stemmedWords;
    }

    // for html files , save the html tags in an external data structure and ignore the unnecessary tags 
    

    public static void main(String args[]) throws IOException , FileNotFoundException{
        TextProcessor Parser = new TextProcessor();
        ArrayList<String> words = Parser.processFile("jsoup.txt");
        words = Parser.stem(words);
        FileWriter writer = new FileWriter("jsoup_parsed.txt");
        for(String word : words){
            if(word.length() != 0)
            writer.write(word + "\n");
        }
        writer.close();
    }
}
