package com.github.tivecs.tivecore.lab.language;

import com.github.tivecs.tivecore.lab.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Placeholder implements Cloneable{

    private HashMap<String, String> replacer = new HashMap<>();
    private HashMap<String, List<String>> replacerList = new HashMap<>();

    public Placeholder(){ }

    //----------------------------------

    public void addReplacer(String key, String replacer){
        getReplacer().put(key, replacer);
    }

    public void addReplacer(String key, List<String> replacer){
        getReplacerList().put(key, replacer);
    }

    //----------------------------------

    public String use(String s){

        for (String p : replacer.keySet()){
            String sign = "%" + p + "%";
            int index = s.indexOf(sign), signLength = sign.length();

            if (index != -1){
                s = s.replace(sign, replacer.get(p));
            }
        }

        return s;
    }
    public String useV2(String s){
        HashSet<String> used = searchUsedPlaceholder(s);
        for (String plc : used){
            s = s.replace(new StringBuilder().append("%").append(plc).append("%").toString(), replacer.get(plc));
        }

        return s;
    }

    public List<String> use(List<String> list){
        int line = 0;
        for (int i = 0; i < list.size(); i++){
            String s = list.get(i);
            for (String p : replacer.keySet()){
                String sign = "%" + p + "%";
                int index = s.indexOf(sign), signLength = sign.length();

                if (index != -1){
                    s = s.replace(sign, replacer.get(p));
                }
            }
            list.set(i, s);

            line++;
        }
        return list;
    }
    public List<String> useV2(List<String> text){

        text = new ArrayList<>(text);
        HashSet<String> used = new HashSet<>();
        used.addAll(searchUsedPlaceholder(text));

        int lineSize = text.size();
        for (int line = 0; line < lineSize; line++){
            String s = text.get(line);

            for (String key : used){
                final String sign = new StringBuilder().append("%").append(key).append("%").toString();
                final int firstLength = s.indexOf(sign) + sign.length();

                if (replacer.get(key) != null){
                    s = s.replace(sign, replacer.get(key));
                    text.set(line, s);
                }else if (replacerList.get(key) != null) {
                    List<String> rp = new ArrayList<>(replacerList.get(key));
                    int replacerSize = rp.size();

                    if (replacerSize > 0) {
                        if (replacerSize == 1) {
                            s = s.replace(sign, rp.get(0));
                            text.set(line, s);
                        } else if (s.contains(sign)) {
                            String first = s.substring(0, firstLength),
                                    last = s.substring(firstLength);

                            s = first.replace(sign, rp.get(0));
                            text.set(line, s);

                            rp.remove(0);
                            int lastIndex = rp.size() - 1;
                            rp.set(lastIndex, new StringBuilder().append(rp.get(lastIndex)).append(last).toString());
                            text = StringUtils.insertString(text, line, rp);

                            lineSize = text.size();
                        }
                    }
                }
            }

        }

        return StringUtils.colored(text);
    } // DONE ?

    public ItemStack use(ItemStack item){

        ItemMeta meta = item.getItemMeta();

        if (meta.hasDisplayName()){
            meta.setDisplayName(use(meta.getDisplayName()));
        }
        if (meta.hasLore()){
            meta.setLore(use(meta.getLore()));
        }

        item.setItemMeta(meta);

        return item;
    }
    public ItemStack useV2(ItemStack item){

        ItemMeta meta = item.getItemMeta();

        if (meta.hasDisplayName()){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', useV2(meta.getDisplayName())));
        }
        if (meta.hasLore()){
            meta.setLore(useV2(meta.getLore()));
        }

        item.setItemMeta(meta);

        return item;
    }

    //----------------------------------

    public void merge(Placeholder otherPlaceholder){
        this.replacer.putAll(otherPlaceholder.replacer);
        this.replacerList.putAll(otherPlaceholder.replacerList);
    }

    //----------------------------------

    public HashSet<String> searchUsedPlaceholderCustom(final String data, final HashSet<String> keys){
        HashSet<String> used = new HashSet<>();

        final List<Integer> l = StringUtils.searchNormalCharLocation(data, '%');

        for (int i = 0; i < l.size() - 1; i++){
            final int a = l.get(i), b = l.get(i + 1);

            final String s = data.substring(a + 1, b);
            if (keys.contains(s)){
                used.add(s);
            }
        }

        return used;
    }

    public HashSet<String> searchUsedPlaceholderCustom(final List<String> data, final HashSet<String> keys){
        HashSet<String> used = new HashSet<>();

        HashMap<Integer, List<Integer>> l = StringUtils.searchNormalCharLocation(data, '%');
        for (int line = 0; line < data.size(); line++){
            final List<Integer> loc = l.get(line);
            final String text = data.get(line);

            for (int i = 0; i < loc.size() - 1; i++){
                final int a = loc.get(i), b = loc.get(i + 1);
                final String s = text.substring(a + 1, b);

                if (keys.contains(s)){
                    used.add(s);
                }
            }
        }

        return used;
    }

    public HashSet<String> searchUsedPlaceholder(final String data){
        HashSet<String> used = new HashSet<>();

        List<Integer> l = StringUtils.searchNormalCharLocation(data, '%');

        for (int i = 0; i < l.size() - 1; i++){
            final int a = l.get(i), b = l.get(i + 1);

            final String s = data.substring(a + 1, b);
            if (replacer.containsKey(s)){
                used.add(s);
            }
        }

        return used;
    }

    public HashSet<String> searchUsedPlaceholder(final List<String> data){
        HashSet<String> used = new HashSet<>(), plc = new HashSet<>();

        plc.addAll(replacer.keySet());
        plc.addAll(replacerList.keySet());

        used.addAll(searchUsedPlaceholderCustom(data, plc));

        return used;
    }

    public HashSet<String> searchUsedPlaceholderList(final List<String> data){
        HashSet<String> used = new HashSet<>(), plc = new HashSet<>(replacerList.keySet());

        used.addAll(searchUsedPlaceholderCustom(data, plc));

        return used;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    //----------------------------------

    public HashMap<String, String> getReplacer() {
        return replacer;
    }

    public HashMap<String, List<String>> getReplacerList() {
        return replacerList;
    }
}
