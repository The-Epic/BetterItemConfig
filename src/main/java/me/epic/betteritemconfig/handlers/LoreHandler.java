package me.epic.betteritemconfig.handlers;

import me.epic.betteritemconfig.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreHandler implements ItemHandler {

    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        if (section.contains("lore")) builder.lore(section.getStringList("lore"));

        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.getItemMeta().hasLore()) {
            section.set("lore", item.getItemMeta().getLore());
        }
    }
}
