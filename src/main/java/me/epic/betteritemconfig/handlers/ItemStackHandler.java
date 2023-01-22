package me.epic.betteritemconfig.handlers;

import me.epic.betteritemconfig.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Objects;

public class ItemStackHandler implements BaseProcessor {
    @Override
    public ItemStack read(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder();
        if (section.isSet("type")) {
            builder.material(Objects.requireNonNull(Material.getMaterial(section.getString("type").toUpperCase(Locale.ROOT)), "The specified Material is not valid"));
        } else {
            throw new IllegalArgumentException("Type parameter is not set, ItemStack is not valid");
        }
        if (section.isSet("amount")) {
            builder.amount(section.getInt("amount"));
        } else {
            builder.amount(1);
        }
        return builder.build();
    }

    @Override
    public void write(ItemStack stack, ConfigurationSection section) {
        section.set("type", stack.getType().toString().toLowerCase(Locale.ROOT));
        section.set("amount", stack.getAmount());
    }
}
