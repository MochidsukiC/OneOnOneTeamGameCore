package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
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
        if(!spawnPointProfile.isEnable()) return;

        for(Player player : spawnPointProfile.getLocation().getNearbyPlayers(2)){
            if(player.isDead()) continue;
            if(player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) continue;

            TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(player);


            if(teamProfile != null){
                updateSpawnPoint(teamProfile);
            }
        }
        Collection<ItemDisplay> itemDisplays = spawnPointProfile.getLocation().getNearbyEntitiesByType(ItemDisplay.class,2);
        for(ItemDisplay itemDisplay : itemDisplays){

            if(itemDisplay.isDead()) continue;

            for(String name: itemDisplay.getScoreboardTags()){
                String[] tags = name.split(":");
                if(tags.length == 2){
                    if(tags[0].equalsIgnoreCase("DummyPlayer")){
                        OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(tags[1]));
                        TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(player);
                        if(teamProfile != null){
                            updateSpawnPoint(teamProfile);
                        }
                    }
                }

            }
        }
    }


    private void updateSpawnPoint(TeamProfile teamProfile){

        spawnPointProfile.addScore(1,teamProfile);

        TextDisplay textDisplay = spawnPointProfile.getTextDisplay();

        if(spawnPointProfile.isMasterSpawnPoint() || !spawnPointProfile.isEnable()){
            textDisplay.text(Component.text(""));
            return;
        }

        TextColor color = NamedTextColor.WHITE;
        if(spawnPointProfile.getOwner() != null && spawnPointProfile.isOwned(spawnPointProfile.getOwner())){
            color = spawnPointProfile.getOwner().getTeam().color();
        }

        Component component = Component.text("[").color(color);
        int score = spawnPointProfile.getScore();
        for(int i = 0; i < score/10; i++){
            Component c = Component.text("■");
            if(spawnPointProfile.getOwner() != null){
                c = c.color(spawnPointProfile.getOwner().getTeam().color());
            }
            component = component.append(c);
        }

        for(int i = 0; i < (config.getInt("SpawnScoreMax") - score-1)/10; i++){
            Component c = Component.text("□");
            if(spawnPointProfile.getOwner() != null){
                c = c.color(spawnPointProfile.getOwner().getTeam().color());
            }
            component = component.append(c);
        }

        component = component.append(Component.text("]").color(color));
        textDisplay.text(component);
    }
}
