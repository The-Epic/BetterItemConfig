package xyz.epicebic.betteritemconfig;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * For internal use only
 */
public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;

    private static final PersistentDataType<?, ?>[] PRIMITIVE_DATA_TYPES = new PersistentDataType<?, ?>[]{
            PersistentDataType.BYTE,
            PersistentDataType.SHORT,
            PersistentDataType.INTEGER,
            PersistentDataType.LONG,
            PersistentDataType.FLOAT,
            PersistentDataType.DOUBLE,
            PersistentDataType.STRING,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.TAG_CONTAINER_ARRAY,
            PersistentDataType.TAG_CONTAINER};

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
        for (Map.Entry<Enchantment, Integer> entry : enchantmentLevelMap.entrySet()) this.meta.addEnchant(entry.getKey(),entry.getValue(), true);
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

    public ItemBuilder basePotionEffect(PotionData potionData) {
        if (!(this.meta instanceof PotionMeta potionMeta)) return this;

        potionMeta.setBasePotionData(potionData);
        return this;
    }

    public <T, Z> ItemBuilder persistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        this.meta.getPersistentDataContainer().set(key, type, value);

        return this;
    }

    public ItemBuilder persistentData(PersistentDataContainer pdc) {
        PersistentDataContainer newContainer = this.meta.getPersistentDataContainer();
        for (NamespacedKey key : pdc.getKeys()) {
            PersistentDataType type = getDataType(pdc, key);
            if (type != null) {
                continue;
            }
            newContainer.set(key, type, pdc.get(key, type));
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

    public PersistentDataAdapterContext getPDCAdapterContext() {
        return this.meta.getPersistentDataContainer().getAdapterContext();
    }

    public ItemStack build() {
        ItemStack finalItem = this.item;
        finalItem.setItemMeta(this.meta);
        return finalItem;
    }

    private static PersistentDataType<?, ?> getDataType(PersistentDataContainer pdc, NamespacedKey key) {
        for (PersistentDataType<?, ?> dataType : PRIMITIVE_DATA_TYPES) {
            if (pdc.has(key, dataType)) return dataType;
        }
        return null;
    }
}
