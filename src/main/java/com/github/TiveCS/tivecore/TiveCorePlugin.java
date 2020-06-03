package com.github.tivecs.tivecore;

import com.github.tivecs.tivecore.cmds.CmdTiveCore;
import com.github.tivecs.tivecore.lab.language.LanguageHandler;
import com.github.tivecs.tivecore.lab.menu.MenuListener;
import com.github.tivecs.tivecore.menu.FileEditorMenu;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TiveCorePlugin extends JavaPlugin {

    private LanguageHandler languageHandler = null;
    private MenuListener menuListener = null;
    private TivePlaceholder placeholder = null;
    private File languageFolder = null;

    @Override
    public void onEnable() {

        loadConfigFiles();
        loadLanguages();
        loadMenus();
        loadCommands();

        placeholder = new TivePlaceholder();

        List<String> loadMsg = new ArrayList<>(languageHandler.getDefaultLanguage().getMessage("system-loaded-language"));
        loadMsg = placeholder.use(loadMsg);
        for (String m : loadMsg){
            getLogger().info(ChatColor.translateAlternateColorCodes('&', m));
        }
    }

    private void loadCommands(){
        getCommand("tivecore").setExecutor(new CmdTiveCore());
        getCommand("tivecore").setTabCompleter(new CmdTiveCore());
    }

    private void loadMenus(){
        File folder = new File(getDataFolder(), "menu");
        if (!folder.exists()){folder.mkdir();}

        menuListener = new MenuListener(this);
        menuListener.registerMenu(
                new FileEditorMenu()
        );
    }

    private void loadConfigFiles(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadLanguages(){

        this.languageFolder = new File(getDataFolder(), "language");
        if (!languageFolder.exists()){languageFolder.mkdir();}

        languageHandler = new LanguageHandler();
        languageHandler.loadLanguageFile(this, "en-US.yml");
        languageHandler.loadLanguages(languageFolder);


    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public MenuListener getMenuListener() {
        return menuListener;
    }

    public TivePlaceholder getPlaceholder(){
        return placeholder;
    }
}
