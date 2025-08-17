package jp.houlab.mochidsuki.oneOnOneTeamGameCore.roundHandler;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.BorderManager;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.events.PrepareRoundEvent;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.MoneySystem.giveInitial;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile.TeamProfiles;

public class PrepareRoundHandler extends BukkitRunnable implements Listener {
private BossBar bossBar;
private int time = config.getInt("PrepareTime");

    public PrepareRoundHandler() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);


        //reset
        for(TeamProfile teamProfile : TeamProfiles.values()){
            if(config.getString("Core.Material") == null || Material.matchMaterial(config.getString("Core.Material")) == null) throw new IllegalArgumentException("Config Error : Core.");
            teamProfile.getSiteProfile().getCoreLocation().getBlock().setType(Material.matchMaterial(config.getString("Core.Material")));
            teamProfile.getSiteProfile().reset();

            teamProfile.setLive(true);
            teamProfile.getBossBar().setVisible(false);
            teamProfile.sync();

            for(Player player : teamProfile.getPlayers()){
                player.teleport(teamProfile.getSiteProfile().getMasterSpawnPoint().getLocation());
                OneOnOneTeamGameCoreMain.resetPlayerInventory(player,null,100);
                jp.houlab.mochidsuki.skillItemManager.Main.allCoolDown(player);
            }
        }
        for(SpawnPointProfile spawnPointProfile : SpawnPointProfile.SpawnPointProfileMap.values()){
            spawnPointProfile.reset();
            spawnPointProfile.setEnable(false);
        }

        //prepare
        BorderManager.close();
        giveInitial();
        bossBar = Bukkit.createBossBar("準備時間", BarColor.WHITE, BarStyle.SOLID);
        for(Player player : Bukkit.getOnlinePlayers()){
            bossBar.addPlayer(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,10,10));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,10,10));
            player.setGameMode(GameMode.SURVIVAL);

            if(player.getScoreboard().getPlayerTeam(player) != null) {
                Color c = Color.fromRGB(player.getScoreboard().getPlayerTeam(player).getColor().asBungee().getColor().getRed(),player.getScoreboard().getPlayerTeam(player).getColor().asBungee().getColor().getGreen(),player.getScoreboard().getPlayerTeam(player).getColor().asBungee().getColor().getBlue());
                ItemStack i = new ItemStack(Material.LEATHER_LEGGINGS);
                LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
                meta.setColor(c);
                i.setItemMeta(meta);
                player.getInventory().setItem(35,i);
                player.getInventory().setItem(22,new ItemStack(Material.LEATHER_HELMET));
                player.getInventory().setItem(23,new ItemStack(Material.LEATHER_CHESTPLATE));
                player.getInventory().setItem(24,new ItemStack(Material.LEATHER_BOOTS));

            }
        }

        PrepareRoundEvent prepareRoundEvent = new PrepareRoundEvent();
        Bukkit.getPluginManager().callEvent(prepareRoundEvent);

        this.runTaskTimer(plugin,0,20);

    }



    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }



    @Override
    public void run() {
        time--;
        bossBar.setProgress((double) time/config.getInt("PrepareTime"));
        bossBar.setTitle("準備時間残り : " + time + "秒");

        if(time <=5 && time > 0){
            for(Player player : Bukkit.getOnlinePlayers()) {
                Title title = Title.title(Component.text(time), Component.text(""));
                player.showTitle(title);
                player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 0);
            }
        }

        if(time == 0){
            bossBar.setVisible(false);
            bossBar.removeAll();
            HandlerList.unregisterAll(this);


            new MainRoundHandler();
            cancel();
        }
    }

}
