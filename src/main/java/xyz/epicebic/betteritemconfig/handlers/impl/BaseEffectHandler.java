package xyz.epicebic.betteritemconfig.handlers.impl;

import xyz.epicebic.betteritemconfig.ItemBuilder;
import xyz.epicebic.betteritemconfig.SectionUtils;
import xyz.epicebic.betteritemconfig.Utils;
import xyz.epicebic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class BaseEffectHandler implements ItemHandler {

    @Override
    public ItemBuilder process(ItemBuilder builder, ConfigurationSection section) {
        ConfigurationSection effectSection = SectionUtils.first(section, "effect", "effects");
        if (effectSection == null) return builder;
        if (effectSection.getKeys(false).isEmpty()) return builder;
        String potionType = effectSection.getKeys(false).toArray(String[]::new)[0];
        ConfigurationSection effect = effectSection.getConfigurationSection(potionType);

        PotionData basePotionData = new PotionData(PotionType.valueOf(potionType), effect.getBoolean("extended", false), effect.getBoolean("upgraded", false));
        builder.basePotionEffect(basePotionData);
        return builder;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (!item.hasItemMeta()) return;
        if (!(item.getItemMeta() instanceof PotionMeta potionMeta)) return;
        ConfigurationSection effectSection = section.createSection("effects");
        PotionData basePotionData = potionMeta.getBasePotionData();
        ConfigurationSection effect = effectSection.createSection(basePotionData.getType().toString());
        effect.set("upgraded", basePotionData.isUpgraded());
        effect.set("extended", basePotionData.isExtended());

        // Colour
        if (!potionMeta.hasColor()) return;
        section.set("dye", Utils.getMetaColor(potionMeta));
    }
}
