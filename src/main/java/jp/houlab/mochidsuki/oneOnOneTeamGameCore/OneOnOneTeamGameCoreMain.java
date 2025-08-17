package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnCommandListener;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import jp.houlab.mochidsuki.skillItemManager.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile.SpawnPointProfileMap;
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

        getCommand("rsp").setExecutor(new SpawnCommandListener());
        getCommand("3otgc").setExecutor(new CommandListener());

        //Initialization
        for(String s : config.getConfigurationSection("SpawnPointLocation").getKeys(false)) {
            new SpawnPointProfile(s);
        }

        for(String s : config.getConfigurationSection("Site").getKeys(false)) {
            new SiteProfile(s);
        }

        for(String s : config.getConfigurationSection("Team").getKeys(false)) {
            new TeamProfile(s);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Iterator<SpawnPointProfile> iterator = SpawnPointProfileMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }


    //Utils

    static public void resetPlayerInventory(Player p, @Nullable List<Material> materialBlackList, int random){
        out:
        for(ItemStack item : p.getInventory().getContents()){
            if(item != null && (materialBlackList == null || !materialBlackList.contains(item.getType()))){
                if(plugin.getServer().getPluginManager().isPluginEnabled("SkillItemManager")){
                    for(String name : Main.config.getConfigurationSection("Items").getKeys(false)) {
                        Material material = Material.matchMaterial(name);
                        if(item.getType().equals(material)) continue out;
                    }
                }
                int r = new Random().nextInt(100);
                if(r<random) {
                    item.setAmount(0);
                }
            }
        }

    }
}
