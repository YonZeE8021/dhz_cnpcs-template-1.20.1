/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.server.world.ServerWorld
 */
package noppes.npcs.api;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.world.ServerWorld;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.ICloneHandler;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IOverlay;

public abstract class NpcAPI {
    private static NpcAPI instance = null;

    public abstract ICustomNpc createNPC(World var1);

    public abstract ICustomNpc spawnNPC(World var1, int var2, int var3, int var4);

    public abstract IEntity getIEntity(Entity var1);

    public abstract IBlock getIBlock(World var1, BlockPos var2);

    public abstract IContainer getIContainer(Inventory var1);

    public abstract IContainer getIContainer(ScreenHandler var1);

    public abstract IItemStack getIItemStack(ItemStack var1);

    public abstract IWorld getIWorld(ServerWorld var1);

    public abstract IWorld getIWorld(String var1);

    public abstract IWorld getIWorld(DimensionType var1);

    public abstract IWorld[] getIWorlds();

    public abstract INbt getINbt(NbtCompound var1);

    public abstract IPos getIPos(double var1, double var3, double var5);

    public abstract IFactionHandler getFactions();

    public abstract IRecipeHandler getRecipes();

    public abstract IQuestHandler getQuests();

    public abstract IDialogHandler getDialogs();

    public abstract ICloneHandler getClones();

    public abstract IDamageSource getIDamageSource(DamageSource var1);

    public abstract INbt stringToNbt(String var1);

    public abstract IPlayerMail createMail(String var1, String var2);

    public abstract ICustomGui createCustomGui(int var1, int var2, int var3, boolean var4, IPlayer var5);

    public abstract IOverlay createOverlay(int var1);

    public abstract INbt getRawPlayerData(String var1);

    public abstract File getGlobalDir();

    public abstract File getLevelDir();

    public static boolean IsAvailable() {
        var fl = FabricLoader.getInstance();
        return fl.isModLoaded("dhz_cnpcs") || fl.isModLoaded("customnpcs");
    }

    public static NpcAPI Instance() {
        if (instance != null) {
            return instance;
        }
        if (!NpcAPI.IsAvailable()) {
            return null;
        }
        try {
            Class<?> c = Class.forName("noppes.npcs.api.wrapper.WrapperNpcAPI");
            instance = (NpcAPI)c.getMethod("Instance", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public abstract boolean hasPermissionNode(String var1);

    public abstract String executeCommand(IWorld var1, String var2);

    public abstract String getRandomName(int var1, int var2);
}

