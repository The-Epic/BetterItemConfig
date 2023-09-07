package xyz.epicebic.betteritemconfig.handlers.impl;

import xyz.epicebic.betteritemconfig.ItemBuilder;
import xyz.epicebic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class NameHandler implements ItemHandler {
    @Override
    public ItemBuilder process(ItemBuilder builder, ConfigurationSection section) {
        if (section.contains("name")) builder.name(section.getString("name"));

        return builder;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            section.set("name", item.getItemMeta().getDisplayName().replace('§', '&'));
    }
}
