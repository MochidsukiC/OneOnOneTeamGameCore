package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;

public class SpawnPointProfile {
    final static Map<String , SpawnPointProfile> spawnPointProfileMap = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
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
    private TextDisplay textDisplay;

    public SpawnPointProfile(String name, Location location, boolean enable){
        this.name = name;
        this.enable = enable;
        this.location = location;
        spawnPointProfileMap.put(name, this);
        spawnPointHandler = new SpawnPointHandler(this);

        this.textDisplay = location.getWorld().spawn(location, TextDisplay.class);
        textDisplay.setBillboard(Display.Billboard.CENTER);
    }
    public SpawnPointProfile(Location location){
        this("SpawnPoint"+spawnPointProfileMap.keySet().size(),location,true);
    }

    public SpawnPointProfile(String name){

        Location location = null;
        try{
            List<Integer> i = config.getIntegerList("SpawnPointLocation."+name);
            location = new Location(Bukkit.getWorld(config.getString("World")),i.get(0),i.get(1),i.get(2));
        }catch(Exception e){
            throw new IllegalArgumentException("SpawnPointLocation."+name+".Core is required");
        }

        this(name,location,true);
    }

    /**
     * スポーンポイントの所有スコアを加算する。
     * 所有者以外が換算しようとすると自動的に減算し、また所有者がNullの場合は自動的に所有者に指定する。
     * @param i 加算値
     * @param teamProfile 加算するチーム
     */
    public void addScore(int i,TeamProfile teamProfile){
        if(teamProfile == null) throw new NullPointerException("teamProfile is null");

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
    public boolean isOwned(TeamProfile teamProfile){
        if(teamProfile == null) return false;
        return teamProfile.equals(owner) && score >= config.getInt("SpawnScoreThreshold");
    }
    public TeamProfile getOwner(){
        return owner;
    }
    public void setOwner(TeamProfile teamProfile){
        owner = teamProfile;
    }

}
