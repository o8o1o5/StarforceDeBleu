package dev.o8o1o5.starforceDeBleu.manager;

import dev.o8o1o5.starforceDeBleu.StarforceDeBleu;
import dev.o8o1o5.starforceDeBleu.data.StarforceLevel;
import dev.o8o1o5.starforceDeBleu.util.StarforceDataUtil;
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

public class StarforceManager {
    private final Random random;

    public static final Set<Material> STARFORCABLE_MATERIAL = new HashSet<>(Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_SWORD,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS
    ));

    public StarforceManager() {
        this.random = new Random();
    }

    public ItemStack processStarforce(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "강화할 아이템이 없습니다.");
            return null;
        }

        int currentStars = StarforceDataUtil.getStars(item);
        StarforceLevel currentLevel = StarforceLevel.getLevel(currentStars);

        long cost = calculateCost(currentStars);

        Economy economy = StarforceDeBleu.getEconomy();

        ItemStack resultItem = item.clone();

        if (economy == null) {
            player.sendMessage(ChatColor.YELLOW + "경제 플러그인이 연동되지 않아 재화가 차감되지 않습니다.");
        }

        if (!economy.has(player, cost)) {
            player.sendMessage(ChatColor.RED + "강화에 필요한 재화가 부족합니다.");
            return resultItem;
        }

        EconomyResponse response = economy.withdrawPlayer(player, cost);

        if (!response.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "내부 오류로 인해 재화 차감에 실패했습니다: " + response.errorMessage);
            return resultItem;
        }

        int roll = random.nextInt(100) + 1;

        if (roll <= currentLevel.getSuccessRate()) {
            StarforceDataUtil.setStars(resultItem, StarforceDataUtil.getStars(item) + 1);
            player.sendMessage(ChatColor.GREEN + "강화 성공! 아이템의 스타포스가 " + StarforceDataUtil.getStars(resultItem) + "성이 되었습니다.");
        } else if (roll <= currentLevel.getSuccessRate() + currentLevel.getFailRate()) {
            if (currentStars <= 10 || currentStars == 15 || currentStars == 20) {
                player.sendMessage(ChatColor.RED + "강화 실패! 아이템의 스타포스가 유지됩니다.");
            } else {
                int newStars = Math.max(0, StarforceDataUtil.getStars(item) - 1);
                StarforceDataUtil.setStars(resultItem, newStars);
                player.sendMessage(ChatColor.RED + "강화 실패! 아이템의 스타포스가 " + StarforceDataUtil.getStars(resultItem) + "성으로 하락했습니다.");
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "강화 파괴! 아이템이 소멸했습니다.");
            resultItem = null;
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
