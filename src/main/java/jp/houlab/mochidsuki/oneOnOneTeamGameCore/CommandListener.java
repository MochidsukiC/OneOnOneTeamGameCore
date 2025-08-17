package jp.houlab.mochidsuki.oneOnOneTeamGameCore;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.roundHandler.PrepareRoundHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("3otgc")) {
            if(args.length == 1) {
                switch (args[0]) {
                    case "start":{
                        new PrepareRoundHandler();
                        break;
                    }
                }
            }
        }
        return false;
    }
}
