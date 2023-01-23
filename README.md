# BetterItemConfig

Default formatting specifications provided on the [wiki](https://github.com/The-Epic/BetterItemConfig/wiki)

## Importing BetterItemConfig

### Maven

```xml
<project>
    <repositories>
        <!-- The artifact is present in both repositories. You can declare both or just one -->
    
        <repository>
            <id>jeff-media-community</id>
            <url>https://hub.jeff-media.com/nexus/repository/jeff-media-community/</url>
        </repository>
    
        <repository>
            <id>maven-dominick-sh-snapshots</id>
            <name>Dominick's Reposilite</name>
            <url>https://maven.dominick.sh/snapshots</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>me.epic</groupId>
            <artifactId>BetterItemConfig</artifactId>
            <version>2.0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.4.1</version>
                <configuration>
                    <dependencyReducedPomLocation>${project.build.directory}/dependency-reduced-pom.xml</dependencyReducedPomLocation>
                    <relocations>
                        <relocation>
                            <pattern>me.epic.betteritemconfig</pattern>
                            <shadedPattern>[YOUR PACKAGE HERE].betteritemconfig</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

> Make sure to update [YOUR PACKAGE HERE] within the relocation plugin configuration.

## Using BetterItemConfig

In order to use BetterItemConfig, you need an instance to ItemFactory. If you want to
use all the default handlers without modification, you can use ItemFactory.DEFAULT.
Instructions on creating a new ItemFactory are provided in the 
[Using Custom Handlers](#using-custom-handlers) section.

The basics are coded below.

```java
public class YourPlugin extends JavaPlugin {
    public ItemStack getBanHammer() {
        return ItemFactory.DEFAULT.read(getConfig().getConfigurationSection("items.ban-hammer"));
    }
    
    public void saveItemStack(ItemStack item) {
        ItemFactory.DEFAULT.write(item, getConfig().getConfigurationSection("items.dynamic"));
    }
}
```

> Do note that you should catch errors with a surrounding try/catch. There is no *one specific
> error* that can be thrown.

## Modifying The Pipeline

BetterItemConfig is developed with extreme flexibility in mind. Each action is
performed by an ItemHandler instance. All ItemHandlers are added to the pipeline
in the ItemFactory class. You may add or remove handlers by creating an ItemFactory
instance using ItemFactory.Builder.

### Extending ItemHandler

The ItemHandler interface provides methods executed by ItemFactory when #process()
or #write() is called. By overriding these methods, you are able to add your own
functionality to the ItemFactory pipeline.

An example for creating a PersistentDataType.INTEGER tag in the ItemStack's
PersistentDataContainer is provided in Java below. 

```java
import me.epic.betteritemconfig.handlers.ItemHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class RevivesHandler implements ItemHandler {
    /* Only included for sakes of example using NamespacedKey. Not required to have an instance of JavaPlugin. */
    private final JavaPlugin plugin;
    private final NamespacedKey revivesKey;

    public RevivesHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.revivesKey = new NamespacedKey(plugin, "revives");
    }

    public ItemStack process(ItemStack item, ConfigurationSection section) {
        if (!section.contains("revives") || !item.hasItemMeta())
            return;

        int revives = section.getInt("revives");

        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(revivesKey, PersistentDataType.INTEGER, revives);

        item.setItemMeta(meta);
        return item;
    }

    public void write(ItemStack item, ConfigurationSection section) {
        if (!item.hasItemMeta())
            return;

        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(revivesKey, PersistentDataType.INTEGER))
            return;

        int revives = pdc.get(revivesKey, PersistentDataType.INTEGER);

        section.set("revives", revives);
    }
}
```

> Do note that ItemHandler has a default #doesSupportRead() method and #doesSupportWrite()
> method. If either #process() or #write() throws UnsupportedOperationException (or any error),
> it is advised to override the equivalent default method and return false.

### Overriding BaseProcessor

The first step that the ItemFactory needs to accomplish is to generate an ItemStack,
because ItemHandler expects an ItemStack to already be instantiated. In order to accomplish
this, BetterItemConfig has BaseProcessor. BaseProcessor's only job is to make an ItemStack
from a ConfigurationSection using the type and amount.

In this example, we modify BaseProcessor to only use the provided type's max stack size, not
allowing modifications to the amount of an item using the configuration.

```java
import me.epic.betteritemconfig.ItemBuilder;
import me.epic.betteritemconfig.handlers.BaseProcessor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class NoAmountItemStackHandler implements BaseProcessor {
    public ItemStack read(ConfigurationSection section) {
        Material type;

        ItemBuilder builder = new ItemBuilder();
        if (section.isSet("type")) {
            type = Objects.requireNonNull(
                    Material.getMaterial(section.getString("type").toUpperCase()),
                    "The specified Material is not valid");

            builder.material(type);
        } else throw new IllegalArgumentException("Type parameter is not set, ItemStack is not valid");

        builder.amount(type.getMaxStackSize());
        return builder.build();
    }

    public void write(ItemStack stack, ConfigurationSection section) {
        section.set("type", stack.getType().toString().toLowerCase());
    }
}
```

### Using Custom Handlers

To use or modify the ItemFactory pipeline, you need to create a new instance of the ItemFactory class.

```java
public class YourPlugin extends JavaPlugin {
    public static final ItemFactory ITEM_FACTORY = new ItemFactory.Builder()
            .register(new NoAmountItemStackHandler())
            .register(new NameHandler())
            .build();
}
```