package backend;
import java.sql.*;
import java.util.ArrayList;

public class db { 
    private static String Connection_String = "jdbc:sqlserver://Dc-OZER;databaseName=search_engine_db;integratedSecurity=true;encrypt=false;";
    private static String user = "sa";
    private static String pswd = "search_engine_db_S23";
    private static Connection con = null;
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
    public static ArrayList<String> getVisited(){
        // get the visited urls from the database
        try {
            //con = DriverManager.getConnection(pswd, user, Connection_String);
            String query = "SELECT * from Docs ORDER BY (doc_id); ";
            Statement Stmt = con.createStatement();
            ResultSet rs = Stmt.executeQuery(query);
            ArrayList<String> visited = new ArrayList<String>();
            while(rs.next()){
                visited.add(rs.getString("link"));
            }
            return visited;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    public static void main(String args[]){
    }
}
