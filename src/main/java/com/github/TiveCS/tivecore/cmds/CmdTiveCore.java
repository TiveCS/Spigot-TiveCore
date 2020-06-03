package com.github.tivecs.tivecore.cmds;

import com.github.tivecs.tivecore.TiveCorePlugin;
import com.github.tivecs.tivecore.lab.menu.Menu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdTiveCore implements CommandExecutor, TabCompleter {

    private static TiveCorePlugin plugin = TiveCorePlugin.getPlugin(TiveCorePlugin.class);

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tivecore")){
            if (strings.length == 0 || (strings.length == 1 && (strings[0].equalsIgnoreCase("help") || strings[0].equalsIgnoreCase("?")))){
                return true;
            }
            if (commandSender instanceof Player) {
                if (strings.length == 1) {
                    if (strings[0].equalsIgnoreCase("filemanager")) {
                        Menu m = plugin.getMenuListener().getRegisteredMenu().get("core-FileEditor");
                        plugin.getMenuListener().open("core-FileEditor", 1, ((Player) commandSender));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tivecore")){
            if (strings.length == 1){
                return Arrays.asList("?", "help", "filemanager", "info", "language");
            }

            if (strings.length == 2){
                if (strings[0].equalsIgnoreCase("language")){
                    return new ArrayList<>(plugin.getLanguageHandler().getRegisteredLanguages().keySet());
                }
            }
        }
        return null;
    }
}
