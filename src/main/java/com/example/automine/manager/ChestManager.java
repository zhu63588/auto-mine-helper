package com.example.automine.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.example.automine.config.ModConfig;
import com.example.automine.util.ModLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestManager {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static Map<String, BlockPos> chestLocations = new HashMap<>();

    public static void setChestLocation(String name, BlockPos pos) {
        chestLocations.put(name, pos);
        ModLogger.info("Set chest '" + name + "' at " + pos.toString());
    }

    public static BlockPos getChestLocation(String name) {
        return chestLocations.get(name);
    }

    public static List<String> listChests() {
        return new ArrayList<>(chestLocations.keySet());
    }

    public static List<BlockPos> findNearbyChests(double radius) {
        List<BlockPos> chests = new ArrayList<>();

        if (mc.player == null || mc.world == null) {
            return chests;
        }

        EntityPlayer player = mc.player;
        World world = mc.world;

        BlockPos playerPos = player.getPosition();
        int searchRadius = (int) Math.ceil(radius);

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    double distance = player.getDistance(pos.getX(), pos.getY(), pos.getZ());

                    if (distance <= radius) {
                        if (isChestBlock(world, pos)) {
                            chests.add(pos);
                        }
                    }
                }
            }
        }

        ModLogger.info("Found " + chests.size() + " chests within " + radius + " blocks.");
        return chests;
    }

    public static boolean isChestBlock(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityChest) {
            return true;
        }

        return false;
    }

    public static boolean canPlaceInChest(BlockPos pos) {
        if (mc.world == null) {
            return false;
        }

        TileEntity te = mc.world.getTileEntity(pos);

        if (te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) te;
            for (int i = 0; i < chest.getSizeInventory(); i++) {
                if (chest.getStackInSlot(i).isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    public static void clearChestLocations() {
        int count = chestLocations.size();
        chestLocations.clear();
        ModLogger.info("Cleared " + count + " chest locations.");
    }

    public static void removeChestLocation(String name) {
        if (chestLocations.containsKey(name)) {
            chestLocations.remove(name);
            ModLogger.info("Removed chest '" + name + "'.");
        }
    }
}
