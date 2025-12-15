package dev.o8o1o5.starforceDeBleu.listener;

import dev.o8o1o5.starforceDeBleu.StarforceDeBleu;
import dev.o8o1o5.starforceDeBleu.gui.StarforceGUI;
import dev.o8o1o5.starforceDeBleu.manager.LogicManager;
import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor; // ChatColor 임포트 유지
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class StarforceGUIListener implements Listener {
    private final LogicManager starforceManager;
    private final StarforceDeBleu plugin;

    public StarforceGUIListener(LogicManager starforceManager, StarforceDeBleu plugin) {
        this.starforceManager = starforceManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof StarforceGUI)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        StarforceGUI gui = (StarforceGUI) event.getInventory().getHolder();

        // 1. GUI 자체의 슬롯 (상단 인벤토리 영역) 클릭 처리
        if (event.getRawSlot() < gui.getInventory().getSize()) {
            event.setCancelled(true);

            int clickedSlot = event.getRawSlot();
            ItemStack clickedItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            // 1-1. 강화 슬롯 (ENHANCE_SLOT)에 아이템 넣기/빼기 처리
            if (clickedSlot == StarforceGUI.ENHANCE_SLOT) {
                // 커서에 아이템이 있고, 강화 슬롯으로 아이템을 드래그하여 넣을 때
                if (cursorItem != null && !cursorItem.getType().equals(Material.AIR)) {
                    if (!LogicManager.STARFORCABLE_MATERIAL.contains(cursorItem.getType())) {
                        player.sendMessage(ChatColor.RED + "강화 가능한 장비만 올려놓을 수 있습니다."); // 메시지 변경
                        event.setCancelled(true);
                        return;
                    }
                    event.setCancelled(false);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        gui.updatePreviewAndInfo(event.getInventory().getItem(StarforceGUI.ENHANCE_SLOT));
                    }, 1L);
                }
                // 강화 슬롯의 아이템을 플레이어 인벤토리로 꺼내려는 시도 (커서에 아이템이 없을 때)
                else if (clickedItem != null && !clickedItem.getType().equals(Material.AIR) && (cursorItem == null || cursorItem.getType().equals(Material.AIR))) {
                    event.setCancelled(false);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        gui.updatePreviewAndInfo(null);
                    }, 1L);
                }
            }
            // 1-2. 강화 버튼 (ENHANCE_BUTTON_SLOT) 클릭 처리
            else if (clickedSlot == StarforceGUI.ENHANCE_BUTTON_SLOT) {
                ItemStack itemToEnhance = gui.getInventory().getItem(StarforceGUI.ENHANCE_SLOT);

                if (itemToEnhance == null || itemToEnhance.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "강화할 아이템을 강화 슬롯에 올려주세요."); // 메시지 변경
                    return;
                }

                if (!LogicManager.STARFORCABLE_MATERIAL.contains(itemToEnhance.getType())) {
                    player.sendMessage(ChatColor.RED + "강화 가능한 장비만 강화할 수 있습니다."); // 메시지 변경
                    return;
                }

                int currentStars = DataUtil.getStars(itemToEnhance);
                if (currentStars >= DataUtil.MAX_STARFORCE_LEVEL) {
                    player.sendMessage(ChatColor.RED + "이 아이템은 최대 강화 레벨에 도달하여 더 이상 강화할 수 없습니다."); // 메시지 변경
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    ItemStack resultItem = starforceManager.processStarforce(player, itemToEnhance);

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        // 이제 resultItem 이 null 이 될 가능성이 제거되었습니다.
                        if (resultItem != null) {
                            gui.getInventory().setItem(StarforceGUI.ENHANCE_SLOT, resultItem);
                            gui.updatePreviewAndInfo(resultItem);
                            // 참고: 파괴 메세지는 Starforce Manager 가 이미 전송했습니다.
                        } else {
                            // 오류로 인해 null 이 반환되면 슬롯을 비웁니다.
                            gui.getInventory().setItem(StarforceGUI.ENHANCE_SLOT, null);
                            gui.updatePreviewAndInfo(null);
                        }
                    });
                });
            } else if (clickedSlot == StarforceGUI.PREVIEW_SLOT ||
                    clickedSlot == StarforceGUI.CHANCE_INFO_SLOT ||
                    clickedSlot == StarforceGUI.COST_INFO_SLOT) {
                event.setCancelled(true);
            }
        }
        // 2. 플레이어 인벤토리 영역 (하단 인벤토리 영역) 클릭 처리
        else {
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                ItemStack clickedInPlayerInv = event.getCurrentItem();
                if (clickedInPlayerInv != null && !clickedInPlayerInv.getType().equals(Material.AIR)) {
                    if (!LogicManager.STARFORCABLE_MATERIAL.contains(clickedInPlayerInv.getType())) {
                        player.sendMessage(ChatColor.RED + "강화 가능한 장비만 강화 슬롯에 넣을 수 있습니다."); // 메시지 변경
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(false);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        ItemStack itemInEnhanceSlot = gui.getInventory().getItem(StarforceGUI.ENHANCE_SLOT);
                        if (itemInEnhanceSlot != null && !itemInEnhanceSlot.getType().equals(Material.AIR)) {
                            gui.updatePreviewAndInfo(itemInEnhanceSlot);
                        } else {
                            gui.updatePreviewAndInfo(null);
                        }
                    }, 1L);
                    return;
                }
            }
            event.setCancelled(false);
        }
    }

    /**
     * 스타포스 GUI가 닫혔을 때 강화 슬롯에 남아있는 아이템을 플레이어에게 돌려줍니다.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof StarforceGUI)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        StarforceGUI gui = (StarforceGUI) event.getInventory().getHolder();

        ItemStack itemInSlot = gui.getInventory().getItem(StarforceGUI.ENHANCE_SLOT);
        if (itemInSlot != null && !itemInSlot.getType().equals(Material.AIR)) {
            player.getInventory().addItem(itemInSlot)
                    .forEach((index, item) -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            gui.getInventory().setItem(StarforceGUI.ENHANCE_SLOT, null);
        }
    }
}