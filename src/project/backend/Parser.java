package project.backend;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {
// build a constructor that takes file name of the links to be parsed
static String title;
static String keywordsString;
static Elements Headings;
static Elements Paragraphs; 
public static void Extract_Tags_from_URL(String url) throws IOException{
    try {
            // parse the url into a document 
            Document doc = Jsoup.connect(url).get();
            // get the title of the document 
            title = doc.title();
            // get the keywords in meta 
            Elements keywords = doc.select("meta[name=keywords]");
            keywordsString = keywords.attr("content");
            // get the headings 
            Headings = doc.select("body h1");
            // get the paragraphs 
            Paragraphs = doc.select("body p");
            // write the parsed data into a text file 
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public static void main(String args[]) throws IOException{
Parser.Extract_Tags_from_URL("https://whatfix.com/blog/knowledge-sharing-platforms/");
System.out.println(Parser.Paragraphs.text());
}
}
