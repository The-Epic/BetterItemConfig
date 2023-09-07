package xyz.epicebic.betteritemconfig.handlers.impl;

import xyz.epicebic.betteritemconfig.ItemBuilder;
import xyz.epicebic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LoreHandler implements ItemHandler {

    @Override
    public ItemBuilder process(ItemBuilder builder, ConfigurationSection section) {
        if (section.contains("lore")) builder.lore(section.getStringList("lore"));

        return builder;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            section.set("lore", item.getItemMeta().getLore());
        }
    }
}
