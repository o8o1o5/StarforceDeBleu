package dev.o8o1o5.starforceDeBleu.util.lore;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemLoreDisplayUtil {
    public static void updateItemLore(ItemStack item, int stars) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        removeExistingStarforceLoreInternal(item);

        StarLoreUtil.addStarLore(item, stars);
        AttributeLoreUtil.addAttributeLore(item, stars);
    }

    private static void removeExistingStarforceLoreInternal(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        if (lore == null || lore.isEmpty()) {
            return;
        }

        List<String> cleanedLore = new ArrayList<>();
        boolean removedAnyLine = false;

        for (String line : lore) {
            String strippedLine = ChatColor.stripColor(line);

            if (strippedLine.contains(ChatColor.stripColor(StarLoreUtil.STAR_DISPLAY_LORE_PREFIX))) {
                removedAnyLine = true;
                continue; // 이 줄 제거
            }
            if (strippedLine.contains(ChatColor.stripColor(AttributeLoreUtil.ATTRIBUTE_ADDITIONAL_DAMAGE_LORE_SURFIX))) {
                removedAnyLine = true;
                continue;
            }
            if (strippedLine.contains(ChatColor.stripColor(AttributeLoreUtil.ATTRIBUTE_ADDITIONAL_DAMAGE_PERCENTAGE_LORE_SURFIX))) {
                removedAnyLine = true;
                continue;
            }
            if (strippedLine.contains(ChatColor.stripColor(AttributeLoreUtil.ATTRIBUTE_REDUCIBLE_DAMAGE_LORE_SURFIX))) {
                removedAnyLine = true;
                continue;
            }
            if (strippedLine.contains(ChatColor.stripColor(AttributeLoreUtil.ATTRIBUTE_REDUCIBLE_DAMAGE_PERCENTAGE_LORE_SURFIX))) {
                removedAnyLine = true;
                continue;
            }
            if (strippedLine.equals("")) {
                removedAnyLine = true;
                continue;
            }

            cleanedLore.add(line);
        }

        if (removedAnyLine) {
            meta.setLore(cleanedLore);
            item.setItemMeta(meta);
        }
    }
}
