package project.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Web_Indexer {
    static db data_search;
    static Parser jsoup_parser;
    TextProcessor text_processor;

    public static void main(String args[]) throws IOException {
        db.connect();
        BufferedReader reader = new BufferedReader(new FileReader("test.txt"));
        // ArrayList<String> Visited = new ArrayList<String>();
        TextProcessor textProcessor = new TextProcessor();
        String line;
        //todo needs threads addition 
        while ((line = reader.readLine()) != null) {
            // add each url in the database if not visited
            if (!db.isVisited(line)) {
                continue;
            }
            // add the url to the visited list
            db.add_url(line);
            // parse the url into a document
            Parser.Extract_Tags_from_URL(line);

            String title = Parser.title;
            textProcessor.ProcessElements(title,"title");

            // word , next word , stemmed , next_Stemmed , title
            String heading = Parser.Headings.text();
            textProcessor.ProcessElements(heading,"h1");

            String paragraph = Parser.Paragraphs.text();
            textProcessor.ProcessElements(paragraph,"p");

        }
        reader.close();
    }
}

// el mafrod ykon fe constructor ll indexer on the first intialization msh fl
// main y3ny
// then open the indices file b2a w y-index !