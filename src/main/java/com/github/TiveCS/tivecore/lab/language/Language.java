package com.github.tivecs.tivecore.lab.language;

import com.github.tivecs.tivecore.lab.storage.yaml.ConfigManager;
import com.github.tivecs.tivecore.lab.utils.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Language extends ConfigManager {

    private FileConfiguration config;

    private String languageId, parentLanguageId;
    private boolean asDefaultLanguage = false;
    private Placeholder placeholder;

    public Language(File languageFile){
        super(languageFile);

        this.config = getConfig();
        if (config != null){
            if (!config.contains("attributes")) {
                config.createSection("attributes");
            }
            if (!config.contains("placeholders")){
                config.createSection("placeholders");
            }
            if (!config.contains("messages")) {
                config.createSection("messages");
            }
        }

        String fileName = languageFile.getName();
        if (fileName.endsWith(".yml")){
            languageId = fileName.substring(0, fileName.length() - 4);
        }else{
            languageId = fileName;
        }

    }

    public void set(String path, List<String> value) {
        super.set(path, value);
    }

    public void setIfNotExists(String path, List<String> value) {
        super.setIfNotExists(path, value);
    }

    @Override
    public boolean saveData() {
        if (config != null){
            HashMap<String, Object> data = getData();
            for (String path : getUpdateHistory()){
                config.set("messages." + path, data.get(path));
            }
            getUpdateHistory().clear();

            try {
                config.save(getFile());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean readData() {
        if (config != null){
            getData().clear();

            Set<String> childs = getChildPath("messages", false);
            for (String path : childs){

                List<String> msg = config.getStringList("messages." + path);
                int size = msg.size();

                getData().put(path, placeholder.use(StringUtils.colored(msg)));
            }
            return true;
        }
        return false;
    }

    public void readAttribute(){
        if (config != null){
            if (config.contains("attributes")) {
                this.languageId = config.getString("attributes.language-id");
                this.parentLanguageId = config.getString("attributes.parent-language-id");
                this.asDefaultLanguage = config.getBoolean("attributes.as-default-language");
            }
        }
    }

    public void readPlaceholders(){
        if (config != null){
            if (placeholder == null) {
                placeholder = new Placeholder();
            }else{
                placeholder.getReplacer().clear();
                placeholder.getReplacerList().clear();
            }
            if (config.contains("placeholders")) {
                for (String p : config.getConfigurationSection("placeholders").getKeys(false)) {
                    placeholder.addReplacer(p, config.getString("placeholders." + p));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getMessage(String path){
        List<String> msg = (List<String>) getData().getOrDefault(path, new ArrayList<>());
        return msg;
    }

    public String getLanguageId() {
        return languageId;
    }

    public String getParentLanguageId() {
        return parentLanguageId;
    }

    public boolean isAsDefaultLanguage() {
        return asDefaultLanguage;
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }
}
