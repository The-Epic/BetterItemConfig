package me.epic.betteritemconfig;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Phaser;

public class BetterItemConfig {

    @Getter
    public static boolean useMiniMessage;

    public static void init(boolean useMiniMessage) {
        BetterItemConfig.useMiniMessage = useMiniMessage;
    }

    public static void toConfig(FileConfiguration configurationFile, String path, ItemStack toSerialize) {
        ConfigurationSection configurationSection = configurationFile.createSection(path);
        configurationSection.set("type", toSerialize.getType().toString());
        configurationSection.set("amount", toSerialize.getAmount());
        if (toSerialize.hasItemMeta()) {
            ItemMeta itemMeta = toSerialize.getItemMeta();
            if (itemMeta.hasDisplayName()) configurationSection.set("name", itemMeta.getDisplayName());
            if (itemMeta.hasEnchants()) {
                Map<String, Integer> enchantmentLevelMap = new HashMap<>();
                for (Map.Entry entry : itemMeta.getEnchants().entrySet()) enchantmentLevelMap.put(((Enchantment)entry.getKey()).getName(), (Integer) entry.getValue());
                configurationSection.set("enchants", enchantmentLevelMap);
            }
            if (itemMeta.hasLore()) {
                List<String> lore = new ArrayList<>();
                for (String string : itemMeta.getLore()) {
                    lore.add(string.replace('§', '&'));
                }
                configurationSection.set("lore", lore);
            }
            if (itemMeta.hasCustomModelData()) configurationSection.set("model-data", itemMeta.getCustomModelData());
            if (itemMeta instanceof SkullMeta skullMeta) {
                GameProfile gameProfile = new GameProfile(skullMeta.getOwningPlayer().getUniqueId(), skullMeta.getOwningPlayer().getName());
                Property texture = (Property) gameProfile.getProperties().get("textures");
                String textureToAdd = texture.getValue();
                if (textureToAdd != null) configurationSection.set("texture", textureToAdd);
            } else if (itemMeta instanceof PotionMeta potionMeta) {
                if (potionMeta.hasCustomEffects()) {
                    Map<PotionEffectType, String> potionEffectValueMap = new HashMap<>();
                    for (PotionEffect effect : potionMeta.getCustomEffects()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(effect.getAmplifier());
                        builder.append(" ; ");
                        builder.append(effect.getDuration());
                        potionEffectValueMap.put(effect.getType(), builder.toString());
                    }
                    configurationSection.set("effect", potionEffectValueMap);
                }
                if (potionMeta.hasColor()) {
                    Color color = potionMeta.getColor();
                    String hex = "#" + Integer.toHexString(color.asRGB()).substring(2);
                    configurationSection.set("dye", hex);
                }
            } else if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                Color color = leatherArmorMeta.getColor();
                String hex = "#" + Integer.toHexString(color.asRGB()).substring(2);
                configurationSection.set("dye", hex);
            }
        }
    }

    public static ItemStack fromConfig(FileConfiguration configuration, String path) {
        Map<String, Object> itemMap = configuration.getConfigurationSection(path).getValues(true);
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(itemMap.get("type").toString().toUpperCase(Locale.ROOT)));
        builder.amount((Integer) itemMap.get("amount"));
        if (itemMap.containsKey("name")) builder.name((String) itemMap.get("name"));
        if (itemMap.containsKey("enchants")) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            for (Map.Entry entry : ((Map<String, Object>)itemMap.get("enchants")).entrySet()) {
                enchantments.put(Enchantment.getByName(((String) entry.getKey()).toUpperCase(Locale.ROOT)), (Integer) entry.getValue());
            }
            builder.enchantments(enchantments);
        }
        if (itemMap.containsKey("lore")) {
            List<String> loreList = (List<String>) itemMap.get("lore");
            builder.lore(loreList);
        }
        if (itemMap.containsKey("effects")) {
            List<PotionEffect> potionEffects = new ArrayList<>();
            for (Map.Entry entry : ((Map<String, String>)itemMap.get("effects")).entrySet()) {
                potionEffects.add(new PotionEffect(
                        PotionEffectType.getByName(((String) entry.getKey()).toUpperCase(Locale.ROOT)),
                        Integer.valueOf(Arrays.stream(entry.getValue().toString().split(";")).toList().get(1)),
                        Integer.valueOf(Arrays.stream(entry.getValue().toString().split(";")).toList().get(0))));
            }
            builder.potionEffects(potionEffects);
        }
        if (itemMap.containsKey("flags")) {
            List<ItemFlag> itemFlags = new ArrayList<>();
            for (String string : (List<String>)itemMap.get("flags")) {
                itemFlags.add(ItemFlag.valueOf(string.toUpperCase(Locale.ROOT)));
            }
            builder.flags(itemFlags);
        }
        if (itemMap.containsKey("model-data")) builder.customModelData((Integer) itemMap.get("model-data"));
        if (itemMap.containsKey("dye")) builder.colour((String) itemMap.get("dye"));
        if (itemMap.containsKey("texture")) builder.skullTexture((String) itemMap.get("texture"));

        return builder.build();
    }
}
