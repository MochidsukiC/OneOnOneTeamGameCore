package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.SiteProfile.SiteProfiles;

public class TeamProfile {
    public static Map<String,TeamProfile> TeamProfiles = new HashMap<>();

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public Set<Player> getPlayers(){
        Set<Player> players = new HashSet<>();
        for(String n : team.getEntries()){
            Player p = Bukkit.getPlayer(n);
            if(p != null){
                players.add(p);
            }
        }
        return players;
    }

    public SiteProfile getSiteProfile() {
        return siteProfile;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    private final String name;
    private final Team team;

    public void setSiteProfile(SiteProfile siteProfile) {
        this.siteProfile = siteProfile;
    }

    private SiteProfile siteProfile;
    private final BossBar bossBar;

    public TeamProfile(@NonNull String name, @NonNull Team team, @NonNull BossBar bossBar, @NonNull SiteProfile siteProfile) {
        this.name = name;
        this.team = team;
        this.siteProfile = siteProfile;
        this.bossBar = bossBar;
    }

    public TeamProfile(@NonNull String name, @NonNull Team team, @NonNull BarColor barColor, @NonNull SiteProfile siteProfile) {
        BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(plugin,name));
        if(bossBar == null){
            bossBar = Bukkit.createBossBar(new NamespacedKey(plugin,name),name,barColor,BarStyle.SEGMENTED_12);
        }

        this(name,team,bossBar,siteProfile);
    }

    public TeamProfile(@NonNull String name){
        String sn = config.getString("Team."+name+".DefaultSiteID");
        if(sn == null) throw new IllegalArgumentException("Team."+name+".DefaultSiteID is required");

        SiteProfile siteProfile = SiteProfiles.get(sn);
        if(siteProfile == null){
            siteProfile = new SiteProfile(sn);
        }

        String teamName = config.getString("Team."+name+".Team.ID");
        if(teamName == null) throw new IllegalArgumentException("Team."+name+".Team.ID is required");
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        if(team == null){
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);
            String c = config.getString("Team."+name+".Team.Color");
            if(c == null) throw new IllegalArgumentException("Team."+name+".Team.Color is required");
            team.color(NamedTextColor.NAMES.value(c));
        }

        String bc =  config.getString("Team."+name+".BossBarColor");
        if(bc == null) throw new IllegalArgumentException("Team."+name+".BossBarColor is required");

        this(name,team,BarColor.valueOf(bc),siteProfile);

    }

    public void sync(){
        bossBar.removeAll();
        for(String n : team.getEntries()){
            Player player = Bukkit.getPlayer(n);
            if(player != null){
                bossBar.addPlayer(player);
            }
        }
    }

}
