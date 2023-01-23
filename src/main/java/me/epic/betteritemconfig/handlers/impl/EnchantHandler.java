package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.SectionUtils;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnchantHandler implements ItemHandler {

    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ConfigurationSection enchantSection = SectionUtils.first(section, "enchant", "enchants", "enchantment", "enchantments");
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        if (enchantSection != null) {
            Map<Enchantment, Integer> enchantmentLevelMap = new HashMap<>();
            for (String key : enchantSection.getKeys(false)) {
                enchantmentLevelMap.put(Enchantment.getByKey(NamespacedKey.minecraft(key)), enchantSection.getInt(key));
            }
            builder.enchantments(enchantmentLevelMap);
        }
        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            Map<String, Integer> enchantLevelMap = new HashMap<>();
            for (Map.Entry entry : item.getItemMeta().getEnchants().entrySet())
                enchantLevelMap.put(((Enchantment) entry.getKey()).getKey().getKey(), (Integer) entry.getValue());

            section.set("enchants", enchantLevelMap);
        }
    }
}
