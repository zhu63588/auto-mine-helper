package com.example.automine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import com.example.automine.config.ModConfig;

public class ChestTypeScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiTextField chestTypeField;
    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiButton backButton;
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 10;

    public ChestTypeScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        chestTypeField = new GuiTextField(0, fontRenderer, width / 2 - 100, 30, 200, 20);
        chestTypeField.setMaxStringLength(100);
        chestTypeField.setFocused(true);

        addButton = new GuiButton(1, width / 2 - 100, 60, 95, 20, "Add");
        removeButton = new GuiButton(2, width / 2 + 5, 60, 95, 20, "Remove");
        backButton = new GuiButton(3, width / 2 - 100, height - 40, 200, 20, "Back");

        buttonList.add(addButton);
        buttonList.add(removeButton);
        buttonList.add(backButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Chest Types", width / 2, 10, 0xFFFFFF);

        chestTypeField.drawTextBox();

        int y = 100;
        int end = Math.min(scrollOffset + ITEMS_PER_PAGE, ModConfig.chestTypes.size());

        for (int i = scrollOffset; i < end; i++) {
            String chestType = ModConfig.chestTypes.get(i);
            drawString(fontRenderer, (i + 1) + ". " + chestType, width / 2 - 150, y, 0xFFFFFF);
            y += 20;
        }

        if (ModConfig.chestTypes.size() > ITEMS_PER_PAGE) {
            drawCenteredString(fontRenderer, "Scroll: " + (scrollOffset / ITEMS_PER_PAGE + 1) + "/" + 
                ((ModConfig.chestTypes.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE), width / 2, height - 70, 0xAAAAAA);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == addButton) {
            String chestType = chestTypeField.getText().trim();
            if (!chestType.isEmpty()) {
                if (!ModConfig.chestTypes.contains(chestType)) {
                    ModConfig.chestTypes.add(chestType);
                    ModConfig.save();
                }
                chestTypeField.setText("");
            }
        } else if (button == removeButton) {
            String chestType = chestTypeField.getText().trim();
            if (!chestType.isEmpty()) {
                ModConfig.chestTypes.remove(chestType);
                ModConfig.save();
                chestTypeField.setText("");
            }
        } else if (button == backButton) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
            return;
        }

        chestTypeField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        chestTypeField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            if (mouseX < width / 2 - 150) {
                if (scrollOffset > 0) {
                    scrollOffset--;
                }
            } else if (mouseX > width / 2 + 150) {
                if (scrollOffset + ITEMS_PER_PAGE < ModConfig.chestTypes.size()) {
                    scrollOffset++;
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        chestTypeField.updateCursorCounter();
    }
}
