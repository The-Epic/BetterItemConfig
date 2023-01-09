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

    public static String getNiceMaterialName(final Material mat) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> iterator = Arrays.stream(mat.name().split("_")).iterator();
        while (iterator.hasNext()) {
            builder.append(WordUtils.upperCaseFirstLetterOnly(iterator.next()));
            if (iterator.hasNext()) builder.append(" ");
        }
        return builder.toString();
    }


    private class WordUtils {
        public static String upperCaseFirstLetterOnly(final String word) {
            return upperCaseFirstLetter(word.toLowerCase(Locale.ROOT));
        }

        public static String upperCaseFirstLetter(final String word) {
            if (word.length() < 1) return word;
            if (word.length() == 1) return word.toUpperCase(Locale.ROOT);
            return word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
        }
    }

}
