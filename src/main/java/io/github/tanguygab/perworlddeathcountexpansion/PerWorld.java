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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

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
            "%perworld_deaths_in_<world name>%",
            "%perworld_blocks-broken.<block>_current%",
            "%perworld_blocks-broken.<block>_in_<world name>%"
    );

    @Override
    public @NotNull String getIdentifier() {
        return "perworld";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.1";
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return placeholders;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (params.endsWith("_current") && player.getPlayer() != null) {
            String type = params.substring(0, params.length()-8);

            return getValue(player, type, player.getPlayer().getWorld().getName());
        }
        if (params.contains("_in_")) {
            String[] args = params.split("_in_");
            if (args.length < 2) return null;

            return getValue(player, args[0], args[1]);
        }
        return null;
    }

    public String getValue(OfflinePlayer player, String type, String world) {
        return getInt(player, type, world)+"";
    }

    public int getInt(OfflinePlayer player, String type, String world) {
        return config.getInt(player.getUniqueId()+"."+type+"."+world,0);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        String w = p.getWorld().getName();
        String uuid = p.getUniqueId().toString();
        config.set(uuid+".deaths."+w, getInt(p, "deaths", w) + 1);

        if (p.getKiller() != null) {
            uuid = p.getKiller().getUniqueId().toString();
            config.set(uuid + ".kills." + w, getInt(p, "kills", w) + 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        String w = p.getWorld().getName();
        String block = e.getBlock().getType().toString();
        String uuid = p.getUniqueId().toString();
        config.set(uuid+".blocks-broken."+block+"."+w, getInt(p, "blocks-broken."+block, w) + 1);
    }

    @Override
    public void start() {
        file = new File(PlaceholderAPIPlugin.getInstance().getDataFolder(), "perworld-data.yml");
        if (!file.exists()) {
            try { //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {throw new RuntimeException(e);}
        }
        config = new YamlConfiguration();
        try {config.load(file);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    @Override
    public void stop() {
        try {config.save(file);}
        catch (IOException e) {throw new RuntimeException(e);}
    }
}
