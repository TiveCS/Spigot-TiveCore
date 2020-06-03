package com.github.tivecs.tivecore.lab.storage.gson;

import com.github.tivecs.tivecore.lab.storage.StorageAbstract;
import com.google.gson.*;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;

public class GsonManager extends StorageAbstract {

    private final File file;
    private final Gson gson, prettyGson;
    private final GsonBuilder builder, prettyBuilder;

    private JsonElement root = null;
    private boolean rootIsJsonObject = false;

    public GsonManager(@Nonnull File file){
        super(file.getName(), new String[]{"json"});
        this.file = file;

        this.builder = new GsonBuilder();
        this.gson = this.builder.create();

        this.prettyBuilder = new GsonBuilder();
        this.prettyGson = this.prettyBuilder.setPrettyPrinting().create();

        if (!this.file.exists()){
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------

    @Override
    public boolean readData() {
        getData().clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            this.root = this.gson.fromJson(reader, JsonElement.class);
            this.rootIsJsonObject = this.root.isJsonObject();
            getData().putAll(getRootChild(true));
            return true;
        } catch (FileNotFoundException e) {
            this.root = null;
            this.rootIsJsonObject = false;
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean saveData() {

        if (rootIsJsonObject) {
            JsonObject o = this.root.getAsJsonObject();
            for (String path : getUpdateHistory()) {
                directSet(path, getData().get(path));
            }

            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                prettyGson.toJson(this.root, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    //----------------------------------------

    @Override
    public boolean set(String path, Object value) {
        getData().put(path, value);
        getUpdateHistory().add(path);
        return true;
    }

    @Override
    public boolean directSet(String path, Object value) {
        String[] paths = convertToSafePath(path);
        boolean isNeedParent = path.contains(".");

        String parentPath = getParentPath(paths);

        JsonElement target = isNeedParent ? getElement(parentPath) : this.root;
        if (target != null && target.isJsonObject()){
            String last = paths[paths.length - 1];
            JsonObject o = target.getAsJsonObject();

            dynamicAddProperty(o, last, value);
            getData().put(path, value);

            return true;
        }

        return false;
    }

    @Override
    public boolean setIfNotExists(String path, Object value) {
        if (!getData().containsKey(path)){
            return set(path, value);
        }
        return false;
    }

    @Override
    public boolean directSetIfNotExists(String path, Object value) {

        String[] paths = convertToSafePath(path);
        boolean isNeedParent = path.contains(".");

        String parentPath = getParentPath(paths);

        JsonElement target = isNeedParent ? getElement(parentPath) : this.root;
        if (target != null && target.isJsonObject()){
            JsonObject o = target.getAsJsonObject();
            String last = paths[paths.length - 1];

            if (!o.has(last)) {
                dynamicAddProperty(o, last, value);
                getData().put(path, value);
                return true;
            }

        }

        return false;
    }

    //----------------------------------------

    private void dynamicAddProperty(JsonObject json, String property, Object value){
        if (isNumber(value)){
            json.addProperty(property, (Number) value);
        }else if (isBoolean(value)){
            json.addProperty(property, (Boolean) value);
        }else if (isCharacter(value)){
            json.addProperty(property, (Character) value);
        }else {
            json.addProperty(property, value.toString());
        }
    }

    private String[] convertToSafePath(String path){
        String[] paths;
        if (path.contains(".")){
            paths = path.split("[.]");
        }else{
            paths = new String[]{path};
        }
        return paths;
    }

    private String getParentPath(String[] paths){
        StringBuilder parent = new StringBuilder();
        int i;
        for (i = 0; i + 1 < paths.length; i++){
            parent.append(paths[i]);
            if (i + 2 < paths.length){
                parent.append(".");
            }
        }
        return parent.toString();
    }

    //----------------------------------------

    public JsonElement getElement(String parent){
        JsonElement element = null;

        if (rootIsJsonObject) {
            String[] paths = parent.split("[.]");
            JsonObject r = this.root.getAsJsonObject();

            JsonElement temp = null;

            // Searching the end of parent path's entries
            for (int i = 0; i < paths.length; i++) {
                String p = paths[i];
                if (i == 0 && r.has(p)) {
                    temp = r.get(p);
                } else if (temp != null && temp.isJsonObject()) {
                    JsonObject o = temp.getAsJsonObject();
                    if (o.has(p)) {
                        temp = o.get(p);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                if (i + 1 == paths.length){
                    element = temp;
                }
            }
        }

        return element;
    }

    public Set<Map.Entry<String, JsonElement>> getEntries(String parent){
        Set<Map.Entry<String, JsonElement>> entries = null;
        if (rootIsJsonObject) {
            String[] paths = parent.split("[.]");
            JsonObject r = this.root.getAsJsonObject();

            JsonElement temp = getElement(parent);
            if (temp != null && temp.isJsonObject()){
                entries = temp.getAsJsonObject().entrySet();
            }
        }
        return entries;
    }

    @Override
    public Set<String> getChildPath(String parent, boolean keys) {
        HashSet<String> set = new HashSet<>();
        if (rootIsJsonObject){
            // Search the parent's entries
            Set<Map.Entry<String, JsonElement>> entries = getEntries(parent);

            // Processing
            if (entries != null) {
                // Deep Search. Give full path instead give its keys.
                if (keys) {
                    for (Map.Entry<String, JsonElement> entry : entries){
                        String path = parent + "." + entry.getKey();
                        JsonElement e = entry.getValue();
                        set.add(parent + "." + entry.getKey());

                        if (e.isJsonObject()){
                            JsonObject o = e.getAsJsonObject();
                            set.addAll(getChildPath(path, true));
                        }
                    }
                }
                // Only childs keys. Also doesn't give full path.
                else {
                    for (Map.Entry<String, JsonElement> entry : entries) {
                        set.add(entry.getKey());
                    }
                }
            }
        }

        return set;
    }

    @Override
    public Set<String> getRootChildPath(boolean keys) {
        HashSet<String> set = new HashSet<>();
        if (rootIsJsonObject){
            JsonObject r = this.root.getAsJsonObject();

            Set<Map.Entry<String, JsonElement>> entries = r.entrySet();
            if (keys){
                for (Map.Entry<String, JsonElement> entry : entries){
                    String path = entry.getKey();
                    JsonElement e = entry.getValue();
                    set.add(entry.getKey());

                    if (e.isJsonObject()){
                        JsonObject o = e.getAsJsonObject();
                        set.addAll(getChildPath(path, true));
                    }
                }
            }else {
                for (Map.Entry<String, JsonElement> entry : entries){
                    set.add(entry.getKey());
                }
            }
        }

        return set;
    }

    @Override
    public HashMap<String, Object> getRootChild(boolean keys) {
        HashMap<String, Object> map = new HashMap<>();

        if (rootIsJsonObject) {
            Set<Map.Entry<String, JsonElement>> entries = this.root.getAsJsonObject().entrySet();
            if (keys) {
                for (Map.Entry<String, JsonElement> entry : entries) {
                    String path = entry.getKey();
                    JsonElement e = entry.getValue();

                    map.put(path, convertElement(e));
                    if (e.isJsonObject()) {
                        JsonObject o = e.getAsJsonObject();
                        map.putAll(getChild(path, true));
                    }
                }
            } else {
                for (Map.Entry<String, JsonElement> entry : entries) {
                    JsonElement e = entry.getValue();
                    map.put(entry.getKey(), convertElement(e));
                }
            }
        }

        return map;
    }

    @Override
    public HashMap<String, Object> getChild(String parent, boolean keys) {
        HashMap<String, Object> map = new HashMap<>();

        Set<Map.Entry<String, JsonElement>> entries = getEntries(parent);
        if (keys){
            for (Map.Entry<String, JsonElement> entry : entries){
                String path = parent + "." + entry.getKey();
                JsonElement e = entry.getValue();

                map.put(path, convertElement(e));
                if (e.isJsonObject()){
                    JsonObject o = e.getAsJsonObject();
                    map.putAll(getChild(path, true));
                }
            }
        }else{
            for (Map.Entry<String, JsonElement> entry : entries){
                JsonElement e = entry.getValue();
                map.put(entry.getKey(), convertElement(e));
            }
        }

        return map;
    }

    //----------------------------------------

    /**
     * Convert JsonElement into JsonObject, Primitive Type, JsonNull or LinkedList<Object>
     *
     * @param element JsonElement that will converted
     *
     * @return JsonObject, Primitive Type, JsonNull or LinkedList<Object>
     * **/
    public Object convertElement(final JsonElement element){
        if (element.isJsonObject()){
            return element.getAsJsonObject();
        }else if (element.isJsonPrimitive()){
            return elementToPrimitive(element);
        }else if (element.isJsonNull()){
            return element.getAsJsonNull();
        }else if (element.isJsonArray()){
            return elementToArray(element);
        }

        return element;
    }

    /**
     * Convert JsonElement into primitive type like int, String, double, etc.
     *
     * @param element JsonElement that will convert into primitive type
     *
     * @return conversion value from JsonElement to primitive type
     * **/
    public Object elementToPrimitive(final JsonElement element){
        if (element.isJsonPrimitive()){
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()){
                return primitive.getAsBoolean();
            }else if (primitive.isString()){
                return primitive.getAsString();
            } else if (primitive.isNumber()){
                Number num = primitive.getAsNumber();
                String n = num.toString();
                if (n.contains(".")){
                    return num.doubleValue();
                }else{
                    return num.intValue();
                }
            }
        }
        return null;
    }

    /**
     * Convert JsonElement into LinkedList<Object>
     *
     * @param element JsonElement that will converted into LinkedList
     *
     * @return JsonArray's LinkedList
     * **/
    public LinkedList<Object> elementToArray(JsonElement element){
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            LinkedList<Object> obj = new LinkedList<>();
            for (Iterator<JsonElement> it = array.iterator(); it.hasNext(); ) {
                JsonElement e = it.next();

                obj.add(convertElement(e));
            }
            return obj;
        }
        return null;
    }

    //----------------------------------------

    public boolean isCharacter(Object o){
        return o instanceof Character;
    }

    public boolean isBoolean(Object o){
        return o instanceof Boolean;
    }

    public boolean isString(Object o){
        return o instanceof String;
    }

    public boolean isNumber(Object o){
        return o instanceof Number;
    }

    //----------------------------------------

    public JsonElement getRoot() {
        return root;
    }

    public GsonBuilder getPrettyBuilder() {
        return prettyBuilder;
    }

    public GsonBuilder getBuilder() {
        return builder;
    }

    public Gson getPrettyGson() {
        return prettyGson;
    }

    public Gson getGson() {
        return gson;
    }

    public boolean isRootIsJsonObject() {
        return rootIsJsonObject;
    }
}
