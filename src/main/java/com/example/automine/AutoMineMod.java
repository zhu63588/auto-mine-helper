package com.example.automine;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import com.example.automine.config.ModConfig;
import com.example.automine.key.ModKeyBindings;
import com.example.automine.command.AutoMineCommand;

@Mod(modid = AutoMineMod.MODID, name = AutoMineMod.NAME, version = AutoMineMod.VERSION)
public class AutoMineMod {
    public static final String MODID = "automine";
    public static final String NAME = "Auto Mine Helper";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModLogger.init(event.getModConfigurationDirectory());
        ModConfig.load();
        ModKeyBindings.register();
        AutoMineCommand.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModLogger.info("Auto Mine Helper initialized!");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
