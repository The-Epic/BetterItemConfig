package me.epic.betteritemconfig;


import de.tr7zw.changeme.nbtapi.NBTItem;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * For internal use only
 */
public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;

    private Map<String, Object> nbtToAdd = new HashMap<>();

    private static Class<?> CRAFT_PERSISTENT_DATA_CONTAINER;
    private static Method CRAFT_PERSISTENT_DATA_CONTAINER_PUT_ALL;

    private static Class<?> CRAFT_ITEM_META_CLASS;
    private static Field CRAFT_ITEM_META_CLASS_CONTAINER_FIELD;

    static {
        try {
            CRAFT_PERSISTENT_DATA_CONTAINER = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".persistence.CraftPersistentDataContainer");
            CRAFT_PERSISTENT_DATA_CONTAINER_PUT_ALL = CRAFT_PERSISTENT_DATA_CONTAINER.getMethod("putAll", NBTTagCompound.class);

            CRAFT_ITEM_META_CLASS = new ItemStack(Material.BARRIER).getItemMeta().getClass();
            CRAFT_ITEM_META_CLASS_CONTAINER_FIELD = CRAFT_ITEM_META_CLASS.getDeclaredField("persistentDataContainer");
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public ItemBuilder() {
        this(Material.AIR, 1);
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
    }

    public static ItemBuilder modifyItem(ItemStack stack) {
        ItemBuilder builder = new ItemBuilder();
        builder.item = stack;
        builder.meta = stack.getItemMeta();

        return builder;
    }

    public ItemBuilder material(Material material) {
        this.item.setType(material);

        return this;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);

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

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(texture.getBytes()));
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL("http://textures.minecraft.net/texture/" + texture));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        profile.setTextures(textures);
        skullMeta.setOwnerProfile(profile);

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

    public ItemBuilder potionEffect(PotionEffect effect) {
        if (!(this.meta instanceof PotionMeta potionMeta)) return this;
        potionMeta.addCustomEffect(effect, true);
        return this;
    }

    public ItemBuilder basePotionEffect(String potion) {
        nbtToAdd.put("Potion", potion);
        return this;
    }

    @Deprecated
    public <T, Z> ItemBuilder persistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        this.meta.getPersistentDataContainer().set(key, type, value);

        return this;
    }

    public ItemBuilder persistentData(NBTTagCompound nbtTagCompound) {
        try {
            CRAFT_PERSISTENT_DATA_CONTAINER_PUT_ALL.invoke(CRAFT_ITEM_META_CLASS_CONTAINER_FIELD.get(CRAFT_ITEM_META_CLASS.cast(this.meta)), nbtTagCompound);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemBuilder generation(BookMeta.Generation generation) {
        if (!(this.meta instanceof BookMeta bookMeta)) return this;
        bookMeta.setGeneration(generation);

        return this;
    }

    public ItemBuilder author(String string) {
        if (!(this.meta instanceof BookMeta bookMeta)) return this;
        bookMeta.setAuthor(Format.format(string));

        return this;
    }

    public ItemBuilder title(String string) {
        if (!(this.meta instanceof BookMeta bookMeta)) return this;
        bookMeta.setTitle(Format.format(string));

        return this;
    }

//    public ItemBuilder pages(List<String> pages) {
//        if (!(this.meta instanceof BookMeta bookMeta)) return this;
//        pages.forEach(page -> bookMeta.addPage(Format.formatBookPage(page)));
//
//        return this;
//    }

    public ItemMeta getItemMeta() {
        return this.meta;
    }

    public ItemStack build() {
        ItemStack finalItem = this.item;
        finalItem.setItemMeta(this.meta);
        if (!finalItem.getType().isAir() || finalItem.getType() != null || finalItem.getAmount() != 0) {
            NBTItem nbtItem = new NBTItem(finalItem);
            for (Map.Entry entry : nbtToAdd.entrySet()) {
                nbtItem.setString((String) entry.getKey(), (String) entry.getValue());
            }
            return nbtItem.getItem();
        }
        return finalItem;
    }

    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
