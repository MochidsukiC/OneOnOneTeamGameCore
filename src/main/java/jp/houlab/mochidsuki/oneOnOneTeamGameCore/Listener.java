package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.ui.SpawnTeleportCUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class Listener implements org.bukkit.event.Listener {


    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player victim){
            Player damager = null;

            // ダメージの原因がエンティティの場合、攻撃者を取得する
            Entity damagerEntity = event.getDamager();
            // 発射物の場合、撃ったプレイヤーを取得
            if (damagerEntity instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                }
            } else if (damagerEntity instanceof Player) {
                damager = (Player) damagerEntity;
            }

            if(damager != null){
                DamagerProfile damagerProfile = new DamagerProfile(damager.getUniqueId());
                damagerProfile.runTaskTimer(plugin,0,20);
                damagerProfiles.put(victim,damagerProfile);
            }
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event){
        Player victim = event.getEntity();

        Player killer = event.getPlayer().getKiller();
        if(killer == null && damagerProfiles.containsKey(victim)){
            killer = damagerProfiles.get(victim).getDamager();
        }
        if(killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            MoneySystem.giveKillMoney(killer, victim);
            MoneySystem.resetDeathCount(killer);
            MoneySystem.giveDeathMoney(victim);
            OneOnOneTeamGameCoreMain.resetPlayerInventory(victim, List.of(Material.EMERALD,Material.LEATHER_HELMET,Material.LEATHER_CHESTPLATE,Material.LEATHER_LEGGINGS,Material.LEATHER_BOOTS),60);
        }

    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        SpawnTeleportCUI.sendSpawnPointCui(player);
        TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(player);
        if(teamProfile != null) {
            event.setRespawnLocation(teamProfile.getSiteProfile().getMasterSpawnPoint().getLocation());
        }
    }



    static final int DAMAGER_TIME_LIMIT = 10;
    static final Map<Player,DamagerProfile> damagerProfiles = new HashMap<>();

    private static class DamagerProfile extends BukkitRunnable {

        UUID damager;
        int t = 0;

        public DamagerProfile(UUID damager) {
            this.damager = damager;
        }

        @Override
        public void run() {
            if(t<DAMAGER_TIME_LIMIT){
                t++;
            }else{
                cancel();
            }
        }

        public @Nullable Player getDamager(){
            if(t<DAMAGER_TIME_LIMIT) {
                return Bukkit.getPlayer(damager);
            }else {
                return null;
            }
        }
    }


}
