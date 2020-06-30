package me.rvt.rccmdmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.net.*;
import java.io.*;

public class GetIP extends Thread{
    Player p;
    CommandSender sender;
    FileConfiguration config;

    public GetIP(Player p, CommandSender sender, FileConfiguration config)
    {
        this.p = p;
        this.sender = sender;
        this.config = config;

        this.start();
    }

    public void run(){
        try{
            String fileName = p.getName() + "-" + p.getAddress().getPort();
            Socket socket = new Socket(
                    config.getString("getip.ip"),
                    config.getInt("getip.port")
            );
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(fileName);
            out.flush();
            out.close();

            socket.close();

            sender.sendMessage(config.getString("message.prefix") +
                    String.format(config.getString("message.gotip"), fileName));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

