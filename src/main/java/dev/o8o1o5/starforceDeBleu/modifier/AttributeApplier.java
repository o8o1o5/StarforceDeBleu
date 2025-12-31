package dev.o8o1o5.starforceDeBleu.modifier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.o8o1o5.starforceDeBleu.util.calculator.SwordCalculator;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttributeApplier {

    /**
     * 아이템의 현재 별 레벨에 따라 AttributeModifier를 적용/업데이트 합니다.
     * @param item 대상 ItemStack
     * @param level 스타포스 레벨
     */
    public static void applyModifiers(ItemStack item, int level) {
        if (item == null || item.getType().isAir()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // 1. 기존 모든 Modifier 제거 및 바닐라 기본 스탯 복구 준비
        // 수동으로 Modifier를 추가하는 순간 바닐라 스탯이 사라지므로,
        // 바닐라 기본 스탯(Default)을 먼저 가져와서 유지시켜야 합니다.
        Multimap<Attribute, AttributeModifier> newModifiers = ArrayListMultimap.create();

        // 아이템의 기본(Vanilla) 속성들을 가져옵니다. (Paper/Spigot 최신 API)
        Multimap<Attribute, AttributeModifier> defaultModifiers = item.getType().getDefaultAttributeModifiers();

        // 2. 기존 Modifier 중 스타포스 관련 키만 필터링하여 제거
        Set<NamespacedKey> starforceKeys = new HashSet<>(ModifierKey.getAllModifierKeys());
        Multimap<Attribute, AttributeModifier> currentModifiers = meta.getAttributeModifiers();

        if (currentModifiers != null) {
            for (Map.Entry<Attribute, AttributeModifier> entry : currentModifiers.entries()) {
                // 스타포스 키가 아닌 것(유저가 수동으로 넣은 인챈트나 타 플러그인 속성)만 유지
                if (!starforceKeys.contains(entry.getValue().getKey())) {
                    newModifiers.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            // 기존 커스텀 Modifier가 없다면 바닐라 기본값을 기초로 시작
            newModifiers.putAll(defaultModifiers);
        }

        // 3. 새로운 Starforce AttributeModifier 추가 (level > 0 일 때만)
        if (level > 0) {
            String typeName = item.getType().name();

            if (typeName.endsWith("_SWORD")) {
                applySwordAttributes(newModifiers, item, level);
            }

            // 공통 적용: 속성 숨기기 (메이플 스타일 툴팁을 별도로 구현할 경우)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        // 4. 최종 Modifier 적용 및 저장 (level이 0이어도 삭제된 상태가 반영됨)
        meta.setAttributeModifiers(newModifiers);
        item.setItemMeta(meta);
    }

    private static void applySwordAttributes(Multimap<Attribute, AttributeModifier> modifiers, ItemStack item, int level) {
        // (1) 고정 수치 증가 - 공격력
        double additionalDamage = SwordCalculator.getAdditionalDamage(item, level);
        if (additionalDamage > 0) {
            modifiers.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                    ModifierKey.FIXED_DAMAGE.getKey(),
                    additionalDamage,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.MAINHAND
            ));
        }

        // (2) 비율 수치 증가 - 총 공격력 %
        double totalMultiplier = SwordCalculator.getAdditionalDamagePercentage(item, level);
        double percentageIncreaseAmount = totalMultiplier - 1.0;

        if (percentageIncreaseAmount > 0) {
            modifiers.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                    ModifierKey.PERCENTAGE_DAMAGE.getKey(),
                    percentageIncreaseAmount,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                    EquipmentSlotGroup.MAINHAND
            ));
        }
    }

    // TODO: 방어구, 도구 Attributes
}