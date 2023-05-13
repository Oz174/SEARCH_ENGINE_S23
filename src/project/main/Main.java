package main;

import java.net.MalformedURLException;
import java.util.ArrayList;


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


        //the list of URLs that have already been crawled
        ArrayList<String> isVisited = database.getCrawled();
        
        //contains the list of URLs that still need to be crawled.
        ArrayList<String> toVisit = database.getNotCrawled();
        
        System.out.println("initially to visit size before seed: "+toVisit.size());

        if (toVisit.isEmpty()) {
            for (String url : seeds)
                toVisit.add(url);
        }
        //TODO if tovisit empty add seeds
        System.out.println("initially to visit size: "+toVisit.size());
        System.out.println("initially visited size: "+isVisited.size());

        WebCrawler crawler = new WebCrawler(toVisit, isVisited, database);
        //crawler.start();
        System.out.println("threads number ");

        int NO_threads = 10;
        Thread [] t = new Thread [NO_threads];
        for(int i = 0; i < NO_threads; i++) t[i] = new Thread(new webCrawlerRunnable(crawler));
        for(int i = 0; i < NO_threads; i++) t[i].start();
        for(int i = 0; i < NO_threads; i++) t[i].join();

        //save the list of URLs that were crawled to a file called "test.txt"
        crawler.saveUrlsToFile("test.txt");

        System.out.println("finally to visit size: "+crawler.getNumberofPagesToVisit());
        System.out.println("finally visited size: "+crawler.getNumberofVisitedPages());
    }

}