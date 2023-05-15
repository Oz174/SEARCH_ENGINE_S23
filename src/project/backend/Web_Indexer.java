package project.backend;

import java.io.IOException;
import java.util.ArrayList;

import project.backend.org.tartarus.snowball.ext.porterStemmer;

public class Web_Indexer {
    static db data_search;
    static Parser jsoup_parser;
   
    public static void main(String args[]) throws IOException {
        db.connect();
        // get urls from the database
        ArrayList<String> urls = db.get_Not_Indexed();

        // ArrayList<String> Visited = new ArrayList<String>();
        for (String url : urls) {

            TextProcessor textProcessor = new TextProcessor();
            // parse the url into a document
            Parser.Extract_Tags_from_URL(url);

            textProcessor.ProcessElements(Parser.title, "title", db.get_doc_id(url));

            textProcessor.ProcessElements(Parser.Headings, "h1", db.get_doc_id(url));

            textProcessor.ProcessElements(Parser.Paragraphs, "p", db.get_doc_id(url));

            textProcessor.ProcessKeywords(Parser.keywords , db.get_doc_id(url));

            db.add_to_ranker_dictionary(textProcessor.queries);

            db.set_Indexed(url);
        }
        System.out.println("Done!");
        porterStemmer stemmer = new porterStemmer();
        stemmer.setCurrent("india");
        stemmer.stem();
        System.out.println(db.get_link_from_indexed_word(stemmer.getCurrent()));
        db.disconnect();
    }
}

// el mafrod ykon fe constructor ll indexer on the first intialization msh fl
// main y3ny
// then open the indices file b2a w y-index !