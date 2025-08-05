package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;

public class SiteProfile {
    public static Map<String, SiteProfile> SiteProfiles = new HashMap<>();

    public String getName() {
        return name;
    }

    private final String name;
    private final String masterSpawnPoint;

    public void setTeamProfile(TeamProfile teamProfile) {
        this.teamProfile = teamProfile;
    }

    public String getMasterSpawnPoint() {
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
    private final Location generatorLocation;

    public SiteProfile(String name, String masterSpawnPoint, Location coreLocation, Location generatorLocation){
        this.name = name;
        this.masterSpawnPoint = masterSpawnPoint;
        this.coreLocation = coreLocation;
        this.generatorLocation = generatorLocation;
        SiteProfiles.put(name, this);
    }

    public SiteProfile(String name){
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
        this(name,masterSpawnPoint,coreLocation,generatorLocation);

    }
}
