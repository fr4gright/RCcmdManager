package me.rvt.rccmdmanager.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigInit {
    private File conf;
    private FileConfiguration config;

    public ConfigInit(Plugin plugin)
    {
        loadConfig(plugin);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration loadConfig(Plugin plugin) {

        if (conf == null) {
            conf = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(conf);

        if (!config.contains("default") || !config.contains("skyblock")) {

            init();

            try {
                config.save(conf);
            } catch (IOException var3) {
                System.out.println("[RCcmdManger] Unable to save config!");
            }
        }
        return config;
    }

    private void init() {
        List<String> defaultCmds = new ArrayList<>();
        List<String> defaultWorlds = new ArrayList<>();

        List<String> skyblockCmds = new ArrayList<>();
        List<String> skyblockWorlds = new ArrayList<>();

        defaultCmds.add("/sell");
        defaultCmds.add("/ah");
        defaultCmds.add("/auction");
        defaultWorlds.add("world");
        defaultWorlds.add("world_nether");
        defaultWorlds.add("world_the_end");

        skyblockCmds.add("/is shop");
        skyblockCmds.add("/island shop");
        skyblockWorlds.add("IridiumSkyblock");
        skyblockWorlds.add("IridiumSkyblock_nether");

        config.set("default.cmds", defaultCmds);
        config.set("default.worlds", defaultWorlds);
        config.set("skyblock.cmds", skyblockCmds);
        config.set("skyblock.worlds", skyblockWorlds);
        config.set("message.prefix",  ChatColor.translateAlternateColorCodes('&',
                "&f&l[&b&lRC&f&l] &r"));
        config.set("message.forbidden", ChatColor.translateAlternateColorCodes('&',
                "&cYou can't use that command right now!"));
        config.set("message.noback", ChatColor.translateAlternateColorCodes('&',
                "&cBacking to the &aMobArena &cis not allowed!"));
        config.set("message.gotip", ChatColor.translateAlternateColorCodes('&',
                "File \"§2%s.txt§r\" were sent to the proxy."));
        config.set("message.noplayer", ChatColor.translateAlternateColorCodes('&',
                "&cPlayer not found!"));
        config.set("message.noplayer", ChatColor.translateAlternateColorCodes('&',
                "&cYou don't have the required permissions!"));
        config.set("message.invalid", ChatColor.RED + "Invalid E-Mail address!");
        config.set("message.success", ChatColor.GREEN + "Your E-Mail was set successfully!");
        config.set("message.reloaded", ChatColor.GREEN + "Config reloaded!");
        config.set("message.emailfound", ChatColor.GREEN + "Your E-Mail is: " + ChatColor.BLUE);
        config.set("message.emailnotfound", ChatColor.RED + "E-Mail address not found...");
        config.set("message.deleted", ChatColor.GREEN + "Your E-Mail was deleted.");

        config.set("res.mobarena", "MobArena");

        config.set("autogen.enabled", false);
        config.set("autogen.start", "wb world fill 20 20 false");
        config.set("autogen.confirm", "wb fill confirm");
        config.set("autogen.delay", 20);
        config.set("autogen.cancel", "wb fill cancel");
        config.set("getip.ip", "192.168.0.100");
        config.set("getip.port", 18182);

        config.set("disabled-mobs.ender-dragon", true);
        config.set("disabled-mobs.wither", true);
    }
}