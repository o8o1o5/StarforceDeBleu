package dev.o8o1o5.starforceDeBleu.gui;

import dev.o8o1o5.starforceDeBleu.StarforceDeBleu;
import dev.o8o1o5.starforceDeBleu.data.StarforceLevel;
import dev.o8o1o5.starforceDeBleu.manager.LogicManager;
import dev.o8o1o5.starforceDeBleu.modifier.AttributeApplier;
import dev.o8o1o5.starforceDeBleu.util.ItemBuilder;
import dev.o8o1o5.starforceDeBleu.util.lore.ItemLoreDisplayUtil;
import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StarforceGUI implements InventoryHolder {
    private final Inventory inventory;

    public static final int ENHANCE_SLOT = 10;
    public static final int PREVIEW_SLOT = 14;
    public static final int ENHANCE_BUTTON_SLOT = 25;
    public static final int CHANCE_INFO_SLOT = 19;
    public static final int COST_INFO_SLOT = 23;

    private boolean isProcessing = false;

    private final ItemStack enabledEnhanceButton;
    private final ItemStack disabledEnhanceButton;

    private final Economy economy = StarforceDeBleu.getEconomy();

    public StarforceGUI() {
        this.inventory = Bukkit.createInventory(this, 27, "스타포스 강화");

        this.enabledEnhanceButton = new ItemBuilder(Material.LIME_WOOL)
                .setName("강화하기")
                .setLore(Arrays.asList("클릭하여 아이템을 강화합니다."))
                .build();
        this.disabledEnhanceButton = new ItemBuilder(Material.RED_WOOL)
                .setName("강화 불가")
                .setLore(Arrays.asList("더 이상 강화할 수 없는 아이템입니다.", "(최대 강화 레벨 도달)"))
                .build();

        setupGUI();
    }

    private void setupGUI() {
        ItemStack backgroundPane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == ENHANCE_SLOT || i == PREVIEW_SLOT || i == ENHANCE_BUTTON_SLOT || 
                    i == CHANCE_INFO_SLOT || i == COST_INFO_SLOT) {
                continue;
            }
            inventory.setItem(i, backgroundPane);
        }

        inventory.setItem(ENHANCE_BUTTON_SLOT, enabledEnhanceButton);

        updateChanceInfo(0);
        updateCostInfo(0);
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void updatePreviewAndInfo(ItemStack itemToEnhance) {
        if (itemToEnhance == null || itemToEnhance.getType().equals(Material.AIR)) {
            inventory.setItem(PREVIEW_SLOT, null);
            updateChanceInfo(0);
            updateCostInfo(0);
            inventory.setItem(ENHANCE_BUTTON_SLOT, enabledEnhanceButton);
        } else {
            int currentStars = DataUtil.getStars(itemToEnhance);

            if (currentStars >= DataUtil.MAX_STARFORCE_LEVEL) {
                inventory.setItem(PREVIEW_SLOT, null);

                ItemStack maxLevelInfo = new ItemBuilder(Material.BARRIER)
                        .setName(ChatColor.RED + "최대 강화 레벨 도달!")
                        .setLore(Arrays.asList(
                                ChatColor.GRAY + "이 아이템은 더 이상 강화할 수 없습니다."
                        ))
                        .build();
                inventory.setItem(CHANCE_INFO_SLOT, maxLevelInfo);
                inventory.setItem(COST_INFO_SLOT, maxLevelInfo);

                inventory.setItem(ENHANCE_BUTTON_SLOT, disabledEnhanceButton);
            } else {
                int nextStars = currentStars + 1;
                ItemStack previewItem = itemToEnhance.clone();
                DataUtil.setStars(previewItem, nextStars); // 먼저 다음 스타포스 레벨 설정 (NBT 데이터)

                // --- 핵심 수정 부분: ItemLoreDisplayUtil을 호출하여 아이템 로어를 업데이트합니다. ---
                // ItemLoreDisplayUtil 내부에서 ItemMeta를 가져와 로어를 수정하고 다시 설정하므로,
                // 여기서는 별도의 ItemMeta 조작이 필요 없습니다.
                AttributeApplier.applyModifiers(previewItem, nextStars);
                ItemLoreDisplayUtil.updateItemLore(previewItem, nextStars);

                // 여기에 미리보기 전용 로어를 추가합니다.
                // ItemLoreDisplayUtil이 설정한 로어 위에 추가됩니다.
                ItemMeta previewMeta = previewItem.getItemMeta();
                if (previewMeta != null) {
                    List<String> lore = previewMeta.hasLore() ? new ArrayList<>(previewMeta.getLore()) : new ArrayList<>();
                    // 기존 로어의 맨 위에 "--- 강화 성공 시 미리보기 ---" 구분선을 추가합니다.
                    lore.add(0, ChatColor.BLUE + "--- 강화 성공 시 미리보기 ---");
                    previewMeta.setLore(lore);
                    previewItem.setItemMeta(previewMeta);
                }
                // -----------------------------------------------------------------------------

                inventory.setItem(PREVIEW_SLOT, previewItem);

                updateChanceInfo(currentStars);
                updateCostInfo(currentStars);

                inventory.setItem(ENHANCE_BUTTON_SLOT, enabledEnhanceButton);
            }
        }
    }

    private void updateChanceInfo(int currentStars) {
        StarforceLevel levelData = StarforceLevel.getLevel(currentStars);
        ItemStack chanceInfo = new ItemBuilder(Material.PAPER)
                .setName("강화 확률")
                .setLore(Arrays.asList(
                        ChatColor.GREEN + "성공: " + levelData.getSuccessRate() + "%",
                        ChatColor.RED + "실패: " + levelData.getFailRate() + "%",
                        ChatColor.DARK_RED + "파괴: " + levelData.getDestroyRate() + "%"
                ))
                .build();
        inventory.setItem(CHANCE_INFO_SLOT, chanceInfo);
    }

    private void updateCostInfo(int currentStars) {
        long cost = LogicManager.calculateCost(currentStars);

        List<String> lore = new ArrayList<>();

        if (economy == null) {
            lore.add(ChatColor.YELLOW + "Vault 연동 실패: 비용 확인 불가");
            lore.add(ChatColor.GRAY + "필요 비용: " + cost + " (추정)");
        } else {
            lore.add(economy.format(cost));
        }

        ItemStack costInfo = new ItemBuilder(Material.GOLD_INGOT)
                .setName("강화 비용")
                .setLore(lore) // 수정된 lore 리스트 사용
                .build();

        inventory.setItem(COST_INFO_SLOT, costInfo);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
}
