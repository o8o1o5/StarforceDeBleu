package dev.o8o1o5.starforceDeBleu.manager;

import dev.o8o1o5.starforceDeBleu.util.StarforceConstants;
import dev.o8o1o5.starforceDeBleu.util.calculator.SwordCalculator;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

public class StarforceAttributeApplier {

    private void applySwordAttributes(ItemAttributeModifiers.Builder builder, ItemStack item, int stars) {
        double baseDamage = SwordCalculator.getAdditionalDamage(item, stars);

        AttributeModifier damageModifier = new AttributeModifier(
                StarforceConstants.SWORD_DAMAGE_KEY,
                baseDamage,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        builder.addModifier(Attribute.ATTACK_DAMAGE, damageModifier, EquipmentSlotGroup.MAINHAND);

        double rawMultiplier = SwordCalculator.getAdditionalDamagePercentage(item, stars) - 1.0;

        AttributeModifier damagePercentageModifier = new AttributeModifier(
                StarforceConstants.SWORD_DAMAGE_KEY,
                rawMultiplier,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                EquipmentSlotGroup.MAINHAND
        )
    }
}
