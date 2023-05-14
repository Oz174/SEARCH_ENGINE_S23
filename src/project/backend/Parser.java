package project.backend;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {
// build a constructor that takes file name of the links to be parsed
static Elements title;
static Elements keywords;
static Elements Headings;
static Elements Paragraphs; 
public static void Extract_Tags_from_URL(String url) throws IOException{
    try {
            // parse the url into a document 
            Document doc = Jsoup.connect(url).get();
            // get the title of the document 
            title = doc.select("title");
            // get the keywords in meta 
            keywords = doc.select("meta[name=keywords]");
            // get the headings 
            Headings = doc.select("body h1");
            // get the paragraphs 
            Paragraphs = doc.select("body p");
            // write the parsed data into a text file 
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
