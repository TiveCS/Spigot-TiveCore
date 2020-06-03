package com.github.tivecs.tivecore.lab.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class StorageManager {

    private final HashMap<String, StorageCenter> registeredCenter = new HashMap<>(); // DataCenter_ID, Storage Extension:Storage (User, .yml:User_Storage_Here)

    private HashMap<String, Object> loadedData = null;
    private HashSet<String> updateHistory = null;
    private StorageAbstract loadedStorage = null;

    public StorageManager(){ }

    //-----------------------------------------

    //-----------------------------------------

    public StorageCenter registerStorageCenter(@Nonnull String centerId){
        StorageCenter center = new StorageCenter(this, centerId);
        this.registeredCenter.put(centerId, center);
        return center;
    }

    public StorageManager unregisterStorageCenter(@Nonnull String centerId){
        this.registeredCenter.remove(centerId);
        return this;
    }

    public boolean hasStorageCenter(@Nonnull String centerId){
        return this.registeredCenter.containsKey(centerId);
    }

    public StorageCenter getStorageCenter(@Nonnull String centerId){
        return this.registeredCenter.get(centerId);
    }

    //-----------------------------------------

    public StorageAbstract createStorage(@Nonnull String centerId, @Nonnull StorageAbstract newStorage){
        StorageCenter center = hasStorageCenter(centerId) ?  getStorageCenter(centerId) : registerStorageCenter(centerId);

        center.addStorage(newStorage, true);

        return newStorage;
    }

    public StorageAbstract createStorage(@Nonnull StorageAbstract newStorage){
        String centerId = newStorage.isIdContainExtension() ? newStorage.getIdWithoutExtension() : newStorage.getId();
        return createStorage(centerId, newStorage);
    }

    //-----------------------------------------

    public StorageManager loadStorage(@Nonnull String centerId){
        StorageCenter center = getStorageCenter(centerId);
        if (center != null) {
            this.loadedStorage = center.getMainStorage();
        }
        return this;
    }
    public StorageManager loadStorage(@Nonnull String centerId, @Nullable String storageId){
        StorageCenter center = getStorageCenter(centerId);
        if (center != null) {
            this.loadedStorage = storageId != null ? center.getStorage(storageId) : center.getMainStorage();
        }
        return this;
    }
    public StorageManager loadStorage(@Nonnull StorageAbstract storage){
        this.loadedStorage = storage;
        return this;
    }

    public StorageManager loadData(@Nonnull StorageAbstract storage){
        this.loadedData = storage.getData();
        this.updateHistory = storage.getUpdateHistory();
        return this;
    }
    public StorageManager loadData(@Nonnull String centerId){
        StorageCenter center = getStorageCenter(centerId);
        if (center != null) {
            StorageAbstract storage = center.getMainStorage();
            return loadData(storage);
        }
        return this;
    }
    public StorageManager loadData(@Nonnull String centerId, @Nullable String storageId){
        StorageCenter center = getStorageCenter(centerId);
        if (center != null) {
            StorageAbstract storage = storageId != null ? center.getStorage(storageId) : center.getMainStorage();
            return loadData(storage);
        }
        return this;
    }

    //-----------------------------------------

    public void save(){
        this.loadedStorage.saveData();
    }

    //-----------------------------------------

    public void set(@Nonnull String path, Object value){
        this.loadedStorage.set(path, value);
    }

    public void directSet(@Nonnull String path, Object value) {
        this.loadedStorage.directSet(path, value);
    }

    public void setIfNotExists(@Nonnull String path, Object value) {
        this.loadedStorage.setIfNotExists(path, value);
    }

    public void directSetIfNotExists(@Nonnull String path, Object value) {
        this.loadedStorage.directSetIfNotExists(path, value);
    }

    //-----------------------------------------

    public HashMap<String, StorageCenter> getRegisteredCenter() {
        return registeredCenter;
    }

    public HashSet<String> getUpdateHistory() {
        return updateHistory;
    }

    public HashMap<String, Object> getLoadedData() {
        return loadedData;
    }
}
