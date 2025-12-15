package dev.o8o1o5.starforceDeBleu.manager;

import dev.o8o1o5.starforceDeBleu.StarforceDeBleu;
import dev.o8o1o5.starforceDeBleu.data.StarforceLevel;
import dev.o8o1o5.starforceDeBleu.modifier.AttributeApplier;
import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import dev.o8o1o5.starforceDeBleu.util.lore.ItemLoreDisplayUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LogicManager {
    private final Random random;

    public static final Set<Material> STARFORCABLE_MATERIAL = new HashSet<>(Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_SWORD,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS
    ));

    public LogicManager() {
        this.random = new Random();
    }

    public ItemStack processStarforce(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "강화할 아이템이 없습니다.");
            return null;
        }

        int currentStars = DataUtil.getStars(item);
        StarforceLevel currentLevel = StarforceLevel.getLevel(currentStars);

        long cost = calculateCost(currentStars);

        Economy economy = StarforceDeBleu.getEconomy();

        ItemStack resultItem = item.clone();

        if (economy == null) {
            player.sendMessage(ChatColor.YELLOW + "경제 플러그인이 연동되지 않아 재화가 차감되지 않습니다.");
        } else {
            if (!economy.has(player, cost)) {
                player.sendMessage(ChatColor.RED + "강화에 필요한 재화가 부족합니다.");
                return resultItem;
            }

            EconomyResponse response = economy.withdrawPlayer(player, cost);

            if (!response.transactionSuccess()) {
                player.sendMessage(ChatColor.RED + "내부 오류로 인해 재화 차감에 실패했습니다: " + response.errorMessage);
                return resultItem;
            }
        }

        int roll = random.nextInt(100) + 1;
        int newStars;

        if (roll <= currentLevel.getSuccessRate()) {
            // 성공: 스타포스 = 스타포스 + 1
            newStars = currentStars + 1;

            DataUtil.setStars(resultItem, newStars);
            AttributeApplier.applyModifiers(resultItem, newStars);
            ItemLoreDisplayUtil.updateItemLore(resultItem, newStars);

            player.sendMessage(ChatColor.GREEN + "강화 성공! 아이템의 스타포스가 " + newStars + "성이 되었습니다.");

        } else if (roll <= currentLevel.getSuccessRate() + currentLevel.getFailRate()) {
            // 실패: 스타포스 = 스타포스
            newStars = currentStars;

            // 스타 레벨이 변동이 없으므로, setStars, applyModifiers, updateItemLore 의 호출은 생략합니다.

            player.sendMessage(ChatColor.RED + "강화 실패! 아이템의 스타포스가 유지됩니다.");

        } else {
            // 파괴: 스타포스 = 0
            newStars = 0;

            DataUtil.setStars(resultItem, newStars);
            AttributeApplier.applyModifiers(resultItem, newStars);
            // updateItemLore 가 (ItemStack item, 0) 으로 호출되면
            // 적용되어있던 스타포스 관련 Lore 가 제거되고
            // 내부적으로 호출되는 addStarLore 와 addAttributeLore 가 작동하지 않게 되므로
            // 결과적으로 제거되는 효과를 만듭니다.
            ItemLoreDisplayUtil.updateItemLore(resultItem, newStars);

            player.sendMessage(ChatColor.DARK_RED + "강화 파괴! 아이템의 스타포스가 초기화되었습니다.");
        }

        return resultItem;

    }

    public static long calculateCost(int stars) {
        double S = (double) stars;

        // 새로운 수식: 25 * (S + 1)^4
        double costDouble = 25.0 * Math.pow(S + 1, 4.0);

        // 천원 단위로 반올림
        long cost = Math.round(costDouble / 1000.0) * 1000;

        return cost;
    }
}
