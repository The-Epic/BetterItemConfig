package me.epic.betteritemconfig;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class Format {

    private static boolean usingMiniMessage = BetterItemConfig.isUseMiniMessage();
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&')
            .hexCharacter('#').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static String format(String toTranslate) {
        return usingMiniMessage ?
                ChatColor.translateAlternateColorCodes('&', SERIALIZER.serialize(MiniMessage.miniMessage().deserialize(toTranslate))) :
                ChatColor.translateAlternateColorCodes('&', toTranslate);

    }

}
