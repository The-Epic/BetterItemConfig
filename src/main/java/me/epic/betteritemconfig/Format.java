package me.epic.betteritemconfig;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class Format {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&')
            .hexCharacter('#').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static String format(String toTranslate) {
        return ChatColor.translateAlternateColorCodes('&', SERIALIZER.serialize(MiniMessage.miniMessage().deserialize(toTranslate)));
    }


}
