package backend;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class JsoupTest {
public static void main(String args[]){
    Document page = null; 
    try{
        page = Jsoup.connect("http://www.ssaurel.com/blog").get();
    }catch(Exception e){
        e.printStackTrace();
    }
    Elements links = page.select("a[href]");

    for(Element link : links){
        System.out.println("\n link : " + link.attr("href"));
        System.out.println("\n Text : " + link.text());
    }

}    
}
