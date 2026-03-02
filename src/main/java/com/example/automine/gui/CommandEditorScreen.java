package com.example.automine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import com.example.automine.config.ModConfig;

public class CommandEditorScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiTextField commandField;
    private GuiTextField delayField;
    private GuiButton addButton;
    private GuiButton clearButton;
    private GuiButton backButton;
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 8;
    private int selectedCommandIndex = -1;
    private int buttonIdCounter = 10;

    public CommandEditorScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        commandField = new GuiTextField(0, fontRenderer, width / 2 - 100, 30, 200, 20);
        commandField.setMaxStringLength(256);
        commandField.setFocused(true);

        delayField = new GuiTextField(1, fontRenderer, width / 2 - 100, 60, 100, 20);
        delayField.setMaxStringLength(10);
        delayField.setText("0");

        addButton = new GuiButton(buttonIdCounter++, width / 2 + 10, 60, 95, 20, "Add");
        clearButton = new GuiButton(buttonIdCounter++, width / 2 - 100, height - 40, 95, 20, "Clear All");
        backButton = new GuiButton(buttonIdCounter++, width / 2 + 5, height - 40, 95, 20, "Back");

        buttonList.add(addButton);
        buttonList.add(clearButton);
        buttonList.add(backButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Command Editor", width / 2, 10, 0xFFFFFF);
        drawString(fontRenderer, "Command:", width / 2 - 150, 35, 0xFFFFFF);
        drawString(fontRenderer, "Delay (ms):", width / 2 - 160, 65, 0xFFFFFF);

        commandField.drawTextBox();
        delayField.drawTextBox();

        int y = 100;
        int end = Math.min(scrollOffset + ITEMS_PER_PAGE, ModConfig.commandSequence.size());

        for (int i = scrollOffset; i < end; i++) {
            ModConfig.CommandEntry entry = ModConfig.commandSequence.get(i);
            String text = (i + 1) + ". " + entry.command + " (" + entry.delay + "ms)";
            drawString(fontRenderer, text, width / 2 - 150, y, i == selectedCommandIndex ? 0xFFFF55 : 0xFFFFFF);
            y += 25;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == addButton) {
            String command = commandField.getText().trim();
            int delay;
            try {
                delay = Integer.parseInt(delayField.getText().trim());
            } catch (NumberFormatException e) {
                delay = 0;
            }

            if (!command.isEmpty()) {
                ModConfig.commandSequence.add(new ModConfig.CommandEntry(command, delay));
                ModConfig.save();
                commandField.setText("");
                delayField.setText("0");
            }
        } else if (button == clearButton) {
            ModConfig.commandSequence.clear();
            ModConfig.save();
            selectedCommandIndex = -1;
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

        if (keyCode == Keyboard.KEY_DELETE && selectedCommandIndex >= 0) {
            ModConfig.commandSequence.remove(selectedCommandIndex);
            ModConfig.save();
            selectedCommandIndex = -1;
            return;
        }

        commandField.textboxKeyTyped(typedChar, keyCode);
        delayField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        commandField.mouseClicked(mouseX, mouseY, mouseButton);
        delayField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            int y = 100;
            int end = Math.min(scrollOffset + ITEMS_PER_PAGE, ModConfig.commandSequence.size());

            for (int i = scrollOffset; i < end; i++) {
                if (mouseY >= y && mouseY < y + 20 && 
                    mouseX >= width / 2 - 150 && mouseX < width / 2 + 150) {
                    selectedCommandIndex = i;
                    return;
                }
                y += 25;
            }

            if (mouseX < width / 2 - 150) {
                if (scrollOffset > 0) {
                    scrollOffset--;
                }
            } else if (mouseX > width / 2 + 150) {
                if (scrollOffset + ITEMS_PER_PAGE < ModConfig.commandSequence.size()) {
                    scrollOffset++;
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        commandField.updateCursorCounter();
        delayField.updateCursorCounter();
    }
}
