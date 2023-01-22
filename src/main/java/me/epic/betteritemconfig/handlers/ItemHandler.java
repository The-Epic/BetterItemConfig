package me.epic.betteritemconfig.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface ItemHandler {

    default boolean doesSupportRead() {
        return true;
    }

    default boolean doesSupportWrite() {
        return true;
    }

    ItemStack process(ItemStack stack, ConfigurationSection section);
    void write(ItemStack item, ConfigurationSection section);
}
