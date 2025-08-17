package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class MoneySystem {
    private static Map<UUID, Integer> deathCounts = new HashMap<>();

    static public void giveInitial(){
        for(Player player : plugin.getServer().getOnlinePlayers()){
            player.getInventory().setItem(6,new ItemStack(Material.EMERALD,10));
        }
    }

    static public void giveKillMoney(Player killer, Player victim){
        int i = 0;
        for (ItemStack itemStack : victim.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType().equals(Material.EMERALD)) {
                i = i + itemStack.getAmount();
            }
        }
        if(killer != null && !victim.getUniqueId().equals(killer.getUniqueId())) {
            killer.getInventory().addItem(new ItemStack(Material.EMERALD, i * 2 / 3 + 8));
        }
    }


    static public void giveDeathMoney(Player victim){
        if(!deathCounts.containsKey(victim.getUniqueId())){
            deathCounts.put(victim.getUniqueId(), 0);
        }
        switch (deathCounts.get(victim.getUniqueId())) {
            case 0 -> victim.getInventory().addItem(new ItemStack(Material.EMERALD, 2));
            case 1 -> victim.getInventory().addItem(new ItemStack(Material.EMERALD, 4));
            case 2 -> victim.getInventory().addItem(new ItemStack(Material.EMERALD, 6));
            case 3 -> victim.getInventory().addItem(new ItemStack(Material.EMERALD, 8));
            default -> victim.getInventory().addItem(new ItemStack(Material.EMERALD, 10));
        }

        deathCounts.put(victim.getUniqueId(), deathCounts.get(victim.getUniqueId()) + 1);
    }

    static public void resetDeathCount(Player player){
        deathCounts.put(player.getUniqueId(), 0);
    }

}
