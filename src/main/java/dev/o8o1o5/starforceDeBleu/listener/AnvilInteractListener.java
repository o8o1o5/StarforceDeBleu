package dev.o8o1o5.starforceDeBleu.listener;

import dev.o8o1o5.starforceDeBleu.gui.StarforceGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null &&
                event.getClickedBlock().getType().name().endsWith("ANVIL")) {

            // 플레이어가 웅크리고 있는지 확인합니다.
            if (event.getPlayer().isSneaking()) {
                // 웅크린 상태일 때 (스타포스 GUI 또는 블록 설치)

                // 바닐라 모루 GUI가 열리는 것을 기본적으로 방지합니다.
                event.setCancelled(true);

                ItemStack itemInHand = event.getItem(); // 손에 든 아이템

                // 손에 블록을 들고 있는지 확인합니다.
                if (itemInHand != null && itemInHand.getType().isBlock()) {
                    // 손에 블록을 들고 있다면 블록을 설치합니다.
                    // event.setCancelled(false)로 바닐라 블록 배치 허용 (손에 든 아이템이 줄어드는 등)
                    event.setCancelled(false);
                } else {
                    // 손에 블록이 없고 웅크린 상태라면 Starforce GUI를 엽니다.
                    StarforceGUI starforceGUI = new StarforceGUI();
                    event.getPlayer().openInventory(starforceGUI.getInventory());
                }
            } else {
                // 웅크리지 않은 상태일 때 (바닐라 모루 GUI 열림)
                // 이 경우 event.setCancelled(false)를 하여 바닐라 동작을 허용합니다.
                // AnvilInteractEvent는 모루 UI가 열리는 것을 막지 않으므로, 이 부분을 명시적으로 true로 하지 않습니다.
                // 즉, 이 else 블록에서는 아무것도 하지 않음으로써 Bukkit의 기본 모루 열기 동작이 수행되도록 합니다.
                // event.setCancelled(false); // 이 코드는 명시적으로 작성할 필요는 없지만, 의도를 명확히 하기 위해 남겨둡니다.
            }
        }
    }
}
