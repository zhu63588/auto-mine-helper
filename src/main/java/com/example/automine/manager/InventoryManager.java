package com.example.automine.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.example.automine.config.ModConfig;
import com.example.automine.util.ModLogger;

public class InventoryManager {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static boolean wasFull = false;
    private static boolean shouldTriggerHome = false;

    public static boolean isInventoryFull() {
        if (mc.player == null) {
            return false;
        }

        EntityPlayer player = mc.player;

        if (ModConfig.inventoryCheckMode == 0) {
            return isInventoryFull_SlotOccupied(player);
        } else {
            return isInventoryFull_StackFull(player);
        }
    }

    private static boolean isInventoryFull_SlotOccupied(EntityPlayer player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isInventoryFull_StackFull(EntityPlayer player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldTriggerAutoHome() {
        if (!ModConfig.enabled) {
            return false;
        }

        boolean isFull = isInventoryFull();

        if (isFull && !wasFull) {
            shouldTriggerHome = true;
            ModLogger.info("Inventory is full! Preparing to return home.");
        }

        wasFull = isFull;

        if (shouldTriggerHome) {
            shouldTriggerHome = false;
            return true;
        }

        return false;
    }

    public static void reset() {
        wasFull = false;
        shouldTriggerHome = false;
    }
}
