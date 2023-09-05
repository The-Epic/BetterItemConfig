package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModelDataHandler implements ItemHandler {
    @Override
    public ItemBuilder process(ItemBuilder builder, ConfigurationSection section) {
        if (section.contains("model-data")) {
            builder.customModelData(section.getInt("model-data", 0));
            return builder;
        }
        return builder;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasCustomModelData()) section.set("model-data", meta.getCustomModelData());
    }
}
