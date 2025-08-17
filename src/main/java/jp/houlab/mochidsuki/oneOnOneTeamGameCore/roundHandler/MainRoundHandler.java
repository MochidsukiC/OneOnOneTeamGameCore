package jp.houlab.mochidsuki.oneOnOneTeamGameCore.roundHandler;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.BorderManager;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class MainRoundHandler implements Listener {

    public MainRoundHandler(){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        BorderManager.open();
        for (Player player : plugin.getServer().getOnlinePlayers()){
            TextComponent textComponent = Component.text("START!!").color(TextColor.color(255,254,0));
            player.showTitle(Title.title(textComponent,Component.text("")));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT,100,1);
        }

        for(TeamProfile teamProfile : TeamProfile.TeamProfiles.values()){
            teamProfile.getBossBar().setVisible(true);
            teamProfile.sync();
        }

        for(SpawnPointProfile spawnPointProfile : SpawnPointProfile.SpawnPointProfileMap.values()){
            spawnPointProfile.setEnable(true);
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event){
        TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(event.getPlayer());
        if(teamProfile == null) return;

        if(config.getString("Core.Material") == null || Material.matchMaterial(config.getString("Core.Material")) == null) throw new IllegalArgumentException("Config Error : Core.");
        if(event.getBlock().getType().equals(Material.matchMaterial(config.getString("Core.Material")))) {
            for(TeamProfile breakTeamProfile : TeamProfile.TeamProfiles.values()) {
                if (event.getBlock().getLocation().distance(breakTeamProfile.getSiteProfile().getCoreLocation().getBlock().getLocation()) <= 1) {
                    breakTeamProfile.getSiteProfile().subtractCoreHealth(1);
                    event.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD, 1));
                    breakTeamProfile.updateBossBar();


                    if (breakTeamProfile.getSiteProfile().getCoreHealth() <= 0) {
                        breakTeamProfile.setLive(false);

                        //終了検知
                        int i = 0;
                        TeamProfile winTeamProfile = null;
                        for(TeamProfile tp :  TeamProfile.TeamProfiles.values()) {
                            if(tp.isLive()){
                                i++;
                                winTeamProfile = tp;
                            }
                        }

                        if(i<=1) {
                            //end
                            HandlerList.unregisterAll(this);
                            new EndRoundHandler(winTeamProfile);
                        }
                    }
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

}
