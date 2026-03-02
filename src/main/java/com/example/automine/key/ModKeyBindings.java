package com.example.automine.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ModKeyBindings {
    public static KeyBinding openConfig;

    public static void register() {
        openConfig = new KeyBinding("key.automine.openconfig", Keyboard.KEY_G, "key.categories.automine");
        ClientRegistry.registerKeyBinding(openConfig);
    }
}
