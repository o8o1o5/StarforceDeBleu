package dev.o8o1o5.starforceDeBleu;

import dev.o8o1o5.starforceDeBleu.command.StarforceCommand;
import dev.o8o1o5.starforceDeBleu.listener.AnvilInteractListener;
import dev.o8o1o5.starforceDeBleu.listener.StarforceGUIListener;
import dev.o8o1o5.starforceDeBleu.manager.LogicManager;
import dev.o8o1o5.starforceDeBleu.modifier.ModifierKey;
import dev.o8o1o5.starforceDeBleu.util.DataUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarforceDeBleu extends JavaPlugin {
    private static Economy economy = null;
    private LogicManager logicManager;

    @Override
    public void onEnable() {
        DataUtil.initialize(this);
        ModifierKey.initialize(this);
        this.logicManager = new LogicManager();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!setupEconomy()) {
                getLogger().warning("Vault Economy이(가) 비활성화되었거나 찾을 수 없습니다. (경제 시스템과 연동되지 않습니다.)");
                // 경제 시스템 없이도 플러그인을 계속 실행하려면 여기에 추가 로직 필요
            } else {
                getLogger().info("Vault Economy와 성공적으로 연동되었습니다.");
            }
        }, 1L); // 1 틱 지연

        StarforceCommand starforceCommandHandler = new StarforceCommand(this);
        getCommand("starforce").setExecutor(starforceCommandHandler);
        getCommand("starforce").setTabCompleter(starforceCommandHandler);

        // 이는 AttributeModifier 를 활용하는 방법으로 통합되었습니다.
        // getServer().getPluginManager().registerEvents(new SwordListener(), this);
        // getServer().getPluginManager().registerEvents(new ArmorListener(), this);

        getServer().getPluginManager().registerEvents(new AnvilInteractListener(), this);
        getServer().getPluginManager().registerEvents(new StarforceGUIListener(logicManager, this), this);

        Bukkit.getLogger().info("스타포스 시스템이 활성화 되었습니다.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("스타포스 시스템이 비활성화 되었습니다.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
