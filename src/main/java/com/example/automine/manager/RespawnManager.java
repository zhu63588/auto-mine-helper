package com.example.automine.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import com.example.automine.config.ModConfig;
import com.example.automine.util.ModLogger;

public class RespawnManager {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static boolean wasDead = false;
    private static int respawnTimer = 0;

    public static void onClientTick() {
        if (!ModConfig.enabled || !ModConfig.autoRespawn) {
            return;
        }

        if (mc.player == null) {
            return;
        }

        EntityPlayerSP player = mc.player;

        if (player.isDead) {
            wasDead = true;
            respawnTimer = 0;
            ModLogger.debug("Player died. Will respawn when possible.");
        } else if (wasDead && !player.isDead) {
            wasDead = false;
            respawnTimer = 0;
            ModLogger.debug("Player has respawned.");
        }

        if (wasDead && mc.currentScreen == null) {
            respawnTimer++;
            if (respawnTimer >= 20) {
                mc.player.respawnPlayer();
                ModLogger.info("Auto-respawn triggered.");
            }
        }
    }

    public static void reset() {
        wasDead = false;
        respawnTimer = 0;
    }
}
