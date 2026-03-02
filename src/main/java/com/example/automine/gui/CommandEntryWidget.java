package com.example.automine.gui;

import com.example.automine.config.ModConfig;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class CommandEntryWidget extends DrawableHelper {
    private int x, y, width, height;
    private ModConfig.CommandEntry command;
    private int index;
    private Consumer<Integer> removeCallback;
    private Consumer<Integer> moveUpCallback;
    private Consumer<Integer> moveDownCallback;
    
    private ButtonWidget removeButton;
    private ButtonWidget upButton;
    private ButtonWidget downButton;
    
    public CommandEntryWidget(int x, int y, int width, int height, 
            ModConfig.CommandEntry command, int index,
            Consumer<Integer> removeCallback,
            Consumer<Integer> moveUpCallback,
            Consumer<Integer> moveDownCallback) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.command = command;
        this.index = index;
        this.removeCallback = removeCallback;
        this.moveUpCallback = moveUpCallback;
        this.moveDownCallback = moveDownCallback;
        
        createButtons();
    }
    
    private void createButtons() {
        int buttonY = y + (height - 20) / 2;
        int buttonWidth = 20;
        
        // 上移按钮
        upButton = ButtonWidget.builder(
            Text.literal("↑"),
            button -> moveUpCallback.accept(index))
            .dimensions(x + width - 70, buttonY, buttonWidth, 20)
            .build();
        
        // 下移按钮
        downButton = ButtonWidget.builder(
            Text.literal("↓"),
            button -> moveDownCallback.accept(index))
            .dimensions(x + width - 45, buttonY, buttonWidth, 20)
            .build();
        
        // 删除按钮
        removeButton = ButtonWidget.builder(
            Text.literal("×"),
            button -> removeCallback.accept(index))
            .dimensions(x + width - 20, buttonY, buttonWidth, 20)
            .build();
    }
    
    public void setButtonsToScreen(net.minecraft.client.gui.screen.Screen screen) {
        screen.addDrawableChild(upButton);
        screen.addDrawableChild(downButton);
        screen.addDrawableChild(removeButton);
    }
    
    public void render(net.minecraft.client.MinecraftClient client, MatrixStack matrices, 
            int mouseX, int mouseY, float delta) {
        // 绘制背景
        fill(matrices, x, y, x + width, y + height, 0xAA000000);
        
        // 绘制指令文本
        String displayText = command.command;
        if (command.delay > 0) {
            displayText += " (延迟 " + command.delay + " 秒)";
        }
        
        int textX = x + 10;
        int textY = y + (height - client.textRenderer.fontHeight) / 2;
        client.textRenderer.draw(matrices, Text.literal(displayText), textX, textY, 0xFFFFFF);
        
        // 绘制序号
        String indexText = String.valueOf(index + 1) + ".";
        client.textRenderer.draw(matrices, Text.literal(indexText), x - 25, textY, 0xAAAAAA);
    }
    
    public ButtonWidget getRemoveButton() {
        return removeButton;
    }
    
    public ButtonWidget getUpButton() {
        return upButton;
    }
    
    public ButtonWidget getDownButton() {
        return downButton;
    }
}
