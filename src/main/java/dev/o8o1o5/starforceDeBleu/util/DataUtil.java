package dev.o8o1o5.starforceDeBleu.util;

import com.google.common.collect.Multimap;
import dev.o8o1o5.starforceDeBleu.manager.LogicManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class DataUtil {
    private static JavaPlugin plugin;

    private static NamespacedKey STARFORCE_STARS_KEY;
    private static NamespacedKey STARFORCE_PROCESSED_KEY;
    private static NamespacedKey STARFORCE_STARFORCABLE_KEY;

    public static int MAX_STARFORCE_LEVEL = 25;

    public static void initialize(JavaPlugin p) {
        plugin = p;
        if (plugin == null) {
            return;
        }

        STARFORCE_STARS_KEY = new NamespacedKey(plugin, "starforce_stars");
        STARFORCE_PROCESSED_KEY = new NamespacedKey(plugin, "starforce_processed");
        STARFORCE_STARFORCABLE_KEY = new NamespacedKey(plugin, "starforce_starforcable");
    }

    public static void setStars(ItemStack item, int stars) {
        if (item == null) {
            return;
        }

        ItemMeta meta;
        if (!item.hasItemMeta()) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
            if (meta == null) {
                return;
            }
            item.setItemMeta(meta);
        } else {
            meta = item.getItemMeta();
        }

        meta.getPersistentDataContainer().set(STARFORCE_STARS_KEY, PersistentDataType.INTEGER, stars);
        item.setItemMeta(meta);

        // 이는 책임 및 관심사 분리를 위해 이관됩니다.
        // ItemLoreDisplayUtil.updateItemLore(item, stars);
        // --> StarforceManager 에서 setStars 를 호출한 후에 담당합니다.
    }

    public static int getStars(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        int retrievedStars = meta.getPersistentDataContainer().getOrDefault(STARFORCE_STARS_KEY, PersistentDataType.INTEGER, 0);
        return retrievedStars;
    }

    public static void setProcessed(ItemStack item, boolean processed) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(STARFORCE_PROCESSED_KEY, PersistentDataType.BOOLEAN, processed);
        item.setItemMeta(meta);
    }

    public static boolean isProcessed(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        boolean processed = meta.getPersistentDataContainer().getOrDefault(STARFORCE_PROCESSED_KEY, PersistentDataType.BOOLEAN, false);
        return processed;
    }

    public static void setStarforcable(ItemStack item, boolean starforcable) {
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
            if (meta == null) {
                return;
            }
        }

        meta.getPersistentDataContainer().set(STARFORCE_STARFORCABLE_KEY, PersistentDataType.BOOLEAN, starforcable);
        item.setItemMeta(meta);
    }

    public static boolean isStarforcable(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(STARFORCE_STARFORCABLE_KEY, PersistentDataType.BOOLEAN, false);
    }

    public static boolean hasRelevantAttributeModifiers(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
            if (meta == null) {
                return false;
            }
        }

        if (meta.hasAttributeModifiers()) {
            Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
            if (!modifiers.isEmpty()) {
                return true;
            }
        }

        if (LogicManager.STARFORCABLE_MATERIAL.contains(item.getType())) {
            return true;
        }

        return false;
    }
}
