package me.epic.betteritemconfig;

import net.minecraft.nbt.NBTBase;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;

public class Utils {

    public static Map<String, NBTBase> getCustomDataTags(PersistentDataContainer container) throws IllegalAccessException, NoSuchFieldException {
        Field customDataTagsField = container.getClass().getDeclaredField("customDataTags");
        customDataTagsField.setAccessible(true);
        return (Map<String, NBTBase>) customDataTagsField.get(container);
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
            if (object instanceof Byte[])
                return PersistentDataType.BYTE;
            else if (object instanceof Integer[])
                return PersistentDataType.INTEGER_ARRAY;
            else if (object instanceof Long[])
                return PersistentDataType.LONG_ARRAY;
            else return PersistentDataType.BYTE_ARRAY;
        } else {
            return PersistentDataType.BYTE_ARRAY;
        }
    }
}