package com.example.automine.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.automine.util.ModLogger;

public class ModConfig {
    private static final String CONFIG_FILE = "automine_config.json";
    private static File configDir;
    private static File configFile;

    public static boolean enabled = false;
    public static boolean autoRespawn = false;
    public static boolean moveCheck = false;
    public static boolean whitelistEnabled = false;
    public static int inventoryCheckMode = 0; // 0: slot occupied, 1: stack full
    public static List<String> whitelistItems = new ArrayList<>();
    public static List<String> chestTypes = new ArrayList<>();
    public static List<CommandEntry> commandSequence = new ArrayList<>();

    public static class CommandEntry {
        public String command;
        public int delay;

        public CommandEntry(String command, int delay) {
            this.command = command;
            this.delay = delay;
        }
    }

    public static void init(File dir) {
        configDir = dir;
        configFile = new File(configDir, CONFIG_FILE);
    }

    public static void load() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Gson gson = new Gson();
                Map<String, Object> data = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());

                if (data != null) {
                    enabled = getBoolean(data, "enabled", false);
                    autoRespawn = getBoolean(data, "autoRespawn", false);
                    moveCheck = getBoolean(data, "moveCheck", false);
                    whitelistEnabled = getBoolean(data, "whitelistEnabled", false);
                    inventoryCheckMode = getInt(data, "inventoryCheckMode", 0);
                    whitelistItems = getList(data, "whitelistItems", new ArrayList<>());
                    chestTypes = getList(data, "chestTypes", getDefaultChestTypes());

                    List<Map<String, Object>> cmdSeq = getList(data, "commandSequence", new ArrayList<>());
                    commandSequence.clear();
                    for (Map<String, Object> entry : cmdSeq) {
                        String cmd = (String) entry.get("command");
                        int delay = getInt(entry, "delay", 0);
                        commandSequence.add(new CommandEntry(cmd, delay));
                    }
                }
                ModLogger.info("Configuration loaded.");
            } catch (IOException e) {
                ModLogger.error("Failed to load config: " + e.getMessage());
            }
        } else {
            chestTypes = getDefaultChestTypes();
            save();
        }
    }

    public static void save() {
        try {
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("enabled", enabled);
            data.put("autoRespawn", autoRespawn);
            data.put("moveCheck", moveCheck);
            data.put("whitelistEnabled", whitelistEnabled);
            data.put("inventoryCheckMode", inventoryCheckMode);
            data.put("whitelistItems", whitelistItems);
            data.put("chestTypes", chestTypes);

            List<Map<String, Object>> cmdSeq = new ArrayList<>();
            for (CommandEntry entry : commandSequence) {
                Map<String, Object> cmdEntry = new HashMap<>();
                cmdEntry.put("command", entry.command);
                cmdEntry.put("delay", entry.delay);
                cmdSeq.add(cmdEntry);
            }
            data.put("commandSequence", cmdSeq);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(data, writer);
            }
            ModLogger.info("Configuration saved.");
        } catch (IOException e) {
            ModLogger.error("Failed to save config: " + e.getMessage());
        }
    }

    private static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    private static int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getList(Map<String, Object> map, String key, List<T> defaultValue) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<T>) value;
        }
        return defaultValue;
    }

    private static List<String> getDefaultChestTypes() {
        List<String> defaults = new ArrayList<>();
        defaults.add("minecraft:chest");
        return defaults;
    }
}
