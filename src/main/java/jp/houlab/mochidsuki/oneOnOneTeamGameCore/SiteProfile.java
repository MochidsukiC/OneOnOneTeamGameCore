package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile.SpawnPointProfileMap;

public class SiteProfile {
    public static Map<String, SiteProfile> SiteProfiles = new HashMap<>();

    public String getName() {
        return name;
    }

    private final String name;

    public void setMasterSpawnPoint(SpawnPointProfile masterSpawnPoint) {
        if(this.masterSpawnPoint != null) {
            this.masterSpawnPoint.setMasterSpawnPoint(false,null);
        }
        masterSpawnPoint.setMasterSpawnPoint(true,teamProfile);
        this.masterSpawnPoint = masterSpawnPoint;
    }

    private SpawnPointProfile masterSpawnPoint;

    public void setTeamProfile(TeamProfile teamProfile) {
        this.teamProfile = teamProfile;
    }

    public SpawnPointProfile getMasterSpawnPoint() {
        return masterSpawnPoint;
    }

    public Location getCoreLocation() {
        return coreLocation;
    }

    public Location getGeneratorLocation() {
        return generatorLocation;
    }

    public TeamProfile getTeamProfile() {
        return teamProfile;
    }

    private TeamProfile teamProfile;
    private final Location coreLocation;

    public int getCoreHealth() {
        return coreHealth;
    }

    private int coreHealth;
    private final Location generatorLocation;

    public SiteProfile(String name, String masterSpawnPoint, Location coreLocation, Location generatorLocation){
        this.name = name;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if(SpawnPointProfileMap.containsKey(masterSpawnPoint)) {
                setMasterSpawnPoint(SpawnPointProfileMap.get(masterSpawnPoint));
            }
        },1);
        this.coreLocation = coreLocation;
        this.generatorLocation = generatorLocation;
        coreHealth = config.getInt("Core.Health");
        SiteProfiles.put(name, this);
    }

    public SiteProfile(String name){
        this.name = name;

        if(config.getConfigurationSection("Site."+name)==null) throw new IllegalArgumentException("The"+name+" site is not set in the configuration file.");

        String masterSpawnPoint = config.getString("Site."+name+".MasterSpawnPoint");
        if(masterSpawnPoint == null) throw new IllegalArgumentException("Site."+name+".MasterSpawnPoint is required");

        Location coreLocation = null;
        try{
            List<Integer> i = config.getIntegerList("Site."+name+".Core");
            coreLocation = new Location(Bukkit.getWorld(config.getString("World")),i.get(0),i.get(1),i.get(2));
        }catch(Exception e){
            throw new IllegalArgumentException("Site."+name+".Core is required");
        }

        Location generatorLocation = null;
        try{
            List<Integer> i = config.getIntegerList("Site."+name+".Generator");
            generatorLocation = new Location(Bukkit.getWorld(config.getString("World")),i.get(0),i.get(1),i.get(2));
        }catch(Exception e){
            throw new IllegalArgumentException("Site."+name+".Generator is required");
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if(SpawnPointProfileMap.containsKey(masterSpawnPoint)) {
                setMasterSpawnPoint(SpawnPointProfileMap.get(masterSpawnPoint));
            }
        },1);
        this.coreLocation = coreLocation;
        this.generatorLocation =generatorLocation;
        coreHealth = config.getInt("Core.Health");
        SiteProfiles.put(name, this);
    }

    public void subtractCoreHealth(int subCoreHealth) {
        this.coreHealth -= subCoreHealth;
    }

    public void reset(){
        coreHealth = config.getInt("Core.Health");
        teamProfile.updateBossBar();
    }
}
