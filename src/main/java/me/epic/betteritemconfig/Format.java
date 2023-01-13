package me.epic.betteritemconfig;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class Format {

    private static boolean usingMiniMessage = BetterItemConfig.isUseMiniMessage();
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&')
            .hexCharacter('#').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static String format(String toTranslate) {
        return usingMiniMessage ?
                ChatColor.translateAlternateColorCodes('&', SERIALIZER.serialize(MiniMessage.miniMessage().deserialize(toTranslate))) :
                ChatColor.translateAlternateColorCodes('&', toTranslate);

    }

    public static String formatBookPage(String originalString) {
        char[] inputChars = originalString.toCharArray();
        int counter = 0;

        for (int i = 0; i < inputChars.length; i++) {
            if (inputChars[i] == '&') {
                counter++;
                if (counter % 2 != 0) {
                    inputChars[i] = '§';
                }
            }
        }

        String replaced = new String(inputChars);
        return replaced;
    }

}
