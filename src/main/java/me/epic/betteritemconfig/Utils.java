package me.epic.betteritemconfig;

import net.minecraft.nbt.NBTBase;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

public class Utils {

    public static Map<String, NBTBase> getCustomDataTags(PersistentDataContainer container) {
        try {
            Field customDataTagsField = container.getClass().getDeclaredField("customDataTags");
            customDataTagsField.setAccessible(true);
            return (Map<String, NBTBase>) customDataTagsField.get(container);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static PersistentDataType getPDT(Object object) {
        if (object instanceof String) {
            return PersistentDataType.STRING;
        } else if (object instanceof Byte) {
            return PersistentDataType.BYTE;
        } else if (object instanceof Double) {
            return PersistentDataType.DOUBLE;
        } else if (object instanceof Float) {
            return PersistentDataType.FLOAT;
        } else if (object instanceof Integer) {
            return PersistentDataType.INTEGER;
        } else if (object instanceof Long) {
            return PersistentDataType.LONG;
        } else if (object instanceof Short) {
            return PersistentDataType.SHORT;
        } else if (object instanceof Array) {
            return PersistentDataType.BYTE_ARRAY;
        } else {
            return PersistentDataType.BYTE_ARRAY;
        }
    }

    public static Object convertToCorrectType(String string, String type) {
        switch (type.toLowerCase(Locale.ROOT)) {
            case "int", "integer" -> {
                return Integer.parseInt(string);
            }
            case "long" -> {
                return Long.parseLong(string);
            }
            case "double" -> {
                return Double.parseDouble(string);
            }
            case "float" -> {
                return Float.parseFloat(string);
            }
            case "short" -> {
                return Short.parseShort(string);
            }
            case "byte" -> {
                return Byte.parseByte(string);
            }
            default -> {
                return string;
            }
        }

    }

    public static String getMetaColor(ItemMeta meta) {
        Color color = meta instanceof PotionMeta potionMeta ? potionMeta.getColor() : meta instanceof LeatherArmorMeta leatherArmorMeta ? leatherArmorMeta.getColor() : null;
        String hex = "#" + Integer.toHexString(color.asRGB()).substring(2);
        return hex;
    }
}