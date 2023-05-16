package project.backend;

import java.io.IOException;

// import javax.swing.text.html.parser.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {
    // build a constructor that takes file name of the links to be parsed
    Elements title;
    Elements keywords;
    Elements Headings;
    Elements Paragraphs;

    public void Extract_Tags_from_URL(String url) throws IOException {
        // parse the url into a document
        Document doc = Jsoup.connect(url).ignoreContentType(true).get();
        String lang = doc.getElementsByTag("html").first().attr("lang");
        if(!lang.contains("en") && lang!="") return;
        // get the title of the document
        title = doc.select("title");
        // get the keywords in meta
        keywords = doc.select("meta[name=keywords]");
        // get the headings
        Headings = doc.select("body h1");
        // get the paragraphs
        Paragraphs = doc.select("body p");
    }

}
