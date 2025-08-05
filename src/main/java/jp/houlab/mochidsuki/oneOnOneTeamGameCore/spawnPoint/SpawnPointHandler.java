package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile.TeamProfiles;

public class SpawnPointHandler extends BukkitRunnable {

    private final SpawnPointProfile spawnPointProfile;

    public SpawnPointHandler(SpawnPointProfile spawnPointProfile){
        this.runTaskTimer(plugin, 0, 1);
        this.spawnPointProfile = spawnPointProfile;
    }

    @Override
    public void run() {
        for(Player player : spawnPointProfile.getLocation().getNearbyPlayers(3)){
            TeamProfile teamProfile = null;
            for(TeamProfile tp : TeamProfiles.values()){
                if(tp.getPlayers().contains(player)){
                    teamProfile = tp;
                }
            }
            if(teamProfile != null){
                spawnPointProfile.addScore(1,teamProfile);
            }
        }
    }
}
