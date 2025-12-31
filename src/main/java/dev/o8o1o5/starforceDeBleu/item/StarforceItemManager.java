package dev.o8o1o5.starforceDeBleu.item;

import dev.o8o1o5.myTextures.api.MyTexturesAPI;
import dev.o8o1o5.myTextures.api.TexturesItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class StarforceItemManager {

    public static final String NETHER_STAR_FRAGMENT = "nether_star_fragment";

    public void registerItems() {
        // 네더의 별 파편
        MyTexturesAPI.registerItem(new TexturesItemBuilder(NETHER_STAR_FRAGMENT)
                .material(Material.PAPER)
                .name("네더의 별 파편")
                .addLore(ChatColor.GRAY + "잘게 부서진 위더의 심장")
                .addLore(ChatColor.GRAY + "비록 파편화되었지만 찬란히 빛난다")
                .shining(true));
    }

    public ItemStack createItem(String id, int amount) {
        ItemStack item = MyTexturesAPI.createItem(id);
        if (item == null) return null;

        item.setAmount(amount);

        return item;
    }

    public List<String> getAllItemIds() {
        return Arrays.asList(NETHER_STAR_FRAGMENT);
    }
}
