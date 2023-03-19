package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.SectionUtils;
import me.epic.betteritemconfig.Utils;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CustomEffectHandler implements ItemHandler {
    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        List<PotionEffect> effectList = new ArrayList<>();
        ConfigurationSection effectSection = SectionUtils.first(section, "effect", "effects");
        if (effectSection != null ) {
            for (String key : effectSection.getKeys(false)) {
                ConfigurationSection effect = effectSection.getConfigurationSection(key);
                boolean ambient = effect.getBoolean("ambient", true);
                boolean particles = effect.getBoolean("particles", true);
                boolean icon = effect.getBoolean("icon", true);
                int duration = effect.getInt("duration") * 20;
                int amplifier = effect.getInt("amplifier") - 1;
                PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.minecraft(key));
                effectList.add(new PotionEffect(effectType, duration, amplifier, ambient, particles, icon));
            }
            builder.potionEffects(effectList);
        }
        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta potionMeta) {
            if (potionMeta.hasCustomEffects()) {
                ConfigurationSection potionSection =  section.createSection("effects");
                for (PotionEffect customEffect : potionMeta.getCustomEffects()) {
                    ConfigurationSection customEffectSection = potionSection.createSection(customEffect.getType().getKey().getKey());
                    if (!customEffect.isAmbient()) customEffectSection.set("ambient", false);
                    if (!customEffect.hasParticles()) customEffectSection.set("particles", false);
                    if (!customEffect.hasIcon()) customEffectSection.set("icon", false);
                    customEffectSection.set("amplifier", customEffect.getAmplifier() + 1);
                    customEffectSection.set("duration", customEffect.getDuration() / 20);
                }
            }
            if (!potionMeta.hasColor()) return;
            section.set("dye", Utils.getMetaColor(potionMeta));
        }

    }
}
