package me.epic.betteritemconfig.handlers.impl;

import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.Utils;
import me.epic.betteritemconfig.exceptions.PluginNotFoundException;
import me.epic.betteritemconfig.handlers.ItemHandler;
import net.minecraft.nbt.NBTBase;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PersistentDataHandler implements ItemHandler {
    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        if (section.contains("pdc") && section.isConfigurationSection("pdc")) {
            ItemBuilder builder = ItemBuilder.modifyItem(stack);
            ConfigurationSection pdcSection = section.getConfigurationSection("pdc");
            for (String value : pdcSection.getKeys(false)) {
                ConfigurationSection pluginKeySection = pdcSection.getConfigurationSection(value);
                List<String> values = Arrays.stream(value.split(":")).toList();
                Plugin plugin = Bukkit.getPluginManager().getPlugin(values.get(0));
                if (plugin == null) throw new PluginNotFoundException("Plugin: :\"" + values.get(0) + "\" was not Found");
                NamespacedKey key = new NamespacedKey(plugin, values.get(1));
                String pdcValue = pluginKeySection.getString("value");
                String pdcType = pluginKeySection.getString("type");
                builder.persistentData(key,
                        Utils.getPDT(Utils.convertToCorrectType(pdcValue, pdcType)),
                        Utils.convertToCorrectType(pdcValue.replace("\"", ""), pdcType));
            }
        }
        return stack;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta() && !item.getItemMeta().getPersistentDataContainer().isEmpty()) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            Map<String, NBTBase> stringNBTBaseMap = new HashMap<>(Utils.getCustomDataTags(pdc));
            ConfigurationSection pdcSection = section.createSection("pdc");
            for (Map.Entry entry : stringNBTBaseMap.entrySet()) {
                String valueClassName = entry.getValue().getClass().getSimpleName().toUpperCase(Locale.ROOT).replace("NBTTAG", "");
                ConfigurationSection pluginKeySection = pdcSection.createSection((String) entry.getKey());
                pluginKeySection.set("type", valueClassName);
                pluginKeySection.set("value", Utils.convertToCorrectType((String) entry.getValue(), valueClassName));
            }
        }
    }
}
