package com.github.tivecs.tivecore.lab.menu;

import com.github.tivecs.tivecore.lab.language.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveMenu {

    private int currentPage;
    private MenuListener listener;
    private Menu menu;
    private Inventory inventory = null;

    public ActiveMenu(MenuListener listener, Menu menu, int initialPage){
        this.menu = menu;
        this.listener = listener;
        this.currentPage = initialPage;
    }

    public ActiveMenu(MenuListener listener, Menu menu){
        this(listener, menu, 1);
    }

    //-----------------------------------------

    public void loadPageData(int page){
        loadPageData(page, null);
    }
    public void loadPageData(int page, Placeholder placeholder){
        boolean hasPlaceholder = placeholder != null;
        if (this.inventory == null) {
            String title = menu.getTitle();
            if (hasPlaceholder){
                title = placeholder.useV2(title);
            }
            title = ChatColor.translateAlternateColorCodes('&', title);
            this.inventory = Bukkit.createInventory(null, menu.getRow() * 9, title);
        }
        this.inventory.clear();
        HashMap<Integer, ItemStack> pageData = getPageDataOnPage(page);

        for (int slot : pageData.keySet()){
            ItemStack item = pageData.get(slot).clone();
            if (hasPlaceholder){
                item = placeholder.useV2(item);
            }
            this.inventory.setItem(slot, item);
        }
    }

    public HashMap<Integer, ItemStack> getPageDataOnPage(int page){
        HashMap<Integer, ItemStack> map = new HashMap<>(),
                current = getMenu().getPageData().getOrDefault(page, null),
                template = getMenu().getPageData().getOrDefault(0, new HashMap<>());

        if (current == null){
            current = new HashMap<>();
            List<String> l = menu.searchUsedItemOnPage(page);

            for (String u : l){
                if (template.containsValue(u)){
                    continue;
                }
                List<Integer> slots = menu.getSlotData().getOrDefault(u, new ArrayList<>());
                ItemStack item = menu.getItemData().get(u);
                for (int slot : slots){
                    current.put(slot, item);
                }
            }
        }

        map.putAll(template);
        map.putAll(current);

        return map;
    }

    public void open(Player... players){
        for (Player p : players){
            if (p.getOpenInventory().getTopInventory() != this.inventory) {
                p.closeInventory();
                p.openInventory(this.inventory);
            }
        }
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        loadPageData(this.currentPage, null);
    }
    public void setCurrentPage(int currentPage, Placeholder placeholder) {
        this.currentPage = currentPage;
        loadPageData(this.currentPage, placeholder);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public MenuListener getListener() {
        return listener;
    }
}
