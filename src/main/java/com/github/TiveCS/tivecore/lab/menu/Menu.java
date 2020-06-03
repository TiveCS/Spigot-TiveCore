package com.github.tivecs.tivecore.lab.menu;

import com.github.tivecs.tivecore.lab.language.Placeholder;
import com.github.tivecs.tivecore.lab.storage.yaml.ConfigManager;
import com.github.tivecs.tivecore.lab.utils.StringUtils;
import com.github.tivecs.tivecore.lab.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Menu implements Cloneable{

    private ConfigManager config = null;

    private File folder;
    private int row = 3;
    private String title, menuId;

    protected boolean isRegistered;
    private Placeholder placeholder;

    private HashMap<String, Integer> itemMinPage = new HashMap<>(), itemMaxPage = new HashMap<>();
    private HashMap<String, ItemStack> itemData = new HashMap<>();
    private HashMap<String, List<Integer>> slotData = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ItemStack>> pageData = new HashMap<>();

    //-------------------------------------------

    public Menu(final File folder, final String menuId, final String title, final int row){
        this.menuId = menuId;
        this.title = title;
        this.row = row;
        this.folder = folder;
        this.placeholder = new Placeholder();

        this.config = new ConfigManager(new File(folder, getClass().getSimpleName() + ".yml"));

        config.directSetIfNotExists("title", this.title);
        config.directSetIfNotExists("row", this.row);
    }

    public Menu(final File folder, final String menuId, final String title){
        this(folder, menuId, title, 3);
    }

    //-------------------------------------------

    public abstract void action(ActiveMenu activeMenu, InventoryClickEvent event);

    public static ActiveMenu freeOpen(Menu menu, MenuListener listener, int page, Player... players){
        return freeOpen(menu, listener, null, page, players);
    }
    public static ActiveMenu freeOpen(Menu menu, MenuListener listener, Placeholder placeholder, int page, Player... players){
        ActiveMenu active = null;
        for (Player p : players){
            active = listener.getActiveMenu().get(p.getUniqueId());
            if (active != null){
                break;
            }
        }
        if (active == null){
            active = new ActiveMenu(listener, menu, page);
        }
        active.setCurrentPage(page, placeholder);
        active.open(players);

        return active;
    }

    public ActiveMenu open(MenuListener listener, int page, Player... players){
        return open(listener, null, page, players);
    }
    public ActiveMenu open(MenuListener listener, Placeholder placeholder, int page, Player... players){
        ActiveMenu active = null;
        for (Player p : players){
            active = listener.getActiveMenu().get(p.getUniqueId());
            if (active != null){
                break;
            }
        }
        if (active == null){
            active = new ActiveMenu(listener,this, page);
        }
        active.setCurrentPage(page, placeholder);
        active.open(players);
        return active;
    }

    //-------------------------------------------

    public void initializeData(){
        this.title = config.getData().get("title").toString();
        this.row = (int) config.getData().get("row");
        int maxSize = this.row * 9;

                itemData.clear(); itemMinPage.clear(); itemMaxPage.clear();
        slotData.clear();
        pageData.clear();

        List<String> childs = new ArrayList<>(config.getChildPath("menu-data", false));
        for (String c : childs){
            String path = "menu-data." + c;

            String displayName = (String) config.getData().getOrDefault(path + ".display-name", null);
            XMaterial material = XMaterial.matchXMaterial(config.getData().getOrDefault(path + ".material", "STONE").toString()).get();
            int amount = (int) config.getData().getOrDefault(path + ".amount", 1);
            List<String> lore = (List<String>) config.getData().getOrDefault(path + ".lore", new ArrayList<>());

            ItemStack item = material.parseItem();
            ItemMeta meta = item.getItemMeta();

            meta.setLore(StringUtils.colored(lore));
            if (displayName != null){
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }
            item.setItemMeta(meta);
            item.setAmount(amount);

            String slotString = config.getData().get(path + ".slots").toString().replace(" ", "");
            List<Integer> slots = new ArrayList<>();
            if (slotString != null && slotString.length() > 0) {
                if (slotString.contains(",")) {
                    String[] sl = slotString.split(",");
                    for (String s : sl) {
                        int slot = Integer.parseInt(s);
                        if (slot < maxSize){
                            slots.add(slot);
                        }else{
                            Bukkit.getLogger().warning("Cannot load slot " + slot + " for menu class " + this.getClass().getSimpleName());
                        }
                    }
                }else{
                    int slot = Integer.parseInt(slotString);
                    if (slot < maxSize){
                        slots.add(slot);
                    }else{
                        Bukkit.getLogger().warning("Cannot load slot " + slot + " for menu class " + this.getClass().getSimpleName());
                    }
                }
            }

            int minPage = (int) config.getData().getOrDefault(path + ".min-page", 0),
                    maxPage = (int) config.getData().getOrDefault(path + ".max-page", 0);

            itemMinPage.put(c, minPage); itemMaxPage.put(c, maxPage);
            itemData.put(c, item);
            slotData.put(c, slots);

            HashMap<Integer, ItemStack> pd;
            if (minPage == 0 && minPage == maxPage){
                pd = pageData.getOrDefault(0, new HashMap<>());
                for (int s : slots){
                    pd.put(s, item);
                }
                pageData.put(0, pd);
            } else{
                for (;minPage <= maxPage; minPage++){
                    pd = pageData.getOrDefault(minPage, new HashMap<>());
                    for (int s : slots){
                        pd.put(s, item);
                    }
                    pageData.put(minPage, pd);
                }
            }
        }
    }

    public void saveData(){
        config.saveData();
    }

    //-------------------------------------------

    public void addItem(String path, XMaterial material, int minPage, int maxPage, int[] slots){
        addItem(path, material, null, minPage, maxPage, slots);
    }

    public void addItem(String path, XMaterial material, String itemName, int minPage, int maxPage, int[] slots){
        addItem(path, material, itemName, 1, minPage, maxPage, slots);
    }

    public void addItem(String path, XMaterial material, String itemName, int amount, int minPage, int maxPage, int[] slots){
        addItem(path, material, itemName, amount, null, minPage, maxPage, slots);
    }

    public void addItem(String path, XMaterial material, String itemName, int amount, List<String> lore, int minPage, int maxPage, int[] slots){
        if (config != null){
            String fmp = path.startsWith("menu-data") ? path : "menu-data." + path;
            StringBuilder sl = new StringBuilder();
            if (slots.length > 0) {
                for (int i = 0; i < slots.length; i++) {
                    int s = slots[i];
                    sl.append(s);
                    if (i + 1 < slots.length){
                        sl.append(",");
                    }
                }
            }

            config.directSetIfNotExists(fmp + ".material", material.name());
            config.directSetIfNotExists(fmp + ".display-name", itemName);
            config.directSetIfNotExists(fmp + ".amount", amount);
            config.directSetIfNotExists(fmp + ".min-page", minPage);
            config.directSetIfNotExists(fmp + ".max-page", maxPage);
            config.directSetIfNotExists(fmp + ".slots", sl.toString());

            if (lore != null){
                config.directSetIfNotExists(fmp + ".lore", lore);
            }
        }
    }

    //-------------------------------------------

    protected void setIsRegistered(boolean b) {
        isRegistered = b;
    }

    public List<String> searchUsedItemOnPage(int page){
        List<String> used = new ArrayList<>();
        for (String s : itemMinPage.keySet()){
            int min = itemMinPage.get(s), max = itemMaxPage.get(s);

            if (!(min == 0 && max == 0)) {
                if ((min > 0 && max > 0) && (min <= page && max >= page)) {
                    used.add(s);
                } else if (min == 0 && max >= page) {
                    used.add(s);
                } else if (max == 0 && min <= page) {
                    used.add(s);
                }
            }
        }

        return used;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    //-------------------------------------------

    public int getRow() {
        return row;
    }

    public File getFolder() {
        return folder;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getTitle() {
        return title;
    }

    public ConfigManager getConfig() {
        return config;
    }

    public HashMap<String, ItemStack> getItemData() {
        return itemData;
    }

    public HashMap<String, List<Integer>> getSlotData() {
        return slotData;
    }

    public HashMap<Integer, HashMap<Integer, ItemStack>> getPageData() {
        return pageData;
    }

    public HashMap<String, Integer> getItemMaxPage() {
        return itemMaxPage;
    }

    public HashMap<String, Integer> getItemMinPage() {
        return itemMinPage;
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }
}
