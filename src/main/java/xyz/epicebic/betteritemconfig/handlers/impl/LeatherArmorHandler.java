package xyz.epicebic.betteritemconfig.handlers.impl;

import xyz.epicebic.betteritemconfig.ItemBuilder;
import xyz.epicebic.betteritemconfig.Utils;
import xyz.epicebic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorHandler implements ItemHandler {
    @Override
    public ItemBuilder process(ItemBuilder builder, ConfigurationSection section) {
        if (!section.contains("dye")) return builder;
        builder.colour(section.getString("dye"));

        return builder;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta && leatherArmorMeta.getColor() != Bukkit.getItemFactory().getDefaultLeatherColor()) {
            section.set("dye", Utils.getMetaColor(item.getItemMeta()));
        }
    }
}
