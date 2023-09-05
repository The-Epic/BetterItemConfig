package me.epic.betteritemconfig.handlers.impl;

import com.jeff_media.persistentdataserializer.PersistentDataSerializer;
import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class PersistentDataHandler implements ItemHandler {



    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        if (section.isConfigurationSection("pdc")) {
            ItemBuilder builder = ItemBuilder.modifyItem(stack);

            builder.persistentData(PersistentDataSerializer.fromMapList(section.getMapList("pdc"), builder.getPDCAdapterContext()));
            return builder.build();
        }
        return stack;
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.hasItemMeta() && !item.getItemMeta().getPersistentDataContainer().isEmpty()) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

            section.set("pdc", PersistentDataSerializer.toMapList(pdc));
        }
    }
}
