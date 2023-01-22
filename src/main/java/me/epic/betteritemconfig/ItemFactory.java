package me.epic.betteritemconfig;

import me.epic.betteritemconfig.handlers.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    public static final ItemFactory DEFAULT = new ItemFactory.Builder()
            .register(new LoreHandler())
            .register(new CustomEffectHandler())
            .register(new NameHandler())
            .build();

    protected final BaseProcessor baseProcessor;
    protected final List<ItemHandler> handlers;

    protected ItemFactory(BaseProcessor baseProcessor, List<ItemHandler> handlers) {
        this.baseProcessor = baseProcessor;
        this.handlers = new ArrayList<>(handlers);
    }

    public ItemStack read(ConfigurationSection section) {
        ItemStack initialItem = baseProcessor.read(section);
        for (ItemHandler handler : handlers) initialItem = handler.process(initialItem, section);

        return initialItem;
    }

    public void write(ItemStack itemStack, FileConfiguration fileConfiguration, String path) {
        this.write(itemStack, fileConfiguration.createSection(path));
    }

    public void write(ItemStack itemStack, ConfigurationSection parent, String key) {
        this.write(itemStack, parent.createSection(key));
    }

    public void write(ItemStack itemStack, ConfigurationSection section) {
        baseProcessor.write(itemStack, section);
        for (ItemHandler handler : handlers)
            handler.write(itemStack, section);
    }



    public static final class Builder {
        private BaseProcessor baseProcessor = new ItemStackHandler();
        private List<ItemHandler> handlers = new ArrayList<>();

        public Builder() {

        }

        public Builder register(ItemHandler handler) {
            if (handler == null) throw new IllegalArgumentException("Unable to register null ItemHandler");
            handlers.add(handler);

            return this;
        }

        public Builder register(BaseProcessor baseProcessor) {
            if (baseProcessor == null) throw new IllegalArgumentException("Unable to register null BaseProcessor");
            this.baseProcessor = baseProcessor;

            return this;
        }

        public ItemFactory build() {
            return new ItemFactory(baseProcessor, handlers);
        }
    }


}
