package dev.o8o1o5.starforceDeBleu.util;

import org.bukkit.NamespacedKey;

import java.util.UUID;

public class StarforceConstants {

    private static final String NAMESPACE = "starforce_de_bleu";

    public static final NamespacedKey SWORD_DAMAGE_KEY = new NamespacedKey(NAMESPACE, "sword_damage_boost");

    public static final NamespacedKey ARMOR_KEY = new NamespacedKey(NAMESPACE, "armor_defense_boost");

    private StarforceConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated.");
    }
}
