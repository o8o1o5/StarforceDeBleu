package dev.o8o1o5.starforceDeBleu.util.lore;

import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import dev.o8o1o5.starforceDeBleu.util.calculator.ArmorCalculator;
import dev.o8o1o5.starforceDeBleu.util.calculator.SwordCalculator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AttributeLoreUtil {
    public static final String ATTRIBUTE_ADDITIONAL_DAMAGE_LORE_SURFIX = ChatColor.GOLD + "추가 피해";
    public static final String ATTRIBUTE_ADDITIONAL_DAMAGE_PERCENTAGE_LORE_SURFIX = ChatColor.GOLD + "추가 피해 배율";
    public static final String ATTRIBUTE_REDUCIBLE_DAMAGE_LORE_SURFIX = ChatColor.GOLD + "받는 피해 감소";
    public static final String ATTRIBUTE_REDUCIBLE_DAMAGE_PERCENTAGE_LORE_SURFIX = ChatColor.GOLD + "받는 피해 감소 배율";

    public static void addAttributeLore(ItemStack item, int stars) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        if (stars <= 0 || stars > DataUtil.MAX_STARFORCE_LEVEL) {
            meta.setLore(lore);
            return;
        }

        Material type = item.getType();

        lore.add("");
        lore.add(ChatColor.GRAY + "스타포스 효과:");

        if (type.name().endsWith("_SWORD")) {
            double additionalDamage = SwordCalculator.getAdditionalDamage(item, stars);
            double additionalDamagePercentage = SwordCalculator.getAdditionalDamagePercentage(item, stars);

            String formattedAddDamage = String.format("%.1f", additionalDamage);
            String formattedAddPercentage = String.format("%.1f", (additionalDamagePercentage - 1.0) * 100);

            if (additionalDamage > 0) {
                lore.add(" " + ChatColor.GOLD + formattedAddDamage + " " + ATTRIBUTE_ADDITIONAL_DAMAGE_LORE_SURFIX);
            }

            if (additionalDamagePercentage > 1.0) {
                lore.add(" " + ChatColor.GOLD + formattedAddPercentage + "% " + ATTRIBUTE_ADDITIONAL_DAMAGE_PERCENTAGE_LORE_SURFIX);
            }
        }

        if (type.name().endsWith("_HELMET") || type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") || type.name().endsWith("_BOOTS")) {
            double reducibleDamage = ArmorCalculator.getReducibleDamage(item, stars);
            double reducibleDamagePercentage = ArmorCalculator.getReducibleDamagePercentage(item, stars);

            String formattedReduceDamage = String.format("%.1f", reducibleDamage);
            String formattedReduceDamagePercentage = String.format("%.1f", (reducibleDamagePercentage) * 100);

            if (reducibleDamage > 0) {
                lore.add(" " + ChatColor.GOLD + formattedReduceDamage + " " + ATTRIBUTE_REDUCIBLE_DAMAGE_LORE_SURFIX);
            }

            if (reducibleDamagePercentage > 0) {
                lore.add(" " + ChatColor.GOLD + formattedReduceDamagePercentage + "% " + ATTRIBUTE_REDUCIBLE_DAMAGE_PERCENTAGE_LORE_SURFIX);
            }
        }


        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
