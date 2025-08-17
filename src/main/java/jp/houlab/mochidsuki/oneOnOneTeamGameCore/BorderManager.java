package jp.houlab.mochidsuki.oneOnOneTeamGameCore;


import org.bukkit.Location;
import org.bukkit.Material;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class BorderManager {
    static public void close(){
        int px = config.getInt("Border.+X");
        int mx = config.getInt("Border.-X");
        int pz = config.getInt("Border.+Z");
        int mz = config.getInt("Border.-Z");

        //+z
        for(;mx<=px;mx++){
            for(int y = config.getInt("Border.-y");y<=config.getInt("Border.+y");y++){
                if(plugin.getServer().getWorld(config.getString("World")).getBlockAt(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,pz)).getType().equals(Material.AIR)){
                    plugin.getServer().getWorld(config.getString("World")).setType(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,pz),Material.BARRIER);
                }
            }
        }
        //-z
        mx = config.getInt("Border.-X");
        for(;mx<=px;mx++){
            for(int y = config.getInt("Border.-y");y<=config.getInt("Border.+y");y++){
                if(plugin.getServer().getWorld(config.getString("World")).getBlockAt(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,mz)).getType().equals(Material.AIR)){
                    plugin.getServer().getWorld(config.getString("World")).setType(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,mz),Material.BARRIER);
                }
            }
        }
    }

    static public void open(){
        int px = config.getInt("Border.+X");
        int mx = config.getInt("Border.-X");
        int pz = config.getInt("Border.+Z");
        int mz = config.getInt("Border.-Z");

        //+z
        for(;mx<=px;mx++){
            for(int y = config.getInt("Border.-y");y<=config.getInt("Border.+y");y++){
                if(plugin.getServer().getWorld(config.getString("World")).getBlockAt(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,pz)).getType().equals(Material.BARRIER)){
                    plugin.getServer().getWorld(config.getString("World")).setType(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,pz),Material.AIR);
                }
            }
        }
        //-z
        mx = config.getInt("Border.-X");
        for(;mx<=px;mx++){
            for(int y = config.getInt("Border.-y");y<=config.getInt("Border.+y");y++){
                if(plugin.getServer().getWorld(config.getString("World")).getBlockAt(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,mz)).getType().equals(Material.BARRIER)){
                    plugin.getServer().getWorld(config.getString("World")).setType(new Location(plugin.getServer().getWorld(config.getString("World")),mx,y,mz),Material.AIR);
                }
            }
        }
    }
}