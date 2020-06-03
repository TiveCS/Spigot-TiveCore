package com.github.tivecs.tivecore.lab.storage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StorageCenter {

    private final String centerId;
    private final StorageManager manager;
    private final HashMap<String, StorageAbstract> storages = new HashMap<>(); // Extension or ID, Storage
    private StorageAbstract mainStorage = null;

    public StorageCenter(@Nonnull StorageManager manager, @Nonnull String centerId){
        this.manager = manager;
        this.centerId = centerId;
    }

    public StorageAbstract getStorage(@Nonnull String storageId){
        return storages.get(storageId);
    }

    public StorageCenter addStorage(@Nonnull StorageAbstract storage){
        storages.put(storage.isIdContainExtension() ? storage.getIdWithoutExtension() : storage.getId(), storage);

        if (this.mainStorage == null){
            this.mainStorage = storage;
        }
        return this;
    }

    public StorageCenter addStorage(@Nonnull StorageAbstract storage, boolean checkSameWithDataCenter){
        if (checkSameWithDataCenter){
            if (storage.getIdWithoutExtension().equals(this.centerId)){
                addStorage(storage);
            }
        }else{
            addStorage(storage);
        }
        return this;
    }

    public StorageCenter removeStorage(@Nonnull StorageAbstract storage){
        List<String> keys = new ArrayList<>(storages.keySet());
        int size = keys.size();

        while (size > 0){
            String key = keys.get(size - 1);

            StorageAbstract value = storages.get(key);
            if (value.equals(storage)){
                storages.remove(key);
                if (this.mainStorage.equals(storage)){
                    this.mainStorage = null;
                }
                break;
            }
            size--;
        }
        return this;
    }

    public StorageCenter removeStorage(@Nonnull String storageId){
        StorageAbstract storage = storages.get(storageId);
        storages.remove(storageId);

        if (this.mainStorage.equals(storage)){
            this.mainStorage = null;
        }
        return this;
    }

    public void setMainStorage(StorageAbstract mainStorage) {
        this.mainStorage = mainStorage;
    }

    public StorageAbstract getMainStorage() {
        return mainStorage;
    }

    public StorageManager getManager() {
        return manager;
    }

    public String getCenterId() {
        return centerId;
    }

    public HashMap<String, StorageAbstract> getStorages() {
        return storages;
    }
}
