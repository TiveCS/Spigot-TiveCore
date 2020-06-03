package com.github.tivecs.tivecore.lab.storage.yaml;

import com.github.tivecs.tivecore.lab.storage.StorageAbstract;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ConfigManager extends StorageAbstract {

    private final FileConfiguration config;
    private final File file;

    public ConfigManager(@Nonnull File file) {
        super(file.getName(), new String[]{"yml", "yaml"});
        this.file = file;
        if (!this.file.exists()){
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public boolean readData() {
        getUpdateHistory().clear();
        getData().clear();

        getData().putAll(getRootChild(true));
        return false;
    }

    @Override
    public boolean saveData() {
        for (String h : getUpdateHistory()){
            config.set(h, getData().get(h));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //---------------------------------

    @Override
    public boolean set(String path, Object value) {
        getData().put(path, value);
        getUpdateHistory().add(path);
        return true;
    }

    @Override
    public boolean directSet(String path, Object value) {
        this.config.set(path, value);
        getData().put(path, value);
        return true;
    }

    @Override
    public boolean setIfNotExists(String path, Object value) {
        if (!getData().containsKey(path)){
            set(path, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean directSetIfNotExists(String path, Object value) {
        if (!this.config.contains(path)){
            return directSet(path, value);
        }
        return false;
    }

    //---------------------------------

    @Override
    public Set<String> getChildPath(final String parent, final boolean keys) {
        return this.config.getConfigurationSection(parent).getKeys(keys);
    }

    @Override
    public HashMap<String, Object> getChild(final String parent, final boolean keys) {
        Set<String> paths = this.config.getConfigurationSection(parent).getKeys(keys);

        HashMap<String, Object> map = new HashMap<>();
        for (String s : paths){
            map.put(s, this.config.get(keys ? s : parent + "." + s));
        }

        return map;
    }

    @Override
    public HashMap<String, Object> getRootChild(final boolean keys) {
        Set<String> paths = this.config.getRoot().getKeys(keys);

        HashMap<String, Object> map = new HashMap<>();
        for (String s : paths){
            map.put(s, this.config.get(s));
        }

        return map;
    }

    @Override
    public Set<String> getRootChildPath(final boolean keys) {
        return this.config.getRoot().getKeys(keys);
    }

    //---------------------------------

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
