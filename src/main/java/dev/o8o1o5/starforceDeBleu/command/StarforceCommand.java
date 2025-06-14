package dev.o8o1o5.starforceDeBleu.command;

import dev.o8o1o5.starforceDeBleu.StarforceDeBleu;
import dev.o8o1o5.starforceDeBleu.util.ItemLoreDisplayUtil;
import dev.o8o1o5.starforceDeBleu.util.StarforceStarLoreUtil;
import dev.o8o1o5.starforceDeBleu.util.StarforceDataUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StarforceCommand implements CommandExecutor, TabCompleter {
    private final StarforceDeBleu plugin;

    public StarforceCommand(StarforceDeBleu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem == null || handItem.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "강화할 아이템을 손에 들어야 합니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "===== 스타포스 명령어 사용법 =====");
            player.sendMessage(ChatColor.YELLOW + "/starforce setstar <숫자[0-25]> - 손에 든 아이템의 스타포스 레벨을 설정합니다.");
            player.sendMessage(ChatColor.YELLOW + "/starforce getstar - 손에 든 아이템의 현재 스타포스 레벨을 확인합니다.");
            player.sendMessage(ChatColor.YELLOW + "/starforce setprocessed <true|false> - 아이템의 처리 플래그를 설정합니다.");
            player.sendMessage(ChatColor.YELLOW + "/starforce isprocessed - 아이템의 처리 플래그 상태를 확인합니다.");
            player.sendMessage(ChatColor.YELLOW + "/starforce setstarforcable <true|false> - 아이템의 강화 가능 플래그를 설정합니다."); //
            player.sendMessage(ChatColor.YELLOW + "/starforce isstarforcable - 아이템의 강화 가능 플래그 상태를 확인합니다.");
            // player.sendMessage(ChatColor.YELLOW + "/starforce enhance - 아이템의 강화를 시도합니다.");
            player.sendMessage(ChatColor.YELLOW + "================================");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "setstarforcable":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.YELLOW + "/starforce setstarforcable <true|false>");
                    return true;
                }
                boolean starforcable = Boolean.parseBoolean(args[1]);
                StarforceDataUtil.setStarforcable(handItem, starforcable);
                player.sendMessage(ChatColor.GREEN + "아이템의 starforcable 플래그가 " + starforcable + "(으)로 설정되었습니다.");
                return true;

            case "isstarforcable":
                boolean currentStarforcable = StarforceDataUtil.isStarforcable(handItem);
                player.sendMessage(ChatColor.AQUA + "현재 아이템의 starforcable 플래그는 " + currentStarforcable + " 상태입니다.");
                return true;
        }

        if (!StarforceDataUtil.isStarforcable(handItem)) {
            if (StarforceDataUtil.hasRelevantAttributeModifiers(handItem)) {
                StarforceDataUtil.setStarforcable(handItem, true);
                player.sendMessage(ChatColor.GREEN + "이 아이템은 강화 대상이므로, starforcable 플래그가 자동으로 부여되었습니다.");
            } else {
                player.sendMessage(ChatColor.RED + "이 아이템은 스타포스 강화 대상이 아닙니다. (무기, 방어구, 도구가 아님)");
                return true;
            }
        }

        switch (subCommand) {
            case "setstar":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.YELLOW + "/starforce setstar <숫자[1-25]>");
                    return true;
                }
                try {
                    int stars = Integer.parseInt(args[1]);
                    if (stars > 25 || stars < 0) {
                        player.sendMessage(ChatColor.RED + "스타포스 레벨은 0부터 " + StarforceDataUtil.MAX_STARFORCE_LEVEL + "까지만 설정할 수 있습니다.");
                        return true;
                    }
                    StarforceDataUtil.setStars(handItem, stars);
                    player.sendMessage(ChatColor.GREEN + handItem.getType().name() + " 아이템의 스타포스 레벨이 " + stars + "(으)로 설정되었습니다.");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "유효한 숫자를 입력하세요.");
                }
                break;

            case "getstar":
                int stars = StarforceDataUtil.getStars(handItem);
                player.sendMessage(ChatColor.AQUA + "현재 아이템의 스타포스 레벨은 " + stars + "입니다.");
                break;

            case "setprocessed":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.YELLOW + "/starforce setprocessed <true|false>");
                    return true;
                }
                boolean processed = Boolean.parseBoolean(args[1]);
                StarforceDataUtil.setProcessed(handItem, processed);
                player.sendMessage(ChatColor.GREEN + "스타포스 처리 플래그가 " + processed + "(으)로 설정되었습니다.");
                break;

            case "isprocessed":
                boolean isProcessed = StarforceDataUtil.isProcessed(handItem);
                player.sendMessage(ChatColor.AQUA + "현재 스타포스 처리 플래그는 " + isProcessed + " 상태입니다.");
                break;

            default:
                player.sendMessage(ChatColor.RED + "알 수 없는 하위 명령어입니다.");
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("setstar", "getstar", "setprocessed", "isprocessed", "setstarforcable", "isstarforcable");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "setprocessed":
                case "setstarforcable":
                    List<String> booleanOptions = Arrays.asList("true", "false");
                    for (String option : booleanOptions) {
                        if (option.startsWith(args[1].toLowerCase())) {
                            completions.add(option);
                        }
                    }
                    break;
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
