package backend;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupTest {
public static void main(String args[]) throws IOException{
  // get a document from any url and turn it into plain text 
    Document doc = Jsoup.connect("https://stackoverflow.com/questions/12318097/why-do-we-use-serialization").get();
    //String head_text = doc.head().html();
    FileWriter writer = new FileWriter("jsoup.txt");
    //writer.write(head_text);
    // iterate over p tags in body and get their content 
    String body_text = doc.body().select("p").text().formatted();
    writer.write(body_text); 
    writer.close();
}
}