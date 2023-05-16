package project.frontend;
// import queryprocess.RetrievedDocument;

// import utilities.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import project.backend.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class ResultDisplay {
    private static FileWriter fw = null;
    private static String[] NUMBER_TO_INDEX = {
            "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "nineth", "tenth" };

    public static void displayDocuments(List<Document> searchResults , int currentPage) {
        try {
            fw = new FileWriter("./src/project/frontend/display/index.html", false);

            if (searchResults != null && searchResults.size() > 0) {
                int length = Math.min(searchResults.size(), 10);
                displayHeader(searchResults.size());
                if(length < 10){
                    for (int i = 0; i < length; i++) {
                        displayResult(i, searchResults.get(i));
                    }
                }
                else{
                    for (int i = (currentPage-1)*10; i < Math.min(((currentPage*10)-1),searchResults.size()); i++) {
                        displayResult(i-(currentPage-1)*10, searchResults.get(i));
                    }
                }
                displayFooter();
            
            } else {
                displayHeader(-1);
                displayFooter();
            }

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayHeader(int number) throws IOException {
        String str1 = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <script src="https://use.fontawesome.com/releases/v5.0.8/js/all.js"></script>
                    <link rel="stylesheet" href="style.css">
                    <title>Google Search Page</title>
                </head>
                <body>
                    <div class="main">
                        <header>
                            <div class="top_header">
                                <div id="logo"><img src="google.png" alt="logo">
                                </div>
                                <div id="search_bar">
                                    <input type="text" id="search" name="search" required />
                                    <i class="fas fa-times"></i>
                                    <span>|</span>
                                    <i class="fas fa-microphone"></i>
                                    <i class="fas fa-search"></i>
                                </div>
                                <div id="right_icons">
                                    <i class="fas fa-th"></i>
                                    <a href="#">Sign in</a>
                                </div>
                            </div>
                            <div class="bottom_header">
                                <div id="all">
                                    <i class="fas fa-search"></i>
                                    <p>All</p>
                                </div>
                                <div id="news">
                                    <i class="far fa-newspaper"></i>
                                    <p>News</p>
                                </div>
                                <div id="videos">
                                    <i class="far fa-stop-circle"></i>
                                    <p>Videos</p>
                                </div>
                                <div id="maps">
                                    <i class="fas fa-map-marker-alt"></i>
                                    <p>Maps</p>
                                </div>
                                <div id="images">
                                    <i class="fas fa-image"></i>
                                    <p>Images</p>
                                </div>
                                <div id="more">
                                    <i class="fas fa-ellipsis-v"></i>
                                    <p>More</p>
                                </div>
                                <a href="#" class="settings">Settings</a>
                                <a href="#" class="middle">Tools</a>
                                <a href="#" id="safesearch">SafeSearch on</a>
                            </div>
                        </header>
                        <div class="body">
                """;
        String str2 = "            <p>" + String.valueOf(number) + " results </p>   \n" +
                "            <div class=\"below_card\">\n" +
                "            </div>\n" +
                "            <div id=\"results\">\n";
        fw.write(str1 + str2);
    }

    private static void displayResult(int index, Document result) throws IOException {
        String desc;
        if (result.body().text().length() > 60) {
            desc = result.body().text().substring(0, 60);
        } else {
            desc = result.body().text();
        }

        String str1 = "<div class=\"" + NUMBER_TO_INDEX[index] + "_result\">\n" +
                "                    <div class=\"text_with_arrow_down\">\n" +
                "                        <p>" + result.baseUri() + "\n" +
                "                        </p>\n" +
                "                        <i class=\"fas fa-angle-down\"></i>\n" +
                "                    </div>\n" +
                "                    <a href=\"" + result.baseUri() + "\">" + result.title() + "</a>\n" +
                "                    <p>" + desc + "...</p>\n" +
                "                </div>\n" +
                "\n";
        fw.write(str1);
    }

    private static void displayFooter() throws IOException {
        String str1 = """
                    \s
                            </div>

                            <div class="last_part">
                            </div>
                            <div class="second_logo">
                                <div class="logo_arrow">
                                    <img src="./images/google.png" alt="">
                                    <i class="fas fa-arrow-right"></i>
                                </div>
                                <div id="pagination">
                                <a href="#" onclick="previousPage()">Previous</a>
                                <span id="currentPage">currentPage</span>
                                <a href="#" onclick="nextPage()">Next</a>
                              </div>
                              <script>
                              let currentPage = 1;
                              var u = document.getElementById("pagination").querySelector("span").textContent = currentPage;
                              function previousPage() {
                                  if (currentPage > 1) {
                                      currentPage--;
                                  }
                              }
                              function nextPage() {
                                  if (currentPage < 10) {
                                      currentPage++;
                                  }
                              }
                          </script>
                            </div>
                        </div>
                        <footer>
                        </footer>
                    </div>
                </body>
                </html>
                """;
        fw.write(str1);
    }

    public static void main(String[] args) throws IOException{
        db.connect();
        ArrayList<String> links = db.get_Not_Indexed();
        ArrayList<Document> docs = new ArrayList<Document>();
        for(String link : links){
            Document doc = Jsoup.connect(link).get();
            docs.add(doc);
        }
        db.disconnect();
        // display docs

        displayDocuments(docs,2);
}
}