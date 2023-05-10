package backend;

import java.sql.*;

public class db {

    private static String Connection_String = "jdbc:sqlserver://Dc-OZER;databaseName=search_engine_db;integratedSecurity=true;encrypt=false;";
    private static String user = "sa";
    private static String pswd = "search_engine_db_S23";
    public static void connect(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try {
                DriverManager.getConnection(Connection_String,user,pswd);
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
        Connection con = DriverManager.getConnection(Connection_String,user,pswd);
        con.close();
        System.out.println("Disconnected");
        } catch (Exception e) {
            System.out.println("Failed to disconnect from database");
        }
    }
    public static void main(String args[]){
        connect();
        disconnect();
    }
}
