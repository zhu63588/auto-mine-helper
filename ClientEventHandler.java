package com.example.automine.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.example.automine.config.ModConfig;
import com.example.automine.key.ModKeyBindings;
import com.example.automine.manager.*;
import com.example.automine.util.ModLogger;
import com.example.automine.gui.ModConfigScreen;

public class ClientEventHandler {
    private static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (ModConfig.enabled) {
            InventoryManager.shouldTriggerAutoHome();
            RespawnManager.onClientTick();
            MoveCheckManager.onClientTick();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ModKeyBindings.openConfig.isPressed()) {
            openConfigScreen();
        }
    }

    private void openConfigScreen() {
        GuiScreen currentScreen = mc.currentScreen;
        if (currentScreen == null) {
            mc.displayGuiScreen(new ModConfigScreen());
            ModLogger.info("Opened config screen.");
        }
    }

    public static void onJoinWorld() {
        ModLogger.info("Player joined world.");
        InventoryManager.reset();
        RespawnManager.reset();
        MoveCheckManager.reset();
        CommandExecutor.reset();
    }
}
