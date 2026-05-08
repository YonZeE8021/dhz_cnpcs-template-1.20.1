/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 */
package noppes.npcs.api.wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import nikedemos.markovnames.generators.MarkovGenerator;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
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
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.api.wrapper.BlockWrapper;
import noppes.npcs.api.wrapper.ContainerWrapper;
import noppes.npcs.api.wrapper.DamageSourceWrapper;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.api.wrapper.OverlayWrapper;
import noppes.npcs.api.wrapper.WorldWrapper;
import noppes.npcs.api.wrapper.WrapperEntityData;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.EntityIMixin;
import noppes.npcs.shared.common.util.LRUHashMap;
import noppes.npcs.util.NBTJsonUtil;

public class WrapperNpcAPI
extends NpcAPI {
    private static final Map<DimensionType, WorldWrapper> worldCache = new LRUHashMap<DimensionType, WorldWrapper>(10);
    private static NpcAPI instance = null;

    public static void clearCache() {
        worldCache.clear();
        BlockWrapper.clearCache();
    }

    @Override
    public IEntity getIEntity(Entity entity) {
        if (entity == null || entity.getWorld().isClient) {
            return null;
        }
        if (entity instanceof EntityNPCInterface) {
            return ((EntityNPCInterface)entity).wrappedNPC;
        }
        return WrapperEntityData.get(entity);
    }

    @Override
    public ICustomNpc createNPC(World level) {
        if (level.isClient) {
            return null;
        }
        EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
        return npc.wrappedNPC;
    }

    public void registerPermissionNode(String permission, int defaultType) {
        throw new CustomNPCsException("registerPermissionNode is nolonger supported", new Object[0]);
    }

    @Override
    public boolean hasPermissionNode(String permission) {
        return false;
    }

    @Override
    public ICustomNpc spawnNPC(World level, int x, int y, int z) {
        if (level.isClient) {
            return null;
        }
        EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
        npc.updatePositionAndAngles((double)x + 0.5, y, (double)z + 0.5, 0.0f, 0.0f);
        npc.ais.setStartPos(x, y, z);
        npc.setHealth(npc.getMaxHealth());
        level.spawnEntity((Entity)npc);
        return npc.wrappedNPC;
    }

    public static NpcAPI Instance() {
        if (instance == null) {
            instance = new WrapperNpcAPI();
        }
        return instance;
    }

    @Override
    public IBlock getIBlock(World level, BlockPos pos) {
        return BlockWrapper.createNew(level, pos, level.getBlockState(pos));
    }

    @Override
    public IItemStack getIItemStack(ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty()) {
            return ItemStackWrapper.AIR;
        }
        if (itemstack.getItem() == CustomItems.scripted_item) {
            return new ItemScriptedWrapper(itemstack);
        }
        return new ItemStackWrapper(itemstack);
    }

    @Override
    public IWorld getIWorld(ServerWorld level) {
        WorldWrapper w = worldCache.get(level.getDimension());
        if (w != null) {
            w.level = level;
            return w;
        }
        w = WorldWrapper.createNew(level);
        worldCache.put(level.getDimension(), w);
        return w;
    }

    @Override
    public IWorld getIWorld(DimensionType dimension) {
        for (ServerWorld level : CustomNpcs.Server.getWorlds()) {
            if (level.getDimension() != dimension) continue;
            return this.getIWorld(level);
        }
        throw new CustomNPCsException("Unknown dimension: " + String.valueOf(dimension), new Object[0]);
    }

    @Override
    public IWorld getIWorld(String dimension) {
        Identifier loc = new Identifier(dimension);
        for (ServerWorld level : CustomNpcs.Server.getWorlds()) {
            if (!level.getRegistryKey().getValue().equals((Object)loc)) continue;
            return this.getIWorld(level);
        }
        throw new CustomNPCsException("Unknown dimension: " + String.valueOf(loc), new Object[0]);
    }

    @Override
    public IContainer getIContainer(ScreenHandler inventory) {
        return new ContainerWrapper(inventory);
    }

    @Override
    public IContainer getIContainer(Inventory container) {
        if (container instanceof ContainerNpcInterface) {
            return ContainerNpcInterface.getOrCreateIContainer((ContainerNpcInterface)container);
        }
        return new ContainerWrapper(container);
    }

    @Override
    public IFactionHandler getFactions() {
        this.checkLevel();
        return FactionController.instance;
    }

    private void checkLevel() {
        if (CustomNpcs.Server == null || CustomNpcs.Server.isStopped()) {
            throw new CustomNPCsException("No world is loaded right now", new Object[0]);
        }
    }

    @Override
    public IRecipeHandler getRecipes() {
        this.checkLevel();
        return RecipeController.instance;
    }

    @Override
    public IQuestHandler getQuests() {
        this.checkLevel();
        return QuestController.instance;
    }

    @Override
    public IWorld[] getIWorlds() {
        this.checkLevel();
        ArrayList<IWorld> list = new ArrayList<IWorld>();
        for (ServerWorld level : CustomNpcs.Server.getWorlds()) {
            list.add(this.getIWorld(level));
        }
        return list.toArray(new IWorld[list.size()]);
    }

    @Override
    public IPos getIPos(double x, double y, double z) {
        return new BlockPosWrapper(new BlockPos((int)x, (int)y, (int)z));
    }

    @Override
    public File getGlobalDir() {
        return CustomNpcs.Dir;
    }

    @Override
    public File getLevelDir() {
        return CustomNpcs.getLevelSaveDirectory();
    }

    @Override
    public INbt getINbt(NbtCompound compound) {
        if (compound == null) {
            return new NBTWrapper(new NbtCompound());
        }
        return new NBTWrapper(compound);
    }

    @Override
    public INbt stringToNbt(String str) {
        if (str == null || str.isEmpty()) {
            throw new CustomNPCsException("Cant cast empty string to nbt", new Object[0]);
        }
        try {
            return this.getINbt(NBTJsonUtil.Convert(str));
        }
        catch (NBTJsonUtil.JsonException e) {
            throw new CustomNPCsException(e, "Failed converting " + str, new Object[0]);
        }
    }

    @Override
    public IDamageSource getIDamageSource(DamageSource damagesource) {
        return new DamageSourceWrapper(damagesource);
    }

    @Override
    public IDialogHandler getDialogs() {
        return DialogController.instance;
    }

    @Override
    public ICloneHandler getClones() {
        return ServerCloneController.Instance;
    }

    @Override
    public String executeCommand(IWorld level, String command) {
        FakePlayer player = EntityNPCInterface.CommandPlayer;
        ((EntityIMixin)player).setLevel((World)level.getMCLevel());
        player.setPosition(0.0, 0.0, 0.0);
        return NoppesUtilServer.runCommand((World)level.getMCLevel(), BlockPos.ORIGIN, "API", command, null, (Entity)player);
    }

    @Override
    public INbt getRawPlayerData(String uuid) {
        return this.getINbt(PlayerData.loadPlayerData(uuid));
    }

    @Override
    public IPlayerMail createMail(String sender, String subject) {
        PlayerMail mail = new PlayerMail();
        mail.sender = sender;
        mail.subject = subject;
        return mail;
    }

    @Override
    public ICustomGui createCustomGui(int id, int width, int height, boolean pauseGame, IPlayer player) {
        return new CustomGuiWrapper(player, id, width, height, pauseGame);
    }

    @Override
    public IOverlay createOverlay(int id) {
        return new OverlayWrapper(id);
    }

    @Override
    public String getRandomName(int dictionary, int gender) {
        return MarkovGenerator.fetch(dictionary, gender);
    }
}

