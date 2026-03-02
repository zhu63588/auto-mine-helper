package com.example.automine.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.automine.config.ModConfig;
import com.example.automine.manager.*;
import com.example.automine.util.ModLogger;

public class AutoMineCommand extends CommandBase {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static Pattern DELAY_PATTERN = Pattern.compile("^\\s*%\\s*(\\d+)\\s*$");

    @Override
    public String getName() {
        return "am";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/am <subcommand>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;

            case "toggle":
                cmdToggle(sender);
                break;

            case "home":
                cmdHome(sender);
                break;

            case "respawn":
                cmdRespawn(sender, args);
                break;

            case "movecheck":
                cmdMoveCheck(sender, args);
                break;

            case "run":
                cmdRun(sender);
                break;

            case "addcmd":
                cmdAddCmd(sender, args);
                break;

            case "clearcmd":
                cmdClearCmd(sender);
                break;

            case "listcmd":
                cmdListCmd(sender);
                break;

            case "whitelist":
                cmdWhitelist(sender, args);
                break;

            case "chest":
                cmdChest(sender, args);
                break;

            case "config":
                cmdConfig(sender, args);
                break;

            default:
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown command. Use /am help"));
                break;
        }
    }

    private void sendHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "=== Auto Mine Helper Commands ==="));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am help" + TextFormatting.WHITE + " - Show this help"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am toggle" + TextFormatting.WHITE + " - Toggle mod on/off"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am home" + TextFormatting.WHITE + " - Return home"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am respawn <on|off>" + TextFormatting.WHITE + " - Toggle auto respawn"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am movecheck <on|off>" + TextFormatting.WHITE + " - Toggle move check"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am run" + TextFormatting.WHITE + " - Execute command sequence"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am addcmd <command>" + TextFormatting.WHITE + " - Add command (use %N for delay)"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am clearcmd" + TextFormatting.WHITE + " - Clear command sequence"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am listcmd" + TextFormatting.WHITE + " - List commands"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am whitelist <add|remove|list|toggle> [item]" + TextFormatting.WHITE + " - Manage whitelist"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am chest <list|set|find> [name|radius]" + TextFormatting.WHITE + " - Manage chests"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/am config <key> <value>" + TextFormatting.WHITE + " - Set config"));
    }

    private void cmdToggle(ICommandSender sender) {
        ModConfig.enabled = !ModConfig.enabled;
        ModConfig.save();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Mod " + (ModConfig.enabled ? "enabled" : "disabled")));
        ModLogger.info("Mod " + (ModConfig.enabled ? "enabled" : "disabled") + " via command.");
    }

    private void cmdHome(ICommandSender sender) {
        if (!ModConfig.enabled) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Mod is disabled. Enable it first with /am toggle"));
            return;
        }

        CommandExecutor.executeCommand("/home");
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Returning home..."));
        ModLogger.info("Returning home via command.");
    }

    private void cmdRespawn(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am respawn <on|off>"));
            return;
        }

        boolean value = parseBoolean(args[1]);
        ModConfig.autoRespawn = value;
        ModConfig.save();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Auto respawn " + (value ? "enabled" : "disabled")));
        ModLogger.info("Auto respawn " + (value ? "enabled" : "disabled") + " via command.");
    }

    private void cmdMoveCheck(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am movecheck <on|off>"));
            return;
        }

        boolean value = parseBoolean(args[1]);
        ModConfig.moveCheck = value;
        ModConfig.save();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Move check " + (value ? "enabled" : "disabled")));
        ModLogger.info("Move check " + (value ? "enabled" : "disabled") + " via command.");
    }

    private void cmdRun(ICommandSender sender) {
        if (!ModConfig.enabled) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Mod is disabled. Enable it first with /am toggle"));
            return;
        }

        CommandExecutor.executeCommandSequence();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Executing command sequence..."));
    }

    private void cmdAddCmd(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am addcmd <command> (use %N for delay)"));
            return;
        }

        StringBuilder cmdBuilder = new StringBuilder();
        int delay = 0;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            Matcher matcher = DELAY_PATTERN.matcher(arg);

            if (matcher.matches()) {
                delay = Integer.parseInt(matcher.group(1));
            } else {
                if (cmdBuilder.length() > 0) {
                    cmdBuilder.append(" ");
                }
                cmdBuilder.append(arg);
            }
        }

        String command = cmdBuilder.toString();
        ModConfig.commandSequence.add(new ModConfig.CommandEntry(command, delay));
        ModConfig.save();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Added command: " + command + " (delay: " + delay + "ms)"));
        ModLogger.info("Added command: " + command + " (delay: " + delay + "ms)");
    }

    private void cmdClearCmd(ICommandSender sender) {
        int count = ModConfig.commandSequence.size();
        ModConfig.commandSequence.clear();
        ModConfig.save();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Cleared " + count + " commands"));
        ModLogger.info("Cleared " + count + " commands");
    }

    private void cmdListCmd(ICommandSender sender) {
        if (ModConfig.commandSequence.isEmpty()) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "No commands in sequence"));
            return;
        }

        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "=== Command Sequence ==="));
        for (int i = 0; i < ModConfig.commandSequence.size(); i++) {
            ModConfig.CommandEntry entry = ModConfig.commandSequence.get(i);
            sender.sendMessage(new TextComponentString(
                TextFormatting.WHITE + (i + 1) + ". " + entry.command + 
                (entry.delay > 0 ? " (delay: " + entry.delay + "ms)" : "")
            ));
        }
    }

    private void cmdWhitelist(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am whitelist <add|remove|list|toggle> [item]"));
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am whitelist add <item>"));
                    return;
                }
                String itemToAdd = args[2];
                ModConfig.whitelistItems.add(itemToAdd);
                ModConfig.save();
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Added " + itemToAdd + " to whitelist"));
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am whitelist remove <item>"));
                    return;
                }
                String itemToRemove = args[2];
                if (ModConfig.whitelistItems.remove(itemToRemove)) {
                    ModConfig.save();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Removed " + itemToRemove + " from whitelist"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + itemToRemove + " not in whitelist"));
                }
                break;

            case "list":
                if (ModConfig.whitelistItems.isEmpty()) {
                    sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Whitelist is empty"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "=== Whitelist ==="));
                    for (String item : ModConfig.whitelistItems) {
                        sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "- " + item));
                    }
                }
                break;

            case "toggle":
                ModConfig.whitelistEnabled = !ModConfig.whitelistEnabled;
                ModConfig.save();
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Whitelist " + (ModConfig.whitelistEnabled ? "enabled" : "disabled")));
                break;

            default:
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown action. Use: add, remove, list, toggle"));
                break;
        }
    }

    private void cmdChest(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am chest <list|set|find|clear|remove> [name|radius]"));
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "list":
                List<String> chests = ChestManager.listChests();
                if (chests.isEmpty()) {
                    sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "No saved chests"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "=== Saved Chests ==="));
                    for (String chest : chests) {
                        BlockPos pos = ChestManager.getChestLocation(chest);
                        sender.sendMessage(new TextComponentString(TextFormatting.WHITE + chest + ": " + pos));
                    }
                }
                break;

            case "set":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am chest set <name>"));
                    return;
                }
                String name = args[2];
                BlockPos playerPos = mc.player.getPosition();
                ChestManager.setChestLocation(name, playerPos);
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Set chest '" + name + "' at your location"));
                break;

            case "find":
                double radius = 10.0;
                if (args.length >= 3) {
                    try {
                        radius = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid radius"));
                        return;
                    }
                }
                List<BlockPos> foundChests = ChestManager.findNearbyChests(radius);
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Found " + foundChests.size() + " chests within " + radius + " blocks"));
                for (BlockPos pos : foundChests) {
                    sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "- " + pos));
                }
                break;

            case "clear":
                ChestManager.clearChestLocations();
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Cleared all saved chests"));
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am chest remove <name>"));
                    return;
                }
                String chestToRemove = args[2];
                ChestManager.removeChestLocation(chestToRemove);
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Removed chest '" + chestToRemove + "'"));
                break;

            default:
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown action. Use: list, set, find, clear, remove"));
                break;
        }
    }

    private void cmdConfig(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /am config <key> <value>"));
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Available keys: enabled, autoRespawn, moveCheck, whitelistEnabled, inventoryCheckMode"));
            return;
        }

        String key = args[1];
        String value = args[2];

        switch (key) {
            case "enabled":
                ModConfig.enabled = parseBoolean(value);
                break;
            case "autoRespawn":
                ModConfig.autoRespawn = parseBoolean(value);
                break;
            case "moveCheck":
                ModConfig.moveCheck = parseBoolean(value);
                break;
            case "whitelistEnabled":
                ModConfig.whitelistEnabled = parseBoolean(value);
                break;
            case "inventoryCheckMode":
                try {
                    ModConfig.inventoryCheckMode = Integer.parseInt(value);
                    if (ModConfig.inventoryCheckMode < 0 || ModConfig.inventoryCheckMode > 1) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "inventoryCheckMode must be 0 or 1"));
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value for inventoryCheckMode"));
                    return;
                }
                break;
            default:
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown config key: " + key));
                return;
        }

        ModConfig.save();
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Set " + key + " = " + value));
    }

    private boolean parseBoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value.equalsIgnoreCase("1");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("toggle");
            completions.add("home");
            completions.add("respawn");
            completions.add("movecheck");
            completions.add("run");
            completions.add("addcmd");
            completions.add("clearcmd");
            completions.add("listcmd");
            completions.add("whitelist");
            completions.add("chest");
            completions.add("config");
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "respawn":
                case "movecheck":
                    completions.add("on");
                    completions.add("off");
                    break;
                case "whitelist":
                    completions.add("add");
                    completions.add("remove");
                    completions.add("list");
                    completions.add("toggle");
                    break;
                case "chest":
                    completions.add("list");
                    completions.add("set");
                    completions.add("find");
                    completions.add("clear");
                    completions.add("remove");
                    break;
            }
        }

        return completions;
    }
}
