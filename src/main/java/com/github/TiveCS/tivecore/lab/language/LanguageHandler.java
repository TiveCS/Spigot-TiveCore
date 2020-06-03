package com.github.tivecs.tivecore.lab.language;

import com.github.tivecs.tivecore.lab.utils.StringUtils;
import com.google.common.base.Charsets;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LanguageHandler {

    private HashMap<String, Language> registeredLanguages = new HashMap<>();

    private HashMap<UUID, String> userSelectedLanguage = new HashMap<>();

    private Language defaultLanguage = null;

    public LanguageHandler(){ }
    public LanguageHandler(File languageFolder){
        loadLanguages(languageFolder);
    }

    //-----------------------------------

    public boolean initUserDefaultLanguage(UUID userUniqueId){
        if (userSelectedLanguage.get(userUniqueId) == null) {
            if (registeredLanguages.size() > 0) {
                List<String> s = new ArrayList<>(registeredLanguages.keySet());
                userSelectedLanguage.put(userUniqueId, s.get(0));
                return true;
            }
        }
        return userSelectedLanguage.get(userUniqueId) == null;
    }

    public Language getUserSelectedLanguage(UUID userUniqueId){
        initUserDefaultLanguage(userUniqueId);
        return registeredLanguages.get(userSelectedLanguage.get(userUniqueId));
    }

    public void sendMessage(Player player, String messagePath){
        if (player.isOnline()) {
            Language lang = getUserSelectedLanguage(player.getUniqueId());

            if (lang != null) {
                sendMessage((CommandSender) player, messagePath, lang, null);
            }
        }
    }
    public void sendMessage(CommandSender user, String messagePath, Placeholder placeholder){
        Language lang = null;
        if (user instanceof Player){
            lang = getUserSelectedLanguage(((Player) user).getUniqueId());
        }else{
            lang = defaultLanguage;
        }

        sendMessage(user, messagePath, lang, placeholder);
    }
    public void sendMessage(CommandSender user, String messagePath, Language lang){
        sendMessage(user, messagePath, lang, null);
    }
    public void sendMessage(CommandSender user, String messagePath, Language lang, Placeholder extraPlaceholder){
        if (lang == null){return;}

        List<String> msg = prepareMessage(messagePath, lang);
        boolean isAvailable = (user instanceof Player) ? ((Player) user).isOnline() : true;

        if (isAvailable) {
            if (extraPlaceholder != null){
                msg = extraPlaceholder.use(msg);
            }
            msg = StringUtils.colored(msg);
            for (String m : msg) {
                user.sendMessage(m);
            }
        }
    }

    public void broadcastMessage(String messagePath){
        broadcastMessage(messagePath, defaultLanguage, null);
    }

    public void broadcastMessage(String messagePath, Placeholder extraPlaceholder){
        broadcastMessage(messagePath, defaultLanguage, extraPlaceholder);
    }
    public void broadcastMessage(String messagePath, Language lang){
        broadcastMessage(messagePath, lang, null);
    }
    public void broadcastMessage(String messagePath, Language lang, Placeholder extraPlaceholder){
        if (lang != null){
            List<String> msg = prepareMessage(messagePath, lang);
            if (extraPlaceholder != null){
                msg = extraPlaceholder.use(msg);
            }
            msg = StringUtils.colored(msg);
            for (String m : msg){
                Bukkit.broadcastMessage(m);
            }
        }
    }

    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath){
        Language lang = getUserSelectedLanguage(player.getUniqueId());
        if (lang == null){
            lang = defaultLanguage;
        }

        sendActionbar(plugin, player, messagePath, lang, 30, null);
    }
    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath, long delay){
        Language lang = getUserSelectedLanguage(player.getUniqueId());
        if (lang == null){
            lang = defaultLanguage;
        }

        sendActionbar(plugin, player, messagePath, lang, delay, null);
    }
    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath, Placeholder extraPlaceholder){
        sendActionbar(plugin, player, messagePath, defaultLanguage, 30, extraPlaceholder);
    }
    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath, long delay, Placeholder extraPlaceholder){
        sendActionbar(plugin, player, messagePath, defaultLanguage, delay, extraPlaceholder);
    }
    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath, Language lang){
        sendActionbar(plugin, player, messagePath, lang, 30, null);
    }
    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath, Language lang, long delay){
        sendActionbar(plugin, player, messagePath, lang, delay, null);
    }
    public void sendActionbar(JavaPlugin plugin, Player player, String messagePath, Language lang, long delay, Placeholder extraPlaceholder){
        if (plugin != null && lang != null) {
            List<String> msg = prepareMessage(messagePath, lang);
            if (extraPlaceholder != null){
                msg = extraPlaceholder.use(msg);
            }
            msg = StringUtils.colored(msg);

            final int size = msg.size();
            boolean moreThanOne = size > 1;
            for (int i = 0; i < size; i++){
                final TextComponent com = new TextComponent(msg.get(i));
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, com);
                    }
                }, delay*i);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> prepareMessage(String messagePath, Language lang){
        List<String> msg = (List<String>) lang.getData().getOrDefault(messagePath, new ArrayList<>());
        msg = new ArrayList<>(StringUtils.colored(lang.getPlaceholder().use(msg)));
        return msg;
    }

    //-----------------------------------
    public void registerUser(UUID userUniqueId, String langId){
        userSelectedLanguage.put(userUniqueId, langId);
    }

    public void loadLanguages(File parentDirectory){
        if (parentDirectory.isDirectory()){
            File[] langs = parentDirectory.listFiles();
            for (File f : langs){

                try {
                    Language lang = new Language(f);
                    lang.saveData();
                    lang.readAttribute();
                    lang.readPlaceholders();
                    lang.readData();
                    if (lang.isAsDefaultLanguage()) {
                        defaultLanguage = lang;
                    }
                    registeredLanguages.put(lang.getLanguageId(), lang);
                }catch (NullPointerException e){
                    System.out.println("Skipped " + f.getName() + " as language file. (NOT_LANGUAGE_FILE)");
                    e.printStackTrace();
                }
            }
        }
    }

    //------------------------------

    public void loadLanguageFile(JavaPlugin plugin, String fileName){
        File languageFolder = new File(plugin.getDataFolder(), "language");
        if (!languageFolder.exists()){languageFolder.mkdir();}

        File lang_file = new File(languageFolder, fileName);
        FileConfiguration lang_config = YamlConfiguration.loadConfiguration(lang_file);
        InputStream input = plugin.getResource("language/" + fileName);
        if (input != null){
            lang_config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, Charsets.UTF_8)));
            lang_config.options().copyDefaults(true);
        }
        try {
            lang_config.save(lang_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------

    public boolean hasDefaultLanguage(){
        return defaultLanguage != null;
    }

    public HashMap<String, Language> getRegisteredLanguages() {
        return registeredLanguages;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }
}
