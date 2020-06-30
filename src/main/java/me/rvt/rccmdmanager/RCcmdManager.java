package me.rvt.rccmdmanager;

import com.bekvon.bukkit.residence.Residence;
import me.rvt.rccmdmanager.commands.GetIP;
import me.rvt.rccmdmanager.config.ConfigInit;
import me.rvt.rccmdmanager.email.ReadEmail;
import me.rvt.rccmdmanager.email.WriteEmail;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RCcmdManager extends JavaPlugin implements Listener {
    FileConfiguration config;
    private static Residence res = (Residence) Bukkit.getServer().getPluginManager().getPlugin("Residence");

    private boolean wbGenerate = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("getip").setExecutor(this);
        this.getCommand("email").setExecutor(this);
        config = new ConfigInit(this).getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName()) {
            case "getip":
                if (!(sender instanceof Player) || sender.isOp() ||
                        sender.hasPermission("rccmdmanager.getip")) {
                    Player p = Bukkit.getPlayer(args[0]);

                    if (p != null && p.isOnline()) {
                        new GetIP(Bukkit.getPlayer(args[0]), sender, config);
                    } else {
                        sender.sendMessage(config.getString("message.prefix") +
                                config.getString("message.noplayer"));
                    }
                } else
                    sender.sendMessage(config.getString("message.prefix") +
                            config.getString("message.nopermission"));
                break;
            case "email":
                if (sender instanceof Player) {
                    Player p = (Player) sender;

                    if (args.length > 0) {
                        switch (args[0].toLowerCase()) {
                            case "set":
                                if (args.length > 1 && args[1].contains("@")) {
                                    new WriteEmail(p.getName(), args[1]);

                                    p.sendMessage(config.getString("message.prefix") +
                                            config.getString("message.success"));
                                } else
                                    p.sendMessage(config.getString("message.prefix") +
                                            config.getString("message.invalid"));
                                break;
                            case "delete":
                                new WriteEmail(p.getName(), "NULL");
                                p.sendMessage(config.getString("message.prefix") +
                                        config.getString("message.deleted"));
                                break;
                            default:
                                p.sendMessage(config.getString("message.prefix") +
                                        config.getString("message.invalid"));
                                break;
                        }
                    } else
                        new ReadEmail(p, config);
                }
                break;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerCMD(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage().toLowerCase();
        List < String > defaultWorlds = config.getStringList("default.worlds");
        List < String > skyblockWorlds = config.getStringList("skyblock.worlds");

        if (!defaultWorlds.contains(p.getWorld().getName())) {
            List < String > defaultCmds = config.getStringList("default.cmds");

            for (String s: defaultCmds) {
                if (msg.contains(s)) {
                    cancelCommand(p, config.getString("message.forbidden"), e);
                    return;
                }
            }
        }

        if (!skyblockWorlds.contains(p.getWorld().getName())) {
            List < String > skyblockCmds = config.getStringList("skyblock.cmds");

            for (String s: skyblockCmds) {
                if (msg.contains(s)) {
                    cancelCommand(p, config.getString("message.forbidden"), e);
                    return;
                }
            }
        }

        if (msg.equals("/back")) {
            if (res.getResidenceManager().getByLoc(p) != null) {
                if (res.getResidenceManager().getByLoc(p).getResidenceName().equals(
                        config.getString("res.mobarena"))) {
                    cancelCommand(p, config.getString("message.noback"), e);
                }
            }
        }
    }

    private void cancelCommand(Player p, String msg, PlayerCommandPreprocessEvent e) {
        if (!p.isOp()) {
            p.sendMessage(config.getString("message.prefix") + msg);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        if (config.getBoolean("autogen.enabled")) {
            if (Bukkit.getOnlinePlayers().size() < 2) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("autogen.start"));

                getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCcmdManager.class), () ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("autogen.confirm")),
                        config.getInt("autogen.delay"));

                wbGenerate = true;
            }
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (config.getBoolean("autogen.enabled"))
            if (wbGenerate || Bukkit.getOnlinePlayers().size() < 3) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("autogen.cancel"));
                wbGenerate = false;
            }
    }

    @EventHandler
    private void noDragonsAndWithers(EntitySpawnEvent e){
        switch(e.getEntity().getType()){
            case ENDER_DRAGON:
                if(config.getBoolean("disabled-mobs.ender-dragon"))
                    e.setCancelled(true);
                break;
            case WITHER:
                if(config.getBoolean("disabled-mobs.wither"))
                    e.setCancelled(true);
                break;
        }
    }
}