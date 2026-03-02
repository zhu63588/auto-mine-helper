package com.example.automine;

import com.example.automine.manager.CommandExecutor;
import com.example.automine.manager.InventoryManager;
import com.example.automine.manager.MoveCheckManager;
import com.example.automine.manager.RespawnManager;
import com.example.automine.util.ModLogger;
import com.example.automine.util.TaskManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class AutoMineClient implements ClientModInitializer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    
    private static final int INVENTORY_CHECK_INTERVAL = 20; // 每秒检查一次（20 ticks）
    private static final int INVENTORY_DETECTION_MODE_SLOT_COUNT = 36;
    
    private static int tickCounter = 0;
    private static boolean isGoingHome = false;
    private static boolean isProcessingRespawn = false;
    private static String currentTaskName = null;
    
    @Override
    public void onInitializeClient() {
        ClientTickCallback.EVENT.register(clientInstance -> {
            if (clientInstance.player == null || clientInstance.world == null) return;
            
            tickCounter++;
            
            // 检测死亡和重生
            checkPlayerLifecycle();
            
            // 背包满检测
            checkInventoryFull();
            
            // 检查任务取消
            checkTaskCancellation();
        });
        
        ModLogger.info("Client initialized");
    }
    
    private static void checkPlayerLifecycle() {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        
        // 检测玩家死亡
        if (player.getHealth() <= 0 && !RespawnManager.hasDied() && !isProcessingRespawn) {
            handlePlayerDeath(player);
        }
        
        // 处理重生
        if (RespawnManager.hasDied() && player.isAlive() && !isProcessingRespawn) {
            handlePlayerRespawn(player);
        }
    }
    
    private static void handlePlayerDeath(ClientPlayerEntity player) {
        if (!AutoMineMod.config.autoRespawnEnabled) return;
        
        ModLogger.sendPlayerMessage("检测到死亡，准备重生...");
        isProcessingRespawn = true;
        
        // 取消当前正在执行的任务
        cancelCurrentTask();
        
        RespawnManager.onDeath();
    }
    
    private static void handlePlayerRespawn(ClientPlayerEntity player) {
        if (!AutoMineMod.config.autoRespawnEnabled) {
            isProcessingRespawn = false;
            return;
        }
        
        ModLogger.sendPlayerMessage("已重生，正在返回矿区...");
        
        // 请求重生
        RespawnManager.requestRespawn();
        RespawnManager.resetDeathFlag();
        
        // 延迟后执行返回指令
        submitDelayedTask("respawn-return", 2000, () -> {
            if (!TaskManager.isTaskCancelled("respawn-return")) {
                ModLogger.sendPlayerMessage("执行返回矿区指令...");
                CommandExecutor.executeReturnCommands();
                
                // 进一步延迟后开始挖矿
                submitDelayedTask("respawn-mine", 2000, () -> {
                    if (!TaskManager.isTaskCancelled("respawn-mine")) {
                        ModLogger.sendPlayerMessage("继续自动挖矿...");
                        CommandExecutor.executeMineCommands();
                    }
                });
            }
        });
        
        isProcessingRespawn = false;
    }
    
    private static void checkInventoryFull() {
        if (!AutoMineMod.config.autoHomeEnabled || isGoingHome) return;
        
        if (tickCounter % INVENTORY_CHECK_INTERVAL == 0 && InventoryManager.isInventoryFull()) {
            handleInventoryFull();
        }
    }
    
    private static void handleInventoryFull() {
        isGoingHome = true;
        currentTaskName = "inventory-home";
        
        ModLogger.sendPlayerMessage("背包已满，开始回家...");
        
        // 取消之前的任务
        cancelCurrentTask();
        
        // 执行回家指令
        CommandExecutor.executeHomeCommands();
        
        // 延迟后执行返回和挖矿
        submitDelayedTask("inventory-return", 5000, () -> {
            if (!TaskManager.isTaskCancelled("inventory-return")) {
                ModLogger.sendPlayerMessage("正在返回矿区...");
                CommandExecutor.executeReturnCommands();
                
                submitDelayedTask("inventory-mine", 2000, () -> {
                    if (!TaskManager.isTaskCancelled("inventory-mine")) {
                        ModLogger.sendPlayerMessage("继续自动挖矿...");
                        CommandExecutor.executeMineCommands();
                        isGoingHome = false;
                        currentTaskName = null;
                    }
                });
            }
        });
    }
    
    private static void checkTaskCancellation() {
        if (currentTaskName != null && TaskManager.isTaskCancelled(currentTaskName)) {
            ModLogger.warn("Current task was cancelled: " + currentTaskName);
            isGoingHome = false;
            currentTaskName = null;
        }
    }
    
    private static void submitDelayedTask(String taskName, long delayMillis, Runnable task) {
        TaskManager.submitTask(taskName, () -> {
            try {
                if (!TaskManager.isTaskCancelled(taskName)) {
                    Thread.sleep(delayMillis);
                    if (!TaskManager.isTaskCancelled(taskName)) {
                        task.run();
                    }
                }
            } catch (InterruptedException e) {
                ModLogger.debug("Task " + taskName + " was interrupted");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                ModLogger.error("Task " + taskName + " failed", e);
            }
        });
    }
    
    private static void cancelCurrentTask() {
        if (currentTaskName != null) {
            TaskManager.cancelTask(currentTaskName);
        }
    }
    
    public static void cancelAllOperations() {
        cancelCurrentTask();
        TaskManager.cancelAllTasks();
        isGoingHome = false;
        isProcessingRespawn = false;
        currentTaskName = null;
        ModLogger.info("All operations cancelled");
    }
    
    public static boolean isOperating() {
        return isGoingHome || isProcessingRespawn || currentTaskName != null;
    }
}
