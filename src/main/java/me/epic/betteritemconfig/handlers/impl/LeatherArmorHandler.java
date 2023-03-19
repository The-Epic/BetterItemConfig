package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.Utils;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorHandler implements ItemHandler {
    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        if (!section.contains("dye")) return stack;
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        builder.colour(section.getString("dye"));

        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta && leatherArmorMeta.getColor() != Bukkit.getItemFactory().getDefaultLeatherColor()) {
            section.set("dye", Utils.getMetaColor(item.getItemMeta()));
        }
    }
}
