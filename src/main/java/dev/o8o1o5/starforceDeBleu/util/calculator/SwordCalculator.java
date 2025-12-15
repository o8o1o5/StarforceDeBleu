package dev.o8o1o5.starforceDeBleu.util.calculator;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

public class SwordCalculator {
    public static double getAdditionalDamage(ItemStack item, int stars) {
        double additionalDamage;

        if (stars <= 5) {
            additionalDamage = stars * 0.2;
        } else if (stars <= 10) {
            additionalDamage = (5 * 0.2) + ((stars - 5) * 0.3);
        } else if (stars <= 15) {
            additionalDamage = (5 * 0.2) + (5 * 0.3) + ((stars - 10) * 0.4);
        } else if (stars <= 20) {
            additionalDamage = (5 * 0.2) + (5 * 0.3) + (5 * 0.4) + ((stars - 15) * 0.5);
        } else {
            additionalDamage = (5 * 0.2) + (5 * 0.3) + (5 * 0.4) + (5 * 0.5) + ((stars - 20) * 0.6);
        }
        return additionalDamage;
    }

    public static double getAdditionalDamagePercentage(ItemStack item, int stars) {
        double percentageIncrease = SwordCalculator.getAdditionalDamagePercentage(item, stars);

        AttributeModifier damagePercentageModifier = new AttributeModifier(
                StarforceConstans.S
        )
    }
}
