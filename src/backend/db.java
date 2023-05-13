package backend;
import java.sql.*;
import java.util.ArrayList;
public class db { 
    private static String Connection_String = "jdbc:sqlserver://Dc-OZER;databaseName=search_engine_db;integratedSecurity=true;encrypt=false;";
    private static String user = "sa";
    private static String pswd = "search_engine_db_S23";
    private static Connection con = null;
    // Connections 
    public static void connect(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try {
                con = DriverManager.getConnection(Connection_String,user,pswd);
                System.out.println("Connected");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch(ClassNotFoundException cex){
            System.out.println(cex);
        }
    }
    public static void disconnect(){
        // close the db connection 
        try {
        con.close();
        System.out.println("Disconnected");
        } catch (Exception e) {
            System.out.println("Failed to disconnect from database");
        }
    }
    //URLS
    public static boolean add_url(String url){
        // add url to the database
        try {
            String query = "INSERT INTO Docs VALUES (\'" + url + "\');";
            Statement Stmt = con.createStatement();
            boolean isResult = Stmt.execute(query);
            return isResult;
        } catch (Exception e) {
            System.out.println("Failed to add url to the database");
            return false;
        }
    }
    public static boolean IsVisited(String url){
        // get the visited urls from the database
        try {
            //con = DriverManager.getConnection(pswd, user, Connection_String);
            String query = "SELECT * from Docs where link = \' " + url +  "\';";
            Statement Stmt = con.createStatement();
            boolean rs = Stmt.execute(query);
            if(rs){
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    // Indexings 
    // public static boolean add_to_ranker_dictionary(word,stemmed,pos,tag)
    // public static boolean update_ranker_dictionary()
    public static void main(String args[]){
    }
}
