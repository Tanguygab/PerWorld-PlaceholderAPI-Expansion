package io.github.tanguygab.perworlddeathcountexpansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Taskable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class PerWorldDeathCountExpansion extends PlaceholderExpansion implements Listener, Taskable {

    public File file;
    public FileConfiguration config;

    @Override
    public String getIdentifier() {
        return "perworlddeathcount";
    }

    @Override
    public String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public List<String> getPlaceholders() {
        return Arrays.asList("%perworlddeathcount_current%","%perworlddeathcount_in_<world name>%");
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        String uuid = player.getUniqueId().toString();

        if (params.equals("current") && player.getPlayer() != null) {
            String world = player.getPlayer().getWorld().getName();
            return config.getInt(uuid+"."+world,0)+"";
        }
        if (params.startsWith("in_")) {
            String world = params.replace("in_","");
            return config.getInt(uuid+"."+world,0)+"";
        }

        return null;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        String w = p.getWorld().getName();
        String uuid = p.getUniqueId().toString();

        config.set(uuid+"."+w,config.getInt(uuid+"."+w,0)+1);
    }

    @Override
    public void start() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI").getDataFolder(), "perworlddeathcounts-data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
