package me.epic.betteritemconfig.handlers.impl;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.SectionUtils;
import me.epic.betteritemconfig.Utils;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class BaseEffectHandler implements ItemHandler {

    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        ConfigurationSection effectSection = SectionUtils.first(section, "effect", "effects");
        if (effectSection.getKeys(false).isEmpty()) return builder.build();
        for (String key : effectSection.getKeys(false)) {
            if (!effectSection.isConfigurationSection(key)) continue;
            ConfigurationSection effect = effectSection.getConfigurationSection(key);
            if (!effect.contains("upgraded") || !effect.contains("extended")) continue;
            boolean upgraded = effect.getBoolean("upgraded", false);
            boolean extended = effect.getBoolean("extended", false);
            StringBuilder stringBuilder = new StringBuilder();
            if (upgraded) stringBuilder.append("strong_");
            if (extended && !upgraded) stringBuilder.append("long_");
            stringBuilder.append(key);
            builder.basePotionEffect(stringBuilder.toString());
        }
        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (!item.hasItemMeta()) return;
        if (!(item.getItemMeta() instanceof PotionMeta potionMeta)) return;
        NBTItem itemNBT = new NBTItem(item);
        if (!itemNBT.hasNBTData()) return;
        ConfigurationSection effectSection = section.createSection("effects");
        String potion = itemNBT.getString("Potion");
        ConfigurationSection effect = effectSection.createSection(potion.replace("strong_", "").replace("long_", ""));
        if (potion.startsWith("strong_")) {
            effect.set("upgraded", true);
            effect.set("extended", false);
        } else if (potion.startsWith("long_")) {
            effect.set("upgraded", false);
            effect.set("extended", true);
        }

        // Colour
        if (!potionMeta.hasColor()) return;
        section.set("dye", Utils.getMetaColor(potionMeta));
    }
}
