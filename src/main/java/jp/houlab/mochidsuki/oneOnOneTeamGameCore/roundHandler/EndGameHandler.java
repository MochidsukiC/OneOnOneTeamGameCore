package jp.houlab.mochidsuki.oneOnOneTeamGameCore.roundHandler;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.events.EndRoundEvent;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class EndGameHandler {
    public EndGameHandler() {
        plugin.getServer().getScheduler().runTaskLater(plugin ,()-> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setGameMode(GameMode.ADVENTURE);
                OneOnOneTeamGameCoreMain.resetPlayerInventory(player, null, 100);
                if (config.getString("World") == null || plugin.getServer().getWorld(config.getString("World")) == null)
                    throw new IllegalArgumentException("Config Error : World");
                player.teleport(plugin.getServer().getWorld(config.getString("World")).getSpawnLocation());
            }

            for(SpawnPointProfile spawnPointProfile : SpawnPointProfile.SpawnPointProfileMap.values()){
                spawnPointProfile.setEnable(false);
            }

        },200);
    }
}
