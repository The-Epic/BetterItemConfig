package xyz.epicebic.betteritemconfig.handlers.impl;

import xyz.epicebic.betteritemconfig.ItemBuilder;
import xyz.epicebic.betteritemconfig.SectionUtils;
import xyz.epicebic.betteritemconfig.Utils;
import xyz.epicebic.betteritemconfig.handlers.ItemHandler;
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
    public ItemBuilder process(ItemBuilder builder, ConfigurationSection section) {
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
        return builder;
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
