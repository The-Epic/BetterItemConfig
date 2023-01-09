package me.epic.betteritemconfig;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

@SerializableAs("ItemStack")
public class ItemSerialization implements Cloneable, ConfigurationSerializable {

    private ItemStack stack;
    public ItemSerialization(ItemStack stack) {
        this.stack = stack;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("type", this.stack.getType().toString().toLowerCase(Locale.ROOT));
        result.put("name", this.stack.getItemMeta().getDisplayName() == null ? Format.getNiceMaterialName(this.stack.getType()) : this.stack.getItemMeta().getDisplayName());
        result.put("amount", this.stack.getAmount());
        if (this.stack.getItemMeta().hasEnchants()) {
            Map<String, Integer> mapMap = new HashMap<>();
            for (Map.Entry entry : this.stack.getItemMeta().getEnchants().entrySet()) {
                mapMap.put(((Enchantment)entry.getKey()).getName(), (Integer) entry.getValue());
            }
            result.put("enchants", mapMap);
        }
        if (this.stack.getItemMeta().hasLore()) {
            List<String> loreList = new ArrayList<>();
            for (String loreLine : this.stack.getItemMeta().getLore()) {
                loreList.add(loreLine);
            }
            result.put("lore", loreList);
        }
        if (this.stack.getItemMeta() instanceof SkullMeta skullMeta) {
            GameProfile gameProfile = new GameProfile(skullMeta.getOwningPlayer().getUniqueId(), skullMeta.getOwningPlayer().getName());
            Property texture = (Property) gameProfile.getProperties().get("textures");
            String textureToAdd = texture.getValue();
            result.put("texture", textureToAdd);
        }
        if (this.stack.getItemMeta() instanceof PotionMeta potionMeta) {
            Map<String, String> potionValue = new HashMap<>();
            for (PotionEffect effect : potionMeta.getCustomEffects()) {
                StringBuilder builder = new StringBuilder();
                builder.append(effect.getAmplifier());
                builder.append(" ; ");
                builder.append(effect.getDuration());
                potionValue.put(effect.getType().toString(), builder.toString());
            }
            Color color = potionMeta.getColor();
            String hex = "#" + Integer.toHexString(color.asRGB()).substring(2);
            result.put("dye", hex);
            result.put("effect", potionValue);
        }
        if (!this.stack.getItemMeta().getItemFlags().isEmpty()) {
            List<String> flagValues = new ArrayList<>();
            for (ItemFlag flag : this.stack.getItemMeta().getItemFlags()) {
                flagValues.add(flag.toString());
            }
            result.put("flags", flagValues);
        }
        if (this.stack.getItemMeta().hasCustomModelData()) {
            result.put("model-data", this.stack.getItemMeta().getCustomModelData());
        }
        if (this.stack.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) {
            Color color = leatherArmorMeta.getColor();
            String hex = "#" + Integer.toHexString(color.asRGB()).substring(2);
            result.put("dye", hex);
        }
        return result;
    }

    public static ItemStack deserialize(Map<String, Object> args) {
        Material material = Material.getMaterial(args.get("type").toString().toUpperCase(Locale.ROOT)) == null
                ? Material.BARRIER : Material.getMaterial(args.get("type").toString().toUpperCase(Locale.ROOT))
                ;
        String itemName = args.get("name") == null ? (String) args.get("name") : Format.getNiceMaterialName(material);
        Integer amount = args.get("amount") == null ? (Integer) args.get("amount") : 1;
        Map<Enchantment, Integer> enchantmentLevelMap;
        if (args.get("enchants") == null) {
            enchantmentLevelMap = new HashMap<>();
        } else {
            enchantmentLevelMap = new HashMap<>();
            ((MemorySection) args.get("enchants")).getValues(true).forEach((key, value) ->
                    enchantmentLevelMap.put(EnchantmentConversion.getEnchantment(key), (Integer) value));
        }
        List<String> lore = ((List<String>) args.get("lore")).isEmpty() || args.get("lore") == null ? new ArrayList<>() : (List<String>) args.get("lore");
        String texture = args.get("texture") == null ? "" : (String) args.get("texture");
        List<PotionEffect> potionEffects;
        if (args.get("effect") == null) {
            potionEffects = new ArrayList<>();
        } else {
            potionEffects = new ArrayList<>();
            ((MemorySection)args.get("effect")).getValues(true).forEach((key, value) -> potionEffects.add(new PotionEffect(PotionEffectType.getByName(key.toUpperCase(Locale.ROOT)),  Integer.valueOf(Arrays.stream(value.toString().split(";")).toList().get(0)), Integer.valueOf(Arrays.stream(value.toString().split(";")).toList().get(1)))));
        }
        List<ItemFlag> itemFlags;
        if (args.get("flags") == null) {
            itemFlags = new ArrayList<>();
        } else {
            itemFlags = new ArrayList<>();
            for (String flag : (List<String>)args.get("flags")) {
                itemFlags.add(ItemFlag.valueOf(flag.toUpperCase(Locale.ROOT)));
            }
        }
        Integer customModelData = args.get("model-data") == null ? 0 : (Integer) args.get("model-data");
        String dyeHex = args.get("dye") == null ? "" : (String) args.get("dye");

        return new ItemBuilder(material)
                .amount(amount)
                .name(itemName)
                .enchantments(enchantmentLevelMap)
                .lore(lore)
                .skullTexture(texture)
                .potionEffects(potionEffects)
                .flags(itemFlags)
                .customModelData(customModelData)
                .colour(dyeHex)
                .build();

    }
}
