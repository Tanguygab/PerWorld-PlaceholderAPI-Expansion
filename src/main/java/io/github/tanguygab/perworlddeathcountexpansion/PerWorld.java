package io.github.tanguygab.perworlddeathcountexpansion;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Taskable;
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

public final class PerWorld extends PlaceholderExpansion implements Listener, Taskable {

    public File file;
    public FileConfiguration config;
    private static final List<String> placeholders = Arrays.asList(
            "%perworld_kills_current%",
            "%perworld_kills_in_<world name>%",
            "%perworld_deaths_current%",
            "%perworld_deaths_in_<world name>%"
    );

    @Override
    public String getIdentifier() {
        return "perworld";
    }

    @Override
    public String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public List<String> getPlaceholders() {
        return placeholders;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String type = params.split("_")[0];

        if (params.equals(type+"_current") && player.getPlayer() != null)
            return getValue(player,type,player.getPlayer().getWorld().getName());
        if (params.startsWith(type+"_in_"))
            return getValue(player,type,params.replace(type+"_in_",""));

        return null;
    }

    public String getValue(OfflinePlayer player, String type, String world) {
        return config.getInt(player.getUniqueId()+"."+type+"."+world,0)+"";
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        String w = p.getWorld().getName();
        String uuid = p.getUniqueId().toString();
        config.set(uuid+".deaths."+w,config.getInt(uuid+".deaths."+w,0)+1);
        if (p.getKiller() != null) {
            uuid = p.getKiller().getUniqueId().toString();
            config.set(uuid + ".kills." + w, config.getInt(uuid + ".kills." + w, 0) + 1);
        }
    }

    @Override
    public void start() {
        file = new File(PlaceholderAPIPlugin.getInstance().getDataFolder(), "perworld-data.yml");
        if (!file.exists()) {
            try {file.createNewFile();}
            catch (IOException e) {e.printStackTrace();}
        }
        config = new YamlConfiguration();
        try {config.load(file);}
        catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void stop() {
        try {config.save(file);}
        catch (IOException e) {e.printStackTrace();}
    }
}
