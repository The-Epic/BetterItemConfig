package me.epic.betteritemconfig.handlers;

import me.epic.betteritemconfig.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class NameHandler implements ItemHandler{
    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        if (section.contains("name")) builder.name(section.getString("name"));

        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.getItemMeta().hasDisplayName()) section.set("name", item.getItemMeta().getDisplayName().replace('§', '&'));
    }
}
