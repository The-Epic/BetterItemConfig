package me.epic.betteritemconfig;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public final class SectionUtils {
    private SectionUtils() { }

    public static ConfigurationSection first(ConfigurationSection parent, String... kids) {
        List<String> sections = parent.getKeys(false).stream()
                .filter(parent::isConfigurationSection)
                .toList();

        for (String kid : kids) {
            for (String section : sections) {
                if (!kid.equalsIgnoreCase(section))
                    continue;

                return parent.getConfigurationSection(section);
            }
        }

        return null;
    }
}
