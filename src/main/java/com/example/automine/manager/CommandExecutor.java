package com.example.automine.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import com.example.automine.config.ModConfig;
import com.example.automine.util.ModLogger;
import com.example.automine.util.TaskManager;

import java.util.List;

public class CommandExecutor {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static boolean isExecuting = false;
    private static String currentTaskId = null;

    public static void executeCommandSequence() {
        if (isExecuting) {
            ModLogger.warn("Command sequence already executing.");
            return;
        }

        List<ModConfig.CommandEntry> sequence = ModConfig.commandSequence;
        if (sequence.isEmpty()) {
            ModLogger.info("No command sequence configured.");
            return;
        }

        isExecuting = true;
        ModLogger.info("Starting command execution with " + sequence.size() + " commands.");

        currentTaskId = TaskManager.submitTask(() -> {
            try {
                for (int i = 0; i < sequence.size(); i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        ModLogger.info("Command execution cancelled at command " + (i + 1));
                        break;
                    }

                    ModConfig.CommandEntry entry = sequence.get(i);
                    String command = entry.command;
                    int delay = entry.delay;

                    executeCommand(command);

                    if (delay > 0 && i < sequence.size() - 1) {
                        ModLogger.info("Waiting " + delay + "ms before next command...");
                        Thread.sleep(delay);
                    }
                }
                ModLogger.info("Command sequence completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ModLogger.info("Command execution interrupted.");
            } catch (Exception e) {
                ModLogger.error("Error executing command: " + e.getMessage());
            } finally {
                isExecuting = false;
            }
        });
    }

    public static void executeCommand(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }

        try {
            mc.player.sendChatMessage(command);
            ModLogger.info("Executed: " + command);
        } catch (Exception e) {
            ModLogger.error("Failed to execute command: " + e.getMessage());
        }
    }

    public static void cancelExecution() {
        if (currentTaskId != null) {
            TaskManager.cancelTask(currentTaskId);
            currentTaskId = null;
        }
        isExecuting = false;
        ModLogger.info("Command execution cancelled.");
    }

    public static boolean isExecuting() {
        return isExecuting;
    }

    public static void reset() {
        cancelExecution();
    }
}
