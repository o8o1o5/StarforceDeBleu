package dev.o8o1o5.starforceDeBleu.manager;

import dev.o8o1o5.starforceDeBleu.data.StarforceLevel;
import dev.o8o1o5.starforceDeBleu.util.StarforceDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

        long cost = calculateCost(currentStars); // 필요한 에메랄드 개수

        ItemStack resultItem = item.clone();

        // 에메랄드 확인 및 차감 로직 시작
        PlayerInventory playerInventory = player.getInventory();

        if (!tryRemoveEmeralds(playerInventory, cost)) {
            player.sendMessage(ChatColor.RED + "강화에 필요한 에메랄드가 부족합니다! (필요: " + cost + "개)");
            return resultItem;
        }
        player.sendMessage(ChatColor.YELLOW + "인벤토리에서 에메랄드 " + cost + "개가 차감되었습니다.");
        // 끝

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
            player.sendMessage(ChatColor.DARK_RED + "강화 파괴! 아이템의 스타포스가 초기화되었습니다.");
            StarforceDataUtil.setStars(resultItem, 0);
        }

        return resultItem;
    }

    private boolean tryRemoveEmeralds(PlayerInventory inventory, long amountToRemove) {
        if (amountToRemove <= 0){
            return true;
        }

        long totalEmeralds = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.EMERALD) {
                totalEmeralds += item.getAmount();
            }
        }

        if (totalEmeralds < amountToRemove) {
            return true;
        }

        ItemStack[] contents = inventory.getContents();
        long remainingToRemove = amountToRemove;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item != null && item.getType() == Material.EMERALD) {
                int currentStackAmount = item.getAmount();

                if (currentStackAmount >= remainingToRemove) {
                    item.setAmount((int) (currentStackAmount - remainingToRemove));
                    remainingToRemove = 0;
                    break;
                } else {
                    remainingToRemove -= currentStackAmount;
                    inventory.setItem(i, null);
                }
            }
        }

        return remainingToRemove == 0;
    }

    public static long calculateCost(int stars) {
        double S = (double) stars;

        // 새로운 수식: 25 * (S + 1)^4
        double costDouble = 25.0 * Math.pow(S + 1, 4.0);

        // 천원 단위로 반올림 대신, 에메랄드 개수로 사용되므로 정수로 변환
        long cost = (long) Math.ceil(costDouble / 20000.0); // 1000원당 1 에메랄드로 가정, 올림

        // 최소 1 에메랄드
        if (cost < 1) {
            cost = 1;
        }

        return cost;
    }
}