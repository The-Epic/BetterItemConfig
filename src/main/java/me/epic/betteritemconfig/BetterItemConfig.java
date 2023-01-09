package me.epic.betteritemconfig;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class BetterItemConfig {

    @Getter
    public static boolean useMiniMessage;

    public static void init(boolean useMiniMessage) {
        BetterItemConfig.useMiniMessage = useMiniMessage;

        ConfigurationSerialization.registerClass(ItemSerialization.class, "ItemSerialization");

    }
}
