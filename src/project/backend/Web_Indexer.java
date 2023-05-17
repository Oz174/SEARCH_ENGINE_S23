package project.backend;

import java.io.IOException;
import java.util.ArrayList;


public class Web_Indexer implements Runnable {
    static db data_search;

    private ArrayList<String> urls_to_index;

    public Web_Indexer(ArrayList<String> urls) {
        urls_to_index = urls;
    }

    public static void main(String args[]) throws IOException {

        db.connect();
        // get urls from the database
        ArrayList<String> urls = db.get_Not_Indexed();
        // Split the urls into chunks
        int chunkSize = 80;
        int thread_count = (int) Math.ceil((double) urls.size() / chunkSize);
        Thread[] threads = new Thread[thread_count];
        int thread_id = 0;
        // Create a thread to parse each chunk
        for (int i = 0; i < urls.size(); i += chunkSize, thread_id++) {
            System.out.println(i + ", " + Math.min(i + chunkSize, urls.size()));
            Web_Indexer wi = new Web_Indexer(
                    new ArrayList<String>(urls.subList(i, Math.min(i + chunkSize, urls.size()))));
            threads[thread_id] = new Thread(wi);
            threads[thread_id].start();
        }
        // Wait for all threads to finish
        for (int i = 0; i < thread_count; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done!");
        db.disconnect();
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Started!");
        for (String url : urls_to_index) {
            try {
                TextProcessor textProcessor = new TextProcessor();
                Parser jsoup_parser = new Parser();
                int doc_id = db.get_doc_id(url);
                // parse the url into a document
                jsoup_parser.Extract_Tags_from_URL(url);

                if (jsoup_parser.title != null)
                    textProcessor.ProcessElements(jsoup_parser.title, "title", doc_id);
                if (jsoup_parser.Headings != null)
                    textProcessor.ProcessElements(jsoup_parser.Headings, "h1", doc_id);
                if (jsoup_parser.Paragraphs != null)
                    textProcessor.ProcessElements(jsoup_parser.Paragraphs, "p", doc_id);
                if (jsoup_parser.keywords != null)
                    textProcessor.ProcessKeywords(jsoup_parser.keywords, doc_id);

                synchronized (db.class) {
                    db.add_to_ranker_dictionary(textProcessor.queries);
                    db.set_Indexed(url);
                }
            } catch (IOException e) {
                synchronized (System.out) {
                    System.out.println("******************************************************************");
                    System.out.println("Error in parsing " + url);
                    System.out.println("\n");
                    e.printStackTrace();
                    System.out.println("******************************************************************\n\n");
                }
                db.remove_url(url);
            }
        }
        System.out.println(Thread.currentThread().getName() + " Done!");
    }
}

// el mafrod ykon fe constructor ll indexer on the first intialization msh fl
// main y3ny
// then open the indices file b2a w y-index !