package dev.o8o1o5.starforceDeBleu.util.lore;

import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.ArrayList;

public class StarLoreUtil {
    private static final String STAR_LORE_PREFIX_TEXT = "스타포스";
    public static final String STAR_DISPLAY_LORE_PREFIX = ChatColor.GRAY + STAR_LORE_PREFIX_TEXT + " ";


    private static final String FILLED_STAR_CHAR = "★";
    private static final int STARS_PER_GROUP = 5;

    private static final ChatColor[] STAR_LEVEL_COLORS = {
            ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.GREEN, ChatColor.AQUA
    };
    private static final ChatColor BASE_EMPTY_STAR_COLOR = ChatColor.DARK_GRAY;

    public static void addStarLore(ItemStack item, int stars) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        if (stars > 0 && stars <= DataUtil.MAX_STARFORCE_LEVEL) {
            StringBuilder starLoreBuilder = new StringBuilder();
            starLoreBuilder.append(ChatColor.GRAY).append(STAR_LORE_PREFIX_TEXT).append(" ");

            int currentSegmentIndex = (stars - 1) / STARS_PER_GROUP;
            ChatColor currentFilledColor = (currentSegmentIndex >= 0 && currentSegmentIndex < STAR_LEVEL_COLORS.length)
                    ? STAR_LEVEL_COLORS[currentSegmentIndex]
                    : BASE_EMPTY_STAR_COLOR;
            ChatColor previousFilledColor = (currentSegmentIndex > 0 && currentSegmentIndex - 1 < STAR_LEVEL_COLORS.length)
                    ? STAR_LEVEL_COLORS[currentSegmentIndex - 1]
                    : BASE_EMPTY_STAR_COLOR;
            int currentGroupFilledStars = stars % STARS_PER_GROUP;
            if (currentGroupFilledStars == 0 && stars > 0) { // 5성, 10성 등 딱 떨어지는 경우
                currentGroupFilledStars = STARS_PER_GROUP;
            }

            for (int i = 0; i < currentGroupFilledStars; i++) {
                starLoreBuilder.append(currentFilledColor).append(FILLED_STAR_CHAR);
            }
            for (int i = 0; i < (STARS_PER_GROUP - currentGroupFilledStars); i++) {
                starLoreBuilder.append(previousFilledColor).append(FILLED_STAR_CHAR);
            }

            lore.add(0, starLoreBuilder.toString());
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}