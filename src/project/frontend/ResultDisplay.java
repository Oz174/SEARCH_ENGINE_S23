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
    private static String[] file_names = {"E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\land.html","E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index1.html","E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index2.html","E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index3.html","E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index4.html","E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index5.html","E:\\apache-tomcat-9.0.74-windows-x64\\apache-tomcat-9.0.74\\webapps\\ROOT\\index6.html"};
    private static String[] NUMBER_TO_INDEX = {
            "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "nineth", "tenth" };
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException {
        String query = request.getQueryString();
        String search = request.getParameter("search");
        if(search != null || query != null){
            // print their response in a new html page using getWriter
            response.setContentType("html/text");
            response.getWriter().println("<!doctype html> <html> <body> <h1>" + search +" </h1> </body></html>");
            response.getWriter().println("<h1> Hello " + query + "</h1>");
        }
    }
    private static void ClearHTML(){
        for(int i=1 ; i<7 ; i++){
            File file = new File(file_names[i]);
            file.delete();
            try {
                displayHeader(0, file_names[i]);
                displayFooter(file_names[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void displayHeader(int number , String filename) throws IOException {
        fw = new FileWriter(filename, true);
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
                            <form action=\"index1.html\" method=\"get\" id=\"search\">
                            <div id=\"search_bar\">
                                <input type=\"text\" placeholder=\"  Search...\" name=\"search\">
                                <button type=\"submit\"><i class=\"fa fa-search\"></i></button>
                            </div>
                        </form>
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

    private static void displayResult(int index, Document result, String filename) throws IOException {
        fw = new FileWriter(filename, true);
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

    private static void displayFooter(String filename) throws IOException {
        fw = new FileWriter(filename, true);
        String str1 = """
                        \s
                                </div>
                                <div class= \"last_part\">
                                <div class=\"numbers\">
                                <a href=\"index1.html\">1</a>
                                <a href=\"index2.html\">2</a>
                                <a href=\"index3.html\">3</a>
                                <a href=\"index4.html\">4</a>
                                <a href=\"index5.html\">5</a>
                                <a href=\"index6.html\">6</a>
                            </div>
                            </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """;
                fw.write(str1);
                fw.close();
                return;
    }
    
    public static void displayAll(ArrayList<Document> searchResults) throws IOException{
        ClearHTML(); // clear all generated htmls from before 
        int currentpage = 1;
        for(int i=0 ; i< searchResults.size(); i++){
            if(i%10==0){
            File file = new File(file_names[currentpage]);
            if(file.exists()){file.delete();}
            displayHeader(searchResults.size(), file_names[currentpage]);
            }
            displayResult(i%10, searchResults.get(i), file_names[currentpage]);
            if(i%10==9 || i==searchResults.size()-1){
                displayFooter(file_names[currentpage]);
                currentpage++;
            }
        }
    }

    // public static void main(String[] args) throws IOException{
        
    // }
}
