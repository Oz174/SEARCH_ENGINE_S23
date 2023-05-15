package project.backend;

import java.sql.*;
import java.util.ArrayList;

public class db {
    private static String Connection_String = "jdbc:sqlserver://Dc-OZER;databaseName=search_engine_db;integratedSecurity=true;encrypt=false;";
    private static String user = "sa";
    private static String pswd = "search_engine_db_S23";
    private static Connection con = null;

    // Connections
    public static void connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try {
                con = DriverManager.getConnection(Connection_String, user, pswd);
                System.out.println("Connected");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException cex) {
            System.out.println(cex);
        }
    }

    public static void disconnect() {
        // close the db connection
        try {
            con.close();
            System.out.println("Disconnected");
        } catch (Exception e) {
            System.out.println("Failed to disconnect from database");
        }
    }
                                            // Crawler // 
    // URLS added by this function are assumed not crawled unless set with the function setCrawled
    public static boolean add_url(String url) {
        // add url to the database
        try {
            String query = "INSERT INTO Docs VALUES (\'" + url + "\'," + "0,0);";
            Statement Stmt = con.createStatement();
            Stmt.execute(query);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to add url to the database");
            return false;
        }
    }

    //remove url 
    public static boolean remove_url(String url) {
        // remove url from the database
        try {
            String query = "DELETE FROM Docs WHERE link = \'" + url + "\';";
            Statement Stmt = con.createStatement();
            Stmt.execute(query);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to remove url from the database");
            return false;
        }
    }
    //set crawled 
    public static boolean set_crawled(String url) {
        // set the crawled column to 1
        try {
            String query = "UPDATE Docs SET crawled = 1 WHERE link = \'" + url + "\';";
            Statement Stmt = con.createStatement();
            Stmt.execute(query);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to set crawled to 1");
            return false;
        }
    }

    //get crawled
    public static ArrayList<String> get_Crawled() {
        // get the visited urls from the database
        ArrayList<String> Visited = new ArrayList<String>();
        try {
            // con = DriverManager.getConnection(pswd, user, Connection_String);
            String query = "SELECT * from Docs where crawled = 1;";
            Statement Stmt = con.createStatement();
            ResultSet rs = Stmt.executeQuery(query);
            while (rs.next()) {
                // get the links in the link column and add them to visited list
                Visited.add(rs.getString("link"));
            }
            return Visited;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Visited;
    }
    // get the non crawled websites 
    public static ArrayList<String> get_Not_Crawled() {
        // get the visited urls from the database
        ArrayList<String> Not_Crawled = new ArrayList<String>();
        try {
            String query = "SELECT * from Docs where crawled = 0;";
            Statement Stmt = con.createStatement();
            ResultSet rs = Stmt.executeQuery(query);
            while (rs.next()) {
                // get the links in the link column and add them to visited list
                Not_Crawled.add(rs.getString("link"));
            }
            return Not_Crawled;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Not_Crawled;
        
    }
                                            // Indexer //   

    // gets the not indexed urls 
    public static ArrayList<String> get_Not_Indexed() {
        // get the visited urls from the database
        ArrayList<String> Not_Indexed = new ArrayList<String>();
        try {
            // con = DriverManager.getConnection(pswd, user, Connection_String);
            String query = "SELECT * from Docs WHERE indexed = 0;";
            Statement Stmt = con.createStatement();
            ResultSet rs = Stmt.executeQuery(query);
            while (rs.next()) {
                // get the links in the link column and add them to visited list
                Not_Indexed.add(rs.getString("link"));
            }
            return Not_Indexed;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Not_Indexed;
    }
    // set the indexed urls 
    public static boolean set_Indexed(String url) {
        // get the visited urls from the database
        try {
            String query = "UPDATE Docs SET indexed = 1 WHERE link = \'" + url + "\';";
            Statement Stmt = con.createStatement();
            Stmt.execute(query);
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    // returns the doc_id for the given link , if not , returns -1
    public static int get_doc_id(String url) {
        // get the visited urls from the database
        try {
            String query = "SELECT * from Docs where link = \'" + url + "\';";
            Statement Stmt = con.createStatement();
            ResultSet rs = Stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("doc_id");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

    // Indexings
    public static boolean add_to_ranker_dictionary(ArrayList<String> Values) {
        try {
            for (String value : Values) {
                String query = "INSERT INTO Ranker_Dictionary VALUES" + value + ";";
                Statement Stmt = con.createStatement();
                Stmt.execute(query);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // public static boolean update_ranker_dictionary(){}
    public static void main(String args[]) {
    }
}
