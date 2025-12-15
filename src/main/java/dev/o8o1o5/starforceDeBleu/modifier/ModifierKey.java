package dev.o8o1o5.starforceDeBleu.modifier;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ModifierKey {
    FIXED_DAMAGE("starforce_modifier_fixed_damage"),
    PERCENTAGE_DAMAGE("starforce_modifier_percentage_damage");

    private static JavaPlugin plugin;
    private final String keyName;
    private NamespacedKey namespacedKey;

    ModifierKey(String keyName) {
        this.keyName = keyName;
    }

    public static void initialize(JavaPlugin p) {
        plugin = p;
        if (plugin == null) return;

        for (ModifierKey key : values()) {
            key.namespacedKey = new NamespacedKey(plugin, key.keyName);
        }
    }

    public NamespacedKey getKey() {
        if (namespacedKey == null) {
            throw new IllegalStateException("ModifierKey가 초기화되지 않았습니다.");
        }
        return namespacedKey;
    }

    public static List<NamespacedKey> getAllModifierKeys() {
        return Arrays.stream(values())
                .map(ModifierKey::getKey)
                .collect(Collectors.toList());
    }
}
