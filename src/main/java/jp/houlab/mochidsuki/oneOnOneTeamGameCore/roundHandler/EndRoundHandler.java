package jp.houlab.mochidsuki.oneOnOneTeamGameCore.roundHandler;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.SiteProfile;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.events.EndRoundEvent;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class EndRoundHandler extends BukkitRunnable implements Listener {

    private final List<TeamProfile> teamProfiles;
    private final List<SiteProfile> siteProfiles;

    public EndRoundHandler(TeamProfile winTeamProfile) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        winTeamProfile.addWinPoint(1);

        Component scoreComponent = Component.text("");
        teamProfiles = new ArrayList<>(TeamProfile.TeamProfiles.values());
        siteProfiles = new ArrayList<>();

        for(int i = 0; i < teamProfiles.size(); i++) {
            TeamProfile teamProfile = teamProfiles.get(i);
            siteProfiles.add(teamProfile.getSiteProfile());

            scoreComponent = scoreComponent.append(Component.text(teamProfile.getWinPoint()).color(teamProfile.getTeam().color()));
            if(i != teamProfiles.size()-1){
                scoreComponent = scoreComponent.append(Component.text("vs"));
            }
        }

        if(winTeamProfile.getWinPoint() < 2) {

            for (TeamProfile teamProfile : teamProfiles) {
                if (teamProfile.equals(winTeamProfile)) {
                    for (Player player : teamProfile.getPlayers()) {
                        Title title = Title.title(Component.text("ラウンド勝利"), scoreComponent);
                        player.showTitle(title);
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                    }
                } else {
                    for (Player player : teamProfile.getPlayers()) {
                        Title title = Title.title(Component.text("ラウンド敗北"), scoreComponent);
                        player.showTitle(title);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);
                    }
                }
            }
            this.runTaskLater(plugin,200);
        }else {
            for (TeamProfile teamProfile : teamProfiles) {
                if (teamProfile.equals(winTeamProfile)) {
                    for (Player player : teamProfile.getPlayers()) {
                        Title title = Title.title(Component.text("試合勝利!!"), scoreComponent);
                        player.showTitle(title);
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                    }
                } else {
                    for (Player player : teamProfile.getPlayers()) {
                        Title title = Title.title(Component.text("試合敗北..."), scoreComponent);
                        player.showTitle(title);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);
                    }
                }
            }
            new EndGameHandler();
        }

        EndRoundEvent event = new EndRoundEvent(winTeamProfile);
        Bukkit.getPluginManager().callEvent(event);

    }


    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }


    @Override
    public void run(){
        for(int i = 0; i < teamProfiles.size(); i++) {
            TeamProfile teamProfile = teamProfiles.get(i);
            SiteProfile siteProfile;
            if(i==0) {
                siteProfile = siteProfiles.getLast();
            }else {
                siteProfile = siteProfiles.get(i - 1);
            }

            teamProfile.setSiteProfile(siteProfile);
        }

        HandlerList.unregisterAll(this);

        cancel();
        new PrepareRoundHandler();
    }
}
