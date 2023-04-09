package me.epic.betteritemconfig;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import me.epic.betteritemconfig.exceptions.PluginNotFoundException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.NBTBase;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"unchecked", "deprecation"})
public class BetterItemConfig {


    public static void toConfig(ConfigurationSection configurationSection, ItemStack toSerialize) {
        NBTItem itemNBT = new NBTItem(toSerialize);
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
                    Map<String, String> potionEffectValueMap = new HashMap<>();
                    for (PotionEffect effect : potionMeta.getCustomEffects()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(effect.getAmplifier());
                        builder.append(" ; ");
                        builder.append(effect.getDuration());
                        potionEffectValueMap.put(effect.toString().toUpperCase(Locale.ROOT), builder.toString());
                    }
                    configurationSection.set("effects", potionEffectValueMap);
                }
                if (itemNBT.hasNBTData()) {
                    configurationSection.set("effects", itemNBT.getString("Potion"));
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
            } else if (itemMeta instanceof BookMeta bookMeta) {
                if (bookMeta.hasTitle()) configurationSection.set("book-info.title", bookMeta.getTitle().replace('§', '&'));
                if (bookMeta.hasAuthor()) configurationSection.set("book-info.author", bookMeta.getAuthor().replace('§', '&'));
                if (bookMeta.hasPages()) {
                    List<String> pages = new ArrayList<>();
                    for (String compound : itemNBT.getStringList("pages")) {
                        // System.out.println(compound);
                        //pages.add(Format.formatBookPage(compound));
                    }
                    configurationSection.set("book-info.pages", pages);
                }
                if (bookMeta.hasGeneration()) configurationSection.set("book-info.generation", bookMeta.getGeneration().toString());
            }
            if (!itemMeta.getPersistentDataContainer().getKeys().isEmpty()) {
                PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                Map<String, NBTBase> stringNBTBaseMap = new HashMap<>();
                stringNBTBaseMap.putAll(Utils.getCustomDataTags(pdc));
                stringNBTBaseMap.forEach((key, value) -> {
                    configurationSection.set("pdc." + key + ".type", value.getClass().getSimpleName().toUpperCase(Locale.ROOT).replace("NBTTAG", ""));
                    configurationSection.set("pdc." + key + ".value", Utils.convertToCorrectType(value.toString(), value.getClass().getSimpleName().replace("NBTTag", "")));
                });

            }
        }
    }

    public static ItemStack fromConfig(FileConfiguration configuration, String path) {
        return fromConfig(configuration.getConfigurationSection(path));
    }

    public static ItemStack fromConfig(ConfigurationSection section) {
        Map<String, Object> itemMap = section.getValues(true);
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
            if (section.isConfigurationSection("effects")) {
                List<PotionEffect> potionEffects = new ArrayList<>();
                for (Map.Entry entry : ((MemorySection) itemMap.get("effects")).getValues(true).entrySet()) {
                    // System.out.println("key = " + entry.getKey() + " --- value = " + entry.getValue());
                    // System.out.println("dur = " + Integer.valueOf(Arrays.stream(entry.getValue().toString().split(";")).toList().get(1) + "amp = " + Integer.valueOf(Arrays.stream(entry.getValue().toString().split(";")).toList().get(0))));
                    potionEffects.add(new PotionEffect(
                            PotionEffectType.getByName(((String) entry.getKey()).toUpperCase(Locale.ROOT)),
                            Integer.valueOf(Arrays.stream(entry.getValue().toString().split(";")).toList().get(1)),
                            Integer.valueOf(Arrays.stream(entry.getValue().toString().split(";")).toList().get(0))));
                }
                builder.potionEffects(potionEffects);
            } else {
                builder.basePotionEffect((String) itemMap.get("effects"));
            }
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
        if (itemMap.containsKey("pdc")) {
            ConfigurationSection pdcSection = section.getConfigurationSection("pdc");
            for (String value : pdcSection.getKeys(false)) {
                ConfigurationSection pdcInfoSection = section.getConfigurationSection("pdc." + value);
                Plugin plugin = Bukkit.getPluginManager().getPlugin(Arrays.stream(value.split(":")).toList().get(0));
                if (plugin == null) {
                    throw new PluginNotFoundException("Plugin: :\"" + Arrays.stream(value.split(":")).toList().get(0) + "\" was not Found");
                }
                NamespacedKey key = new NamespacedKey(plugin, Arrays.stream(value.split(":")).toList().get(1));
                builder.persistentData(key,
                        Utils.getPDT(Utils.convertToCorrectType(pdcInfoSection.getString("value"), pdcInfoSection.getString("type"))),
                        Utils.convertToCorrectType(pdcInfoSection.getString("value").replace("\"", ""), pdcInfoSection.getString("type")));
            }
        }
        if (itemMap.containsKey("book-info")) {
            ConfigurationSection bookSection = section.getConfigurationSection("book-info");
            if (bookSection.contains("author")) builder.author(bookSection.getString("author"));
            if (bookSection.contains("generation")) builder.generation(BookMeta.Generation.valueOf(bookSection.getString("generation")));
            if (bookSection.contains("title")) builder.title(bookSection.getString("title"));
            if (bookSection.contains("pages")) {
                //builder.pages(bookSection.getStringList("pages"));
            }
        }


        return builder.build();
    }
}
