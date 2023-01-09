package me.epic.betteritemconfig;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

/**
 * For internal use only
 */
public class ItemBuilder {

    private static final String TEXTURE_URL = "http://textures.minecraft.net/texture/";
    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);

        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantmentLevelMap) {
        for (Map.Entry entry : enchantmentLevelMap.entrySet()) this.meta.addEnchant((Enchantment) entry.getKey(), (Integer) entry.getValue(), true);
        return this;
    }

    public ItemBuilder name(String name) {
        this.meta.setDisplayName(Format.format(name));
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> itemLore = getLore();
        List<String> loreList = Arrays.stream(lore.toArray()).map(line -> Format.format((String) line)).toList();
        itemLore.addAll(loreList);

        this.meta.setLore(itemLore);

        return this;
    }

    public List<String> getLore() {
        return this.meta.hasLore() ? this.meta.getLore() : new ArrayList<>();
    }


    public ItemBuilder flags(List<ItemFlag> flags) {
        ItemFlag[] itemFlags = flags.toArray(new ItemFlag[0]);
        this.meta.addItemFlags(itemFlags);

        return this;
    }

    public ItemBuilder skullTexture(String texture) {
        if (!(this.meta instanceof SkullMeta skullMeta)) return this;

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
            profileField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemBuilder skullTexture(Player player) {
        if (!(this.meta instanceof SkullMeta skullMeta)) return this;
        skullMeta.setOwningPlayer(player);

        return this;
    }

    // Hex with #
    public ItemBuilder colour(String hex) {
        if (this.meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(Color.fromRGB(
                    Integer.valueOf(hex.substring(1, 3), 16),
                    Integer.valueOf(hex.substring(3, 5), 16),
                    Integer.valueOf(hex.substring(5, 7), 16)));
        } else if (this.meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(Color.fromRGB(
                    Integer.valueOf(hex.substring(1, 3), 16),
                    Integer.valueOf(hex.substring(3, 5), 16),
                    Integer.valueOf(hex.substring(5, 7), 16)));
        } else {
            return this;
        }
        return null;
    }

    public ItemBuilder customModelData(int number) {
        this.meta.setCustomModelData(number);
        return this;
    }

    public ItemBuilder potionEffects(List<PotionEffect> potionEffectList) {
        if (!(this.meta instanceof PotionMeta potionMeta)) return this;
        for (PotionEffect effect : potionEffectList) potionMeta.addCustomEffect(effect, true);
        return this;
    }

    public ItemStack build() {
        ItemStack finalItem = this.item;
        finalItem.setItemMeta(this.meta);

        return finalItem;
    }
}
