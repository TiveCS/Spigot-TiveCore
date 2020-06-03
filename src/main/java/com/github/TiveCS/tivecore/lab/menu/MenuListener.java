package com.github.tivecs.tivecore.lab.menu;

import com.github.tivecs.tivecore.lab.language.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class MenuListener implements Listener {

    private JavaPlugin plugin;

    private HashMap<UUID, ActiveMenu> activeMenu = new HashMap<>();
    private HashMap<String, Menu> registeredMenu = new HashMap<>();

    public MenuListener(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //----------------------------------------------

    /*@EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        UUID user = event.getPlayer().getUniqueId();
        ActiveMenu menu = activeMenu.get(user);
        if (menu != null && menu.getInventory().equals(event.getInventory())){
            activeMenu.remove(user);
        }
    }*/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        UUID user = event.getWhoClicked().getUniqueId();
        ActiveMenu menu = activeMenu.getOrDefault(user, null);
        if (menu != null && menu.getInventory().equals(event.getInventory())){
            menu.getMenu().action(menu, event);
        }
    }

    //----------------------------------------------

    public void registerMenu(Menu... menu){
        for (Menu m : menu){
            m.saveData();
            m.getConfig().readData();

            m.initializeData();
            m.setIsRegistered(true);
            registeredMenu.put(m.getMenuId(), m);
        }
    }

    public void open(String menuId, int page, Player... players){
        open(menuId, null, page, players);
    }
    public void open(String menuId, Placeholder placeholder, int page, Player... players){
        Menu m = registeredMenu.get(menuId);
        open(m, placeholder, page, players);
    }
    public void open(Menu menu, Placeholder placeholder, int page, Player... players){
        if (menu != null){
            for (Player p : players){
                UUID u = p.getUniqueId();
                ActiveMenu a = activeMenu.getOrDefault(u, null);
                if (a != null && !a.getMenu().equals(menu)){
                    activeMenu.remove(u);
                }
            }
            ActiveMenu active = menu.open(this, placeholder, page, players);

            for (Player p : players){
                activeMenu.put(p.getUniqueId(), active);
            }
        }
    }

    public void freeOpen(Menu menu, int page, Player... players){
        freeOpen(menu, null, page, players);
    }
    public void freeOpen(Menu menu, Placeholder placeholder, int page, Player... players){
        if (menu != null){
            ActiveMenu active = Menu.freeOpen(menu, this, placeholder, page, players);
            for (Player p : players){
                activeMenu.put(p.getUniqueId(), active);
            }
        }
    }

    public HashMap<String, Menu> getRegisteredMenu() {
        return registeredMenu;
    }

    public HashMap<UUID, ActiveMenu> getActiveMenu() {
        return activeMenu;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
