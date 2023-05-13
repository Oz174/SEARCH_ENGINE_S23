package project.main;

import java.net.MalformedURLException;
import java.util.ArrayList;
import project.db;

public class Main {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        //String startUrl="https://cu.blackboard.com";
        //Webcrawler crawler = new Webcrawler(startUrl);
        //crawler.start();
        /////////////////////////////////////////////
        ArrayList<String> seeds  = new ArrayList<>();
        seeds.add("https://www.geeksforgeeks.org/");
        seeds.add("https://www.imdb.com/");
        seeds.add("https://www.spotify.com/eg-en/");
        seeds.add("https://edition.cnn.com/");
        seeds.add("https://en.wikipedia.org/wiki/Main_Page");
        seeds.add("https://cu.blackboard.com");
        seeds.add("https://www.coursera.org/");
        seeds.add("https://www.javatpoint.com/");
        seeds.add("https://www.amazon.eg/");
        seeds.add("https://www.youm7.com");

        //connect to the database
        db.connect();
        //the list of URLs that have already been crawled
        ArrayList<String> isVisited = db.getVisited();
        
        //contains the list of URLs that still need to be crawled.
         ArrayList<String> toVisit = new ArrayList<String>();
        

        if (toVisit.isEmpty()) {
            for (String url : seeds)
                toVisit.add(url);
        }
 
        WebCrawler crawler = new WebCrawler(toVisit, isVisited);
        //crawler.start();
        System.out.println("threads number ");

        int NO_threads = 10;
        Thread [] t = new Thread [NO_threads];
        for(int i = 0; i < NO_threads; i++) t[i] = new Thread(new webCrawlerRunnable(crawler));
        for(int i = 0; i < NO_threads; i++) t[i].start();
        for(int i = 0; i < NO_threads; i++) t[i].join();

        //save the list of URLs that were crawled to a file called "test.txt"
        crawler.saveUrlsToFile("test.txt");
        db.disconnect();
    }

}