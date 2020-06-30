package me.rvt.rccmdmanager.email;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReadEmail extends Thread{
    private final Player p;
    FileConfiguration config;

    public ReadEmail(Player p, FileConfiguration config) {
        this.p = p;
        this.config = config;

        this.start();
    }

    public void run(){
        ResultSet rSet = null;
        Statement stmt = null;
        Connection c = null;

        try{
            String email;
            c = DriverManager.getConnection("jdbc:sqlite:data/Data.db");

            stmt = c.createStatement();

            rSet = stmt.executeQuery(
                    String.format("SELECT email FROM Players WHERE username = '%s'", p.getName())
            );

            email = rSet.getString("email");

            if(email.contains("@"))
                p.sendMessage(config.getString("message.prefix") +
                        config.getString("message.emailfound") + rSet.getString("email"));
            else
                p.sendMessage(config.getString("message.prefix") +
                        config.getString("message.emailnotfound"));
        }
        catch (Exception e){
            System.out.println("[RCemailSystem] " + e.getMessage());
        }
        finally {
            try{
                if(rSet != null && !rSet.isClosed())
                    rSet.close();
                if(stmt != null && !stmt.isClosed())
                    stmt.close();
                if(c != null && !c.isClosed())
                    c.close();
            }
            catch (Exception ignored) {}
        }
    }
}