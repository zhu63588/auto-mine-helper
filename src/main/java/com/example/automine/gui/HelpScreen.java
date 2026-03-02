package com.example.automine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class HelpScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiButton backButton;
    private int scrollOffset = 0;

    public HelpScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        backButton = new GuiButton(0, width / 2 - 100, height - 40, 200, 20, "Back");
        buttonList.add(backButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Auto Mine Helper - Help", width / 2, 10, 0xFFFFFF);

        String[] helpLines = {
            "=== Features ===",
            "",
            "1. Auto Home:",
            "   - Automatically returns home when inventory is full",
            "   - Two modes: Slot Occupied / Stack Full",
            "",
            "2. Auto Respawn:",
            "   - Automatically respawns after death",
            "",
            "3. Command Sequence:",
            "   - Execute a sequence of commands with delays",
            "   - Use /am run or click 'Run Commands' button",
            "",
            "4. Move Check:",
            "   - Alerts if player hasn't moved for 5 minutes",
            "",
            "5. Whitelist:",
            "   - Filter items to ignore when checking inventory",
            "",
            "=== Commands ===",
            "",
            "/am help - Show this help",
            "/am toggle - Toggle mod on/off",
            "/am home - Return home",
            "/am respawn <on|off> - Toggle auto respawn",
            "/am movecheck <on|off> - Toggle move check",
            "/am run - Execute command sequence",
            "/am addcmd <command> - Add command",
            "/am clearcmd - Clear command sequence",
            "/am listcmd - List commands",
            "/am whitelist <add|remove|list|toggle> [item]",
            "/am chest <list|set|find> [name|radius]",
            "",
            "=== GUI ===",
            "",
            "Press G key to open configuration screen",
            "Use the GUI to easily configure all settings",
        };

        int y = 30 - scrollOffset;
        for (String line : helpLines) {
            if (y > 20 && y < height - 60) {
                drawString(fontRenderer, line, width / 2 - 180, y, 0xFFFFFF);
            }
            y += 15;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == backButton) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (mouseY > 100 && mouseY < height - 60) {
            if (clickedMouseButton == 0) {
                scrollOffset -= 2;
            } else if (clickedMouseButton == 1) {
                scrollOffset += 2;
            }
            if (scrollOffset < 0) scrollOffset = 0;
        }
    }
}
