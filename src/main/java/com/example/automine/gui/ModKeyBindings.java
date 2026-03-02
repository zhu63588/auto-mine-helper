package com.example.automine.gui;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding openConfigKey;
    
    public static void register() {
        // 注册打开配置界面的快捷键 (默认: G)
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.automine.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.automine"
        ));
    }
}
