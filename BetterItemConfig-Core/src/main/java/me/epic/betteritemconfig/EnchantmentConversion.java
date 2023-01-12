package me.epic.betteritemconfig;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentConversion {

    public static Enchantment getEnchantment(String string) {
        return Enchantment.getByName(string);
    }
}
