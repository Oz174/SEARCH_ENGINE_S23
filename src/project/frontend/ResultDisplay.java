package project.frontend;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import project.backend.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ResultDisplay")
public class ResultDisplay extends HttpServlet {
    private static FileWriter fw = null;
    private static String[] NUMBER_TO_INDEX = {
            "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "nineth", "tenth" };
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException {
       int currentpage =1;
        String query = request.getQueryString();
        response.setContentType("text/html");
        response.getWriter().write(query);
        if (query =="next") {currentpage++;}
        else{currentpage--;}
    }

    private static void displayHeader(int number) throws IOException {
        fw = new FileWriter("E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index.html", true);
        String str0 = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                <script src=\"https://use.fontawesome.com/releases/v5.0.8/js/all.js\"></script>
                <link rel=\"stylesheet\" href=\"style.css\">
                <title>Google Search Page</title>
            </head>
            <body>
                <div class=\"main\">
                    <header>
                        <div class=\"top_header\">
                            <div id=\"logo\"><img src=\"google.png\" alt=\"logo\">
                            </div>
                            <div id=\"search_bar\">
                                <input type=\"text\" id=\"search\" name=\"search\" required />
                                <i class=\"fas fa-times\"></i>
                                <span>|</span>
                                <i class=\"fas fa-microphone\"></i>
                                <i class=\"fas fa-search\"></i>
                            </div>
                        </div>
                        <div class=\"bottom_header\">
                            <div id=\"all\">
                                <i class=\"fas fa-search\"></i>
                                <p>All</p>
                            </div>
                            <div id=\"news\">
                                <i class=\"far fa-newspaper\"></i>
                                <p>News</p>
                            </div>
                            <div id=\"videos\">
                                <i class=\"far fa-stop-circle\"></i>
                                <p>Videos</p>
                            </div>
                            <div id=\"maps\">
                                <i class=\"fas fa-map-marker-alt\"></i>
                                <p>Maps</p>
                            </div>
                            <div id=\"images\">
                                <i class=\"fas fa-image\"></i>
                                <p>Images</p>
                            </div>
                            <div id=\"more\">
                                <i class=\"fas fa-ellipsis-v\"></i>
                                <p>More</p>
                            </div>
                        </div>
                    </header>
                    <div class=\"body\">
                """;
        String str1 = " <p>" + String.valueOf(number) + " results </p>   \n" +
                "            <div class=\"below_card\">\n" +
                "            </div>\n" +
                "            <div id=\"results\">\n";
        fw.write(str0 + str1);
        fw.close();
        return;
    }

    private static void displayResult(int index, Document result) throws IOException {
        fw = new FileWriter("E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index.html", true);
        String desc;
        if (result.body().text().length() > 60) {
            desc = result.body().text().substring(0, 60);
        } else {
            desc = result.body().text();
        }

        String str1 = "<div class=\"" + NUMBER_TO_INDEX[index] + "_result\">\n" +
                "        <div class=\"text_with_arrow_down\">\n" +
                "        <p>" + result.baseUri().trim() + "\n" +
                "        </p>\n" +
                "        <i class=\"fas fa-angle-down\"></i>\n" +
                "        </div>\n" +
                "        <a href=\"" + result.baseUri() + "\">" + result.title() + "</a>\n" +
                "        <p>" + desc + "...</p>\n" +
                "        </div>\n" +
                "\n";
        fw.write(str1);
        fw.close();
        return;
    }

    private static void displayFooter(int currentPage) throws IOException {
        fw = new FileWriter("E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index.html", true);
        String str1 ="";
        if(currentPage ==1){
            str1 = """
                \s
                        </div>
                        <div class=\"last_part\">
                        </div>
                        <div class=\"second_logo\">
                            <div id= \"pagination\">
                            <span id= \"currentPage\"></span>
                            <a href=\"ResultDisplay?next\"> Next </a>
                          </div>
                        </div>
                    </div>
                </div>
                </div>
            </body>
            </html>
            """;
        }
        else {
            str1 = """
                        \s
                                </div>
                                <div class= \"last_part\">
                                </div>
                                <div class=\"second_logo\">
                                    <div class=\"logo_arrow\">
                                        <img src=\"./images/google.png\" alt=\"\">
                                        <i class= \"fas fa-arrow-right\"></i>
                                    </div>
                                    <div id=\"pagination\">
                                    <a href= \"ResultDisplay?prev\"> Previous</a>"
                                    "<span id=\"currentPage\"> </span>"
                                    "<a href=\"ResultDisplay?next\" > Next </a>"
                                  "</div>
                                </div>
                            </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """;
                }
                fw.write(str1);
                fw.close();
                return;
    }
    public static void main(String[] args) throws IOException{
        File file = new File("E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index.html");
        if(file.exists()){file.delete();}
        db.connect();
        ArrayList<String> links = db.get_Not_Indexed();
        List<Document> searchResults = new ArrayList<Document>();
        for (String link : links) {
            try {
                Document doc = Jsoup.connect(link).get();
                searchResults.add(doc);
            } catch (Exception e) {
                System.out.println("Error in fetching the document");
            }
        }
        db.disconnect();
        int currentpage = 1;

        ResultDisplay.displayHeader(searchResults.size()); 
        if(searchResults.size() < 10){
        for(int i=0 ; i<10; i++){
            ResultDisplay.displayResult(i, searchResults.get(i));
        }
        }else{
            for(int i=(currentpage-1)*10; i < Math.min(searchResults.size(),(currentpage*10)); i++){
                ResultDisplay.displayResult(i-((currentpage-1)*10), searchResults.get(i));
            }
        }
        ResultDisplay.displayFooter(currentpage);
    }
}
