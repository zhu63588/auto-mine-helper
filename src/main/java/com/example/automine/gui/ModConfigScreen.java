package com.example.automine.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

import com.example.automine.config.ModConfig;
import com.example.automine.manager.CommandExecutor;
import com.example.automine.util.ModLogger;

public class ModConfigScreen extends GuiScreen {
    private GuiButton enabledButton;
    private GuiButton autoRespawnButton;
    private GuiButton moveCheckButton;
    private GuiButton whitelistEnabledButton;
    private GuiButton inventoryModeButton;
    private GuiButton runCommandsButton;
    private GuiButton saveButton;
    private GuiButton closeButton;
    private GuiButton chestTypesButton;
    private GuiButton commandEditorButton;
    private GuiButton helpButton;

    private int buttonIdCounter = 0;

    @Override
    public void initGui() {
        int y = 50;

        enabledButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Mod: " + (ModConfig.enabled ? "ON" : "OFF"));
        buttonList.add(enabledButton);
        y += 30;

        autoRespawnButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Auto Respawn: " + (ModConfig.autoRespawn ? "ON" : "OFF"));
        buttonList.add(autoRespawnButton);
        y += 30;

        moveCheckButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Move Check: " + (ModConfig.moveCheck ? "ON" : "OFF"));
        buttonList.add(moveCheckButton);
        y += 30;

        whitelistEnabledButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Whitelist: " + (ModConfig.whitelistEnabled ? "ON" : "OFF"));
        buttonList.add(whitelistEnabledButton);
        y += 30;

        inventoryModeButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Inventory Mode: " + (ModConfig.inventoryCheckMode == 0 ? "Slot Occupied" : "Stack Full"));
        buttonList.add(inventoryModeButton);
        y += 30;

        chestTypesButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Chest Types (" + ModConfig.chestTypes.size() + ")");
        buttonList.add(chestTypesButton);
        y += 30;

        commandEditorButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Command Editor (" + ModConfig.commandSequence.size() + ")");
        buttonList.add(commandEditorButton);
        y += 30;

        runCommandsButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Run Commands");
        buttonList.add(runCommandsButton);
        y += 30;

        helpButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 200, 20, "Help");
        buttonList.add(helpButton);
        y += 40;

        saveButton = new GuiButton(buttonIdCounter++, width / 2 - 100, y, 95, 20, "Save");
        buttonList.add(saveButton);

        closeButton = new GuiButton(buttonIdCounter++, width / 2 + 5, y, 95, 20, "Close");
        buttonList.add(closeButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Auto Mine Helper Configuration", width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == enabledButton) {
            ModConfig.enabled = !ModConfig.enabled;
            enabledButton.displayString = "Mod: " + (ModConfig.enabled ? "ON" : "OFF");
            ModLogger.info("Mod " + (ModConfig.enabled ? "enabled" : "OFF") + " via GUI");
        } else if (button == autoRespawnButton) {
            ModConfig.autoRespawn = !ModConfig.autoRespawn;
            autoRespawnButton.displayString = "Auto Respawn: " + (ModConfig.autoRespawn ? "ON" : "OFF");
            ModLogger.info("Auto respawn " + (ModConfig.autoRespawn ? "enabled" : "disabled") + " via GUI");
        } else if (button == moveCheckButton) {
            ModConfig.moveCheck = !ModConfig.moveCheck;
            moveCheckButton.displayString = "Move Check: " + (ModConfig.moveCheck ? "ON" : "OFF");
            ModLogger.info("Move check " + (ModConfig.moveCheck ? "enabled" : "disabled") + " via GUI");
        } else if (button == whitelistEnabledButton) {
            ModConfig.whitelistEnabled = !ModConfig.whitelistEnabled;
            whitelistEnabledButton.displayString = "Whitelist: " + (ModConfig.whitelistEnabled ? "ON" : "OFF");
            ModLogger.info("Whitelist " + (ModConfig.whitelistEnabled ? "enabled" : "disabled") + " via GUI");
        } else if (button == inventoryModeButton) {
            ModConfig.inventoryCheckMode = ModConfig.inventoryCheckMode == 0 ? 1 : 0;
            inventoryModeButton.displayString = "Inventory Mode: " + (ModConfig.inventoryCheckMode == 0 ? "Slot Occupied" : "Stack Full");
            ModLogger.info("Inventory mode changed to " + (ModConfig.inventoryCheckMode == 0 ? "Slot Occupied" : "Stack Full") + " via GUI");
        } else if (button == chestTypesButton) {
            mc.displayGuiScreen(new ChestTypeScreen(this));
        } else if (button == commandEditorButton) {
            mc.displayGuiScreen(new CommandEditorScreen(this));
        } else if (button == runCommandsButton) {
            if (ModConfig.enabled) {
                CommandExecutor.executeCommandSequence();
                mc.player.sendMessage(new TextComponentString("Executing command sequence..."));
            } else {
                mc.player.sendMessage(new TextComponentString("Mod is disabled!"));
            }
        } else if (button == helpButton) {
            mc.displayGuiScreen(new HelpScreen(this));
        } else if (button == saveButton) {
            ModConfig.save();
            mc.player.sendMessage(new TextComponentString("Configuration saved!"));
        } else if (button == closeButton) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onGuiClosed() {
        ModConfig.save();
    }
}
