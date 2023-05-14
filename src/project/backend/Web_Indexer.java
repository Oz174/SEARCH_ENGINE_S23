package project.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Web_Indexer {
    static db data_search;
    static Parser jsoup_parser;
    TextProcessor text_processor;

    public static void main(String args[]) throws IOException {
        db.connect();
        //get urls from the database 
        ArrayList<String> urls = db.getVisited();
        BufferedReader reader = new BufferedReader(new FileReader("urls.txt"));
        // ArrayList<String> Visited = new ArrayList<String>();
        TextProcessor textProcessor = new TextProcessor();
        for(String url : urls) {
            // parse the url into a document
            Parser.Extract_Tags_from_URL(url);

            textProcessor.ProcessElements(Parser.title,"title");

            textProcessor.ProcessElements(Parser.Headings,"h1");

            textProcessor.ProcessElements(Parser.Paragraphs,"p");

        }
        reader.close();
    }
}

// el mafrod ykon fe constructor ll indexer on the first intialization msh fl
// main y3ny
// then open the indices file b2a w y-index !