package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
            Player p = plugin.getServer().getPlayer(n);
            if(p != null){
                players.add(p);
            }
        }
        return players;
    }

    public Set<OfflinePlayer> getOnlineOfflinePlayers(){
        Set<OfflinePlayer> players = new HashSet<>();
        for(String n : team.getEntries()){
            OfflinePlayer p = Bukkit.getOfflinePlayer(n);
            if(p.isOnline()){
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
        this.siteProfile.setTeamProfile(this);
    }

    private SiteProfile siteProfile;
    private BossBar bossBar;

    public int getWinPoint() {
        return winPoint;
    }
    public int addWinPoint(int i){
        return winPoint+=i;
    }

    private int winPoint;

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    private boolean live;


    public TeamProfile(@NonNull String name, @NonNull Team team) {
        this.name = name;
        this.team = team;
        this.bossBar = null;
        TeamProfiles.put(name, this);
    }

    public TeamProfile(@NonNull String name, @NonNull Team team, @NonNull SiteProfile siteProfile) {
        this(name,team);
        setSiteProfile(siteProfile);
    }

    public TeamProfile(@NonNull String name, @NonNull Team team, @NonNull BossBar bossBar, @NonNull SiteProfile siteProfile) {
        this(name,team,siteProfile);
        this.bossBar = bossBar;
    }

    public TeamProfile(@NonNull String name, @NonNull Team team, @NonNull BarColor barColor, @NonNull SiteProfile siteProfile) {
        this(name,team,siteProfile);
        BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(plugin,name));
        if(bossBar == null){
            bossBar = Bukkit.createBossBar(new NamespacedKey(plugin,name),name,barColor,BarStyle.SEGMENTED_10);
        }
        this.bossBar = bossBar;
    }

    public TeamProfile(@NonNull String name){
        this.name = name;

        String sn = config.getString("Team."+name+".DefaultSiteID");
        if(sn == null) throw new IllegalArgumentException("Team."+name+".DefaultSiteID is required");

        SiteProfile siteProfile = SiteProfiles.get(sn);
        setSiteProfile(Objects.requireNonNullElseGet(siteProfile, () -> new SiteProfile(sn)));

        String teamName = config.getString("Team."+name+".Team.ID");
        if(teamName == null) throw new IllegalArgumentException("Team."+name+".Team.ID is required");
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        if(team == null){
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);
            String c = config.getString("Team."+name+".Team.Color");
            if(c == null) throw new IllegalArgumentException("Team."+name+".Team.Color is required");
            team.color(NamedTextColor.NAMES.value(c));
            team.setAllowFriendlyFire(false);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        }

        String bc =  config.getString("Team."+name+".BossBarColor");
        if(bc == null) throw new IllegalArgumentException("Team."+name+".BossBarColor is required");
        BarColor barColor = BarColor.valueOf(bc);

        BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(plugin,name));
        if(bossBar == null){
            bossBar = Bukkit.createBossBar(new NamespacedKey(plugin,name),name,barColor,BarStyle.SEGMENTED_10);
        }
        this.team = team;
        this.bossBar = bossBar;
        this.siteProfile = siteProfile;
        TeamProfiles.put(name, this);
    }

    public void updateBossBar(){
        bossBar.setTitle(getName()+"チーム残りコアHP : " + getSiteProfile().getCoreHealth()*100/config.getInt("Core.Health")+"%");
        bossBar.setProgress((double) getSiteProfile().getCoreHealth()/config.getInt("Core.Health"));
    }

    public void sync(){
        bossBar.removeAll();
        for(Player player : Bukkit.getOnlinePlayers()){
            bossBar.addPlayer(player);
        }
    }

    @Nullable
    public static TeamProfile getTeamProfileFromPlayer(OfflinePlayer offlinePlayer){
         for(TeamProfile tp : TeamProfiles.values()){
            if(tp.getTeam().hasPlayer(offlinePlayer)) return tp;
        }

        return null;
    }

}
