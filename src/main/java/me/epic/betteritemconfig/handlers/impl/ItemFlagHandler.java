package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemFlagHandler implements ItemHandler {
    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        if (section.isList("itemflags")) {
            builder.flags(section.getStringList("itemflags").stream().map(ItemFlag::valueOf).toList());
        }
        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            List<String> flags = new ArrayList<>();
            for (ItemFlag flag : ItemFlag.values()) {
                if (meta.hasItemFlag(flag)) {
                    flags.add(flag.toString());
                }
            }
            if (!flags.isEmpty()) {
                section.set("itemflags", flags);
            }
        }
    }
}
