package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.handlers.ItemHandler;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class PersistentDataHandler implements ItemHandler {

    private static Class<?> CRAFT_ITEM_META_CLASS;
    private static Class<?> CRAFT_ITEM_META_SERIALIZABLE_META_CLASS;
    private static Class<?> CRAFT_PERSISTENT_DATA_CONTAINER_CLASS;
    private static Class<?> CRAFT_NBTTAG_CONFIG_SERIALIZER_CLASS;

    private static Method CRAFT_PERSISTENT_DATA_CONTAINER_SERIALIZE;
    private static Method CRAFT_NBTTAG_CONFIG_SERIALIZER_DESERIALIZE;
    private static Method CRAFT_ITEM_META_SERIALIZABLE_META_GET_OBJECT;

    static {
        try {
            CRAFT_ITEM_META_CLASS = new ItemStack(Material.BARRIER).getItemMeta().getClass();
            CRAFT_ITEM_META_SERIALIZABLE_META_CLASS = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".inventory.CraftMetaItem$SerializableMeta");
            CRAFT_ITEM_META_SERIALIZABLE_META_GET_OBJECT = CRAFT_ITEM_META_SERIALIZABLE_META_CLASS.getDeclaredMethod("getObject", Class.class, Map.class, Object.class, boolean.class);
            CRAFT_ITEM_META_SERIALIZABLE_META_GET_OBJECT.setAccessible(true);

            CRAFT_PERSISTENT_DATA_CONTAINER_CLASS = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".persistence.CraftPersistentDataContainer");
            CRAFT_PERSISTENT_DATA_CONTAINER_SERIALIZE = CRAFT_PERSISTENT_DATA_CONTAINER_CLASS.getDeclaredMethod("serialize");

            CRAFT_NBTTAG_CONFIG_SERIALIZER_CLASS = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".util.CraftNBTTagConfigSerializer");
            CRAFT_NBTTAG_CONFIG_SERIALIZER_DESERIALIZE = CRAFT_NBTTAG_CONFIG_SERIALIZER_CLASS.getDeclaredMethod("deserialize", Object.class);
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        if (section.contains("pdc") && section.isConfigurationSection("pdc")) {
            ItemBuilder builder = ItemBuilder.modifyItem(stack);

            try {
                Map objectMap = (Map) CRAFT_ITEM_META_SERIALIZABLE_META_GET_OBJECT.invoke(CRAFT_ITEM_META_CLASS.cast(builder.getItemMeta()), Map.class, section.getValues(false), "pdc", true);
                builder.persistentData((NBTTagCompound) CRAFT_NBTTAG_CONFIG_SERIALIZER_DESERIALIZE.invoke(null, objectMap));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return stack;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta() && !item.getItemMeta().getPersistentDataContainer().isEmpty()) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            Map<String, Object> serializedPDC;

            try {
                serializedPDC = (Map<String, Object>) CRAFT_PERSISTENT_DATA_CONTAINER_SERIALIZE.invoke(pdc);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            section.set("pdc", serializedPDC);
        }
    }

    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
