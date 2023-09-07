package xyz.epicebic.betteritemconfig.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface BaseProcessor {
    ItemStack read(ConfigurationSection section);
    void write(ItemStack stack, ConfigurationSection section);
}
