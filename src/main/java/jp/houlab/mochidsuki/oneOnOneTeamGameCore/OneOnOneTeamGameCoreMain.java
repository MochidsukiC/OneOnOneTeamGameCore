package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointUtils.loadAdjacencyListFromConfig;

public final class OneOnOneTeamGameCoreMain extends JavaPlugin {

    public static Plugin plugin;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        saveDefaultConfig();
        config = getConfig();

        loadAdjacencyListFromConfig();

        getServer().getPluginManager().registerEvents(new Listener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
