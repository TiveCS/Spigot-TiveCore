package com.github.tivecs.tivecore.menu;

import com.github.tivecs.tivecore.TiveCorePlugin;
import com.github.tivecs.tivecore.lab.menu.ActiveMenu;
import com.github.tivecs.tivecore.lab.menu.Menu;
import com.github.tivecs.tivecore.lab.utils.xseries.XMaterial;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class FileEditorMenu extends Menu {

    private static TiveCorePlugin plugin = TiveCorePlugin.getPlugin(TiveCorePlugin.class);

    public FileEditorMenu() {
        super(new File(plugin.getDataFolder(), "menu"), "core-FileEditor", "&1File Editor", 6);

        addItem("border", XMaterial.BLACK_STAINED_GLASS_PANE, " ", 0,0, new int[]{0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,44, 45,46,47,48,49,50,51,52,53});
        addItem("next-page", XMaterial.LIME_STAINED_GLASS_PANE, "&aNext Page", 0,0, new int[]{51});
        addItem("previous-page", XMaterial.LIME_STAINED_GLASS_PANE, "&aPrevious Page", 2,0, new int[]{47});
    }

    @Override
    public void action(ActiveMenu activeMenu, InventoryClickEvent event) {
        int slot = event.getSlot();

        event.setCancelled(true);
        if (getSlotData().get("next-page").contains(slot)){
            activeMenu.setCurrentPage(activeMenu.getCurrentPage() + 1);
        }else if (activeMenu.getCurrentPage() > 1 && getSlotData().get("previous-page").contains(slot)){
            activeMenu.setCurrentPage(activeMenu.getCurrentPage() - 1);
        }
    }
}
