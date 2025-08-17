package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.ui.SpawnTeleportCUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


import java.time.Duration;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile.SpawnPointProfileMap;

public class SpawnCommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("rsp")) {
            if(args.length == 0) {
                if(sender instanceof Player player) {
                    TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(player);
                    if(teamProfile != null) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,120,10));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,120,10));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,120,10));

                        new BukkitRunnable() {
                            int time = 6;
                            public void run(){
                                time--;
                                player.showTitle(Title.title(Component.text(""),Component.text("マスタースポーンポイントテレポートにテレポートします"+".".repeat(time)), Title.Times.times(Duration.ZERO,Duration.ofSeconds(2),Duration.ofMillis(500))));
                                if(time == 2) player.playSound(player, Sound.ENTITY_WARDEN_SONIC_CHARGE, 0.3f, 1f);
                                if(time < 1) {
                                    player.teleport(teamProfile.getSiteProfile().getMasterSpawnPoint().getLocation());
                                    player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 1.0f);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(plugin,0,20);

                    }
                }
            }
            if(args.length >= 1) {
                if (sender instanceof Player player) {
                    TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(player);
                    if(teamProfile != null && teamProfile.getSiteProfile().getMasterSpawnPoint().getLocation().distance(player.getLocation()) < 5) {
                        if (SpawnPointUtils.isConnect(teamProfile.getSiteProfile().getMasterSpawnPoint().getName(), args[0], teamProfile) && SpawnPointProfileMap.containsKey(args[0])) {
                            player.teleport(SpawnPointProfileMap.get(args[0]).getLocation());
                            sender.sendMessage(Component.text("スポーンポイントにテレポートしました。"));
                        } else {
                            sender.sendMessage(Component.text("スポーンポイントが支配できていないか、接続できません。").color(NamedTextColor.RED));
                        }
                    }else {
                        player.sendMessage(Component.text("マスタースポーンポイントから遠すぎます。").color(NamedTextColor.RED));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
