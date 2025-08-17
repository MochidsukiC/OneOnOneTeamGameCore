package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class SpawnPointProfile {
    public final static Map<String , SpawnPointProfile> SpawnPointProfileMap = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        if(isMasterSpawnPoint()) return true;
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Location getLocation() {
        return location;
    }

    private String name;
    private boolean enable;
    private final Location location;

    public SpawnPointHandler getSpawnPointHandler() {
        return spawnPointHandler;
    }

    public int getScore() {
        return score;
    }

    private final SpawnPointHandler spawnPointHandler;
    private int score = 0;
    private TeamProfile owner;

    public TextDisplay getTextDisplay() {
        return textDisplay;
    }

    private final TextDisplay textDisplay;

    public boolean isMasterSpawnPoint() {
        return isMasterSpawnPoint;
    }

    public void setMasterSpawnPoint(boolean masterSpawnPoint,TeamProfile teamProfile) {
        isMasterSpawnPoint = masterSpawnPoint;
        owner = teamProfile;
    }

    private boolean isMasterSpawnPoint = false;


    public SpawnPointProfile(String name, Location location, boolean enable){
        this.name = name;
        this.enable = enable;
        this.location = location;

        SpawnPointProfileMap.put(name, this);

        spawnPointHandler = new SpawnPointHandler(this);

        for(Entity e : location.clone().add(0.5,2,0.5).getNearbyEntitiesByType(TextDisplay.class,4)){
            e.getLocation().getChunk().load();
            if(e.getScoreboardTags().contains("SpawnPointBar"))e.remove();
        }
        this.textDisplay = location.clone().add(0.5,2,0.5).getWorld().spawn(location.clone().add(0.5,2,0.5), TextDisplay.class);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.addScoreboardTag("SpawnPointBar");
    }
    public SpawnPointProfile(Location location){
        this("SpawnPoint"+ SpawnPointProfileMap.keySet().size(),location,true);
    }

    public SpawnPointProfile(String name){
        this.name = name;
        Location location = null;
        try{
            List<Integer> i = config.getIntegerList("SpawnPointLocation."+name);
            location = new Location(Bukkit.getWorld(config.getString("World")),i.get(0),i.get(1),i.get(2));
        }catch(Exception e){
            throw new IllegalArgumentException("SpawnPointLocation."+name+".Core is required");
        }
        this.location = location;
        this.enable = false;


        SpawnPointProfileMap.put(name, this);

        spawnPointHandler = new SpawnPointHandler(this);

        for(Entity e : location.clone().add(0.5,2,0.5).getNearbyEntitiesByType(TextDisplay.class,4)){
            e.getLocation().getChunk().load();
            if(e.getScoreboardTags().contains("SpawnPointBar"))e.remove();
        }
        this.textDisplay = location.clone().add(0.5,2,0.5).getWorld().spawn(location.clone().add(0.5,2,0.5), TextDisplay.class);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.addScoreboardTag("SpawnPointBar");
    }


    /**
     * スポーンポイントの所有スコアを加算する。
     * 所有者以外が換算しようとすると自動的に減算し、また所有者がNullの場合は自動的に所有者に指定する。
     * @param i 加算値
     * @param teamProfile 加算するチーム
     */
    public void addScore(int i,TeamProfile teamProfile){
        if(teamProfile == null) throw new NullPointerException("teamProfile is null");
        if(isMasterSpawnPoint() || !isEnable()) return;

        if(owner == null){
            owner = teamProfile;
        }
        if(owner.equals(teamProfile)){
            if(score<config.getInt("SpawnScoreMax")) {
                score += i;
            }
            if(score>config.getInt("SpawnScoreMax")) {
                score = config.getInt("SpawnScoreMax");
            }
        }else {
            score -= i;
            if(score<0){
                owner = teamProfile;
            }
        }
    }
    public boolean isOwned(@Nullable TeamProfile teamProfile){
        if(teamProfile == null || !isEnable()) return false;
        return teamProfile.equals(owner) && (isMasterSpawnPoint() || score >= config.getInt("SpawnScoreThreshold"));
    }
    public @Nullable TeamProfile getOwner(){
        return owner;
    }
    public void setOwner(TeamProfile teamProfile){
        owner = teamProfile;
    }

    public void reset(){
        if(isMasterSpawnPoint()) return;
        owner = null;
        score = 0;
        textDisplay.text(Component.text(""));
    }

    public void remove(){
        textDisplay.remove();
        enable = false;
        SpawnPointProfileMap.remove(name);
    }

}

