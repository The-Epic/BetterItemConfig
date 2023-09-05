package me.epic.betteritemconfig.handlers;

import me.epic.betteritemconfig.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface ItemHandler {

    default boolean doesSupportRead() {
        return true;
    }

    default boolean doesSupportWrite() {
        return true;
    }

    ItemBuilder process(ItemBuilder builder, ConfigurationSection section);
    void write(ItemStack item, ConfigurationSection section);
}
