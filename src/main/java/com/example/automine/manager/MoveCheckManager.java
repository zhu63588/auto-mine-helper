package com.example.automine.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import com.example.automine.config.ModConfig;
import com.example.automine.util.ModLogger;

public class MoveCheckManager {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static double lastX = 0;
    private static double lastZ = 0;
    private static boolean hasMoved = false;
    private static long lastMoveTime = 0;
    private static final long MOVE_TIMEOUT = 300000; // 5 minutes

    public static void onClientTick() {
        if (!ModConfig.enabled || !ModConfig.moveCheck) {
            return;
        }

        if (mc.player == null) {
            return;
        }

        EntityPlayer player = mc.player;
        double currentX = player.posX;
        double currentZ = player.posZ;

        double distance = Math.sqrt(Math.pow(currentX - lastX, 2) + Math.pow(currentZ - lastZ, 2));

        if (distance > 0.1) {
            hasMoved = true;
            lastMoveTime = System.currentTimeMillis();
            lastX = currentX;
            lastZ = currentZ;
            ModLogger.debug("Player moved. Distance: " + String.format("%.2f", distance));
        }

        if (hasMoved) {
            long timeSinceLastMove = System.currentTimeMillis() - lastMoveTime;
            if (timeSinceLastMove > MOVE_TIMEOUT) {
                ModLogger.warn("Player has not moved for 5 minutes!");
                hasMoved = false;
            }
        }
    }

    public static boolean hasPlayerMoved() {
        return hasMoved;
    }

    public static void reset() {
        lastX = 0;
        lastZ = 0;
        hasMoved = false;
        lastMoveTime = 0;
    }
}
