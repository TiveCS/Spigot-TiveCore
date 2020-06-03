package com.github.tivecs.tivecore.lab.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that used for creating storage type that has been generalized by using abstracted methods.
 *
 * @since 1.1.0
 * **/
public abstract class StorageAbstract {

    private final HashMap<String, Object> data;
    private final HashSet<String> updateHistory;
    private final String[] extensions;
    private final String id;

    public StorageAbstract(@Nonnull String id, @Nullable String[] extensions){
        this.id = id;
        this.data = new HashMap<>();
        this.updateHistory = new HashSet<>();
        this.extensions = extensions;
    }

    //----------------------------------

    /**
     * Check if the ID contain the storage extension or not.
     *
     * @return true if there are used extension. false if there are no used extension
     *
     * @since 1.1.0
     * */
    public boolean isIdContainExtension(){
        if (this.extensions != null){
            for (String s : extensions){
                if (this.id.endsWith("." + s)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the used extension from the ID.
     *
     * @return if there are used extension, they will return extension name. otherwise return null
     *
     * @since 1.1.0
     * */
    public String getUsedExtension(){
        if (this.extensions != null){
            for (String s : extensions){
                if (this.id.endsWith("." + s)){
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Check if the ID using any one of extension, then return the ID without extension name.
     *
     * @return ID without extension name
     *
     * @since 1.1.0
     * */
    public String getIdWithoutExtension(){
        String usedExtension = getUsedExtension();
        String newId = this.id;

        if (usedExtension != null){
            newId = newId.replace("." + usedExtension, "");
        }
        return newId;
    }

    //----------------------------------

    /**
     * Read raw data from file and store it inside getData()'s field. The raw data should be converted into primitive type
     *
     * @return successfully read data or not
     *
     * @since 1.1.0
     * **/
    public abstract boolean readData();

    /**
     * Save data that directly and edited non-directly data
     *
     * @return successfully save data or not
     *
     * @since 1.1.0
     * **/
    public abstract boolean saveData();

    /**
     * Set data by specified path into defined value
     *
     * @param path data path for defined value
     * @param value value of data
     *
     * @return is successfully set data or not
     *
     * @since 1.1.0
     *
     * **/
    public abstract boolean set(String path, Object value);

    /**
     * Directly set data into storage file by specified path into defined value
     *
     * @param path data path for defined value
     * @param value value of data
     *
     * @return is successfully set data or not
     *
     * @since 1.1.0
     *
     * **/
    public abstract boolean directSet(String path, Object value);

    /**
     * Set data by specified path into defined value if the data path is not exists.
     *
     * @param path data path for defined value
     * @param value value of data
     *
     * @return is successfully set data or not
     *
     * @since 1.1.0
     *
     * **/
    public abstract boolean setIfNotExists(String path, Object value);

    /**
     * Directly set data into storage file by specified path into defined value it the data path is not exists.<br>
     * Directly set doesn't track the log into updateHistory
     *
     * @param path data path for defined value
     * @param value value of data
     *
     * @return is successfully set data or not
     *
     * @since 1.1.0
     *
     * **/
    public abstract boolean directSetIfNotExists(String path, Object value);

    /**
     *Directly interact with file to get related path by defined parent that separated by dot
     *
     * @param parent parent path
     * @param keys true if is using deep path, and false if not using deep path
     *
     * @return childs path of parent
     *
     * @since 1.1.0
     *
     * **/
    public abstract Set<String> getChildPath(String parent, final boolean keys);

    /**
     * Directly interact with file to get related path by root that separated by dot.
     *
     * @param keys true if is using deep path, and false if not using deep path
     *
     * @return childs path of parent
     *
     * @since 1.1.0
     *
     * **/
    public abstract Set<String> getRootChildPath(final boolean keys);

    /**
     * Directly interact with file to get whole data path and its value from root.<br>
     * Directly set doesn't track the log into updateHistory
     *
     * @param keys true if is using deep path, and false if not using deep path
     *
     * @return whole data path and value
     *
     * @since 1.1.0
     *
     * **/
    public abstract HashMap<String, Object> getRootChild(final boolean keys);

    /**
     * Directly interact with file to get whole data path and its value from parent path
     *
     * @param parent path
     * @param keys true if is using deep path, and false if not using deep path
     *
     * @return whole data path and value
     *
     * @since 1.1.0
     *
     * **/
    public abstract HashMap<String, Object> getChild(final String parent, final boolean keys);

    //-----------------------------------

    /**
     * Get data that have been read from file
     *
     * @return data path and its value
     *
     * @since 1.1.0
     * **/
    public HashMap<String, Object> getData() {
        return data;
    }

    /**
     * Get edited data path
     *
     * @return edited data path
     *
     * @since 1.1.0
     * **/
    public HashSet<String> getUpdateHistory() {
        return updateHistory;
    }

    /**
     * Storage file's extension that at the end of file name. (e.g myStorage.yml, ourStorage.json)
     *
     * @return accepted file extensions
     *
     * @since 1.1.0
     * */
    public String[] getExtensions() {
        return extensions;
    }

    /**
     * Identity of Storage
     *
     * @return Identity string
     *
     * @since 1.1.0
     *
     * */
    public String getId() {
        return id;
    }
}
