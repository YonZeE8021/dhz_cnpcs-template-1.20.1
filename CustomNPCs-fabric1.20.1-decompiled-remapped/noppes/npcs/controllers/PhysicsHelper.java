/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.world.World
 */
package noppes.npcs.controllers;

import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public class PhysicsHelper {
    public static boolean Enabled = FabricLoader.getInstance().isModLoaded("physicsmod");

    public static void resetEntityPhysics(World level, int id) {
        try {
            Class<?> physModClass = Class.forName("net.diebuddies.physics.PhysicsMod");
            Object modInstance = physModClass.getMethod("getInstance", World.class).invoke(null, level);
            Set blockified = (Set)physModClass.getField("alreadyBlockified").get(modInstance);
            blockified.remove(id);
        }
        catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}

