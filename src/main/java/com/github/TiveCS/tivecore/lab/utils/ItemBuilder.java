package com.github.tivecs.tivecore.lab.utils;

import com.github.tivecs.tivecore.lab.language.Placeholder;
import com.github.tivecs.tivecore.lab.utils.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class ItemBuilder {

    public static ItemStack createItem(@Nonnull ConfigurationSection config, Placeholder placeholder){
        ItemStack item = null;

        Optional<XMaterial> get = XMaterial.matchXMaterial(config.getString("material"));
        if (get.isPresent()){
            item = get.get().parseItem();

            String displayName = config.getString("display-name", null);
            int amount = config.getInt("amount", 1);
            List<String> lore = StringUtils.colored(config.getStringList("lore"));

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName != null ? ChatColor.translateAlternateColorCodes('&', displayName) : null);
            meta.setLore(!lore.isEmpty() ? lore : null);
            item.setItemMeta(meta);

            item.setAmount(amount);
            if (placeholder != null) {
                item = placeholder.useV2(item);
            }
        }

        return item;
    }

    public static ItemStack createItem(@Nonnull ConfigurationSection config){
        return createItem(config, null);
    }

}
