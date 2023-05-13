package backend;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Web_Indexer {
static db data_search ;
static Parser jsoup_parser;
TextProcessor text_processor;
public static void main(String args[]) throws IOException{
    db.connect();
    BufferedReader reader = new BufferedReader(new FileReader("urls.txt"));
    // ArrayList<String> Visited = new ArrayList<String>();
    TextProcessor textProcessor = new TextProcessor();
    String line;
    while((line = reader.readLine()) != null){
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
