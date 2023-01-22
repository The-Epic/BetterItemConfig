package me.epic.betteritemconfig;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SectionUtils {

    public static ConfigurationSection first(ConfigurationSection parent, String... kids) {
        List<String> children = Arrays.asList(kids).stream().map(kid -> kid.toLowerCase(Locale.ROOT)).toList();
        return parent.getKeys(false).stream()
                .filter(parent::isConfigurationSection)
                .map(key -> key.toLowerCase(Locale.ROOT)) /* no Locale.ITALY :pepe_cry_hands: */
                .filter(children::contains)
                .map(parent::getConfigurationSection)
                .findFirst().orElse(null);
    }
}
