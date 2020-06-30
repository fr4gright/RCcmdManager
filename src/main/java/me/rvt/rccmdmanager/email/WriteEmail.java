package me.rvt.rccmdmanager.email;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class WriteEmail extends Thread{

    private String userName;
    private String email;

    public WriteEmail(String userName, String email) {
        this.userName = userName;
        this.email = email;

        this.start();
    }

    public void run(){
        Statement stmt = null;
        Connection c = null;

        try{
            c = DriverManager.getConnection("jdbc:sqlite:data/Data.db");

            stmt = c.createStatement();

            stmt.executeUpdate(
                    String.format("UPDATE Players SET email = '%s' WHERE username = '%s'", email, userName)
            );
        }
        catch (Exception e){
            System.out.println("[RCemailSystem] " + e.getMessage());
        }
        finally {
            try{
                if(stmt != null && !stmt.isClosed())
                    stmt.close();
                if(c != null && !c.isClosed())
                    c.close();
            }
            catch (Exception ignored) {}
        }
    }
}