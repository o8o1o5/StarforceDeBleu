package dev.o8o1o5.starforceDeBleu.modifier;

import com.google.common.collect.Multimap;
import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import dev.o8o1o5.starforceDeBleu.util.calculator.SwordCalculator;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeApplier {

    /**
     * 아이템의 현재 별 레벨에 따라 AttributeModifier를 적용/업데이트 합니다.
     * 이 클래스는 Modifier 조작만을 전담합니다.
     * @param item 대상 ItemStack
     * @param level 스타포스 레벨
     */
    public static void applyModifiers(ItemStack item, int level) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();

        // 기존 스타포스 Modifier 제거 - NamespacedKey 에 의한 식별
        Multimap<Attribute, AttributeModifier> existingModifiers = meta.getAttributeModifiers();

        if (existingModifiers != null) {
            List<Map.Entry<Attribute, AttributeModifier>> modifiersToRemove = new ArrayList<>();

            List<NamespacedKey> starforceKeys = ModifierKey.getAllModifierKeys();

            for (Attribute attribute : existingModifiers.keySet()) {
                for (AttributeModifier modifier : existingModifiers.get(attribute)) {
                    if (starforceKeys.contains(modifier.getKey())) {
                        modifiersToRemove.add(new AbstractMap.SimpleEntry<>(attribute, modifier));
                    }
                }
            }

            for (Map.Entry<Attribute, AttributeModifier> entry : modifiersToRemove) {
                meta.removeAttributeModifier(entry.getKey(), entry.getValue());
            }
        }

        // 새로운 Starforce AttributeModifier 생성 및 적용
        if (level > 0) {

            // 1. SWORD 속성
            if (item.getType().name().endsWith("_SWORD")) {

                // (1) 고정 수치 증가 - 공격력
                double additionalDamage = SwordCalculator.getAdditionalDamage(item, level);

                AttributeModifier fixedDamageMod = new AttributeModifier(
                        ModifierKey.FIXED_DAMAGE.getKey(),
                        additionalDamage,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND
                );
                meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, fixedDamageMod);

                // (2) 비율 수치 증가 - 총 공격력 %
                double totalMultiplier = SwordCalculator.getAdditionalDamagePercentage(item, level);
                double percentageIncreaseAmount = totalMultiplier - 1.0;

                AttributeModifier percentageDamageMod = new AttributeModifier(
                        ModifierKey.PERCENTAGE_DAMAGE.getKey(),
                        percentageIncreaseAmount,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                        EquipmentSlotGroup.MAINHAND
                );
                meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, percentageDamageMod);
            }

            // TODO: 방어구, 도구에 대한 Attribute Modifier

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
    }
}
