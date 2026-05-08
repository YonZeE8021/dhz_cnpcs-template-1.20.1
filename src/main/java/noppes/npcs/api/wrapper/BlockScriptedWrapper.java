/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.Registries;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlockScripted;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.BlockWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.EntityIMixin;

public class BlockScriptedWrapper
extends BlockWrapper
implements IBlockScripted {
    private TileScripted tile;

    public BlockScriptedWrapper(World level, Block block, BlockPos pos) {
        super(level, block, pos);
        this.tile = (TileScripted)((BlockWrapper)this).tile;
    }

    @Override
    public void setModel(IItemStack item) {
        if (item == null) {
            this.tile.setItemModel(null, null);
        } else {
            Item itemmc = item.getMCItemStack().getItem();
            this.tile.setItemModel(item.getMCItemStack(), itemmc instanceof BlockItem ? ((BlockItem)itemmc).getBlock() : Blocks.AIR);
        }
    }

    @Override
    public void setModel(String name) {
        if (name == null) {
            this.tile.setItemModel(null, null);
        } else {
            Identifier loc = new Identifier(name);
            Block block = (Block)Registries.BLOCK.get(loc);
            this.tile.setItemModel(new ItemStack((ItemConvertible)Registries.ITEM.get(loc)), block);
        }
    }

    @Override
    public IItemStack getModel() {
        return NpcAPI.Instance().getIItemStack(this.tile.itemModel);
    }

    @Override
    public void setRedstonePower(int strength) {
        this.tile.setRedstonePower(strength);
    }

    @Override
    public int getRedstonePower() {
        return this.tile.powering;
    }

    @Override
    public void setIsLadder(boolean bo) {
        this.tile.isLadder = bo;
        this.tile.needsClientUpdate = true;
    }

    @Override
    public boolean getIsLadder() {
        return this.tile.isLadder;
    }

    @Override
    public void setIsPassible(boolean bo) {
        this.tile.isPassible = bo;
        this.tile.needsClientUpdate = true;
    }

    @Override
    public boolean getIsPassible() {
        return this.tile.isPassible;
    }

    @Override
    public void setLight(int value) {
        this.tile.setLightValue(value);
    }

    @Override
    public int getLight() {
        return this.tile.lightValue;
    }

    @Override
    public void setScale(float x, float y, float z) {
        this.tile.setScale(x, y, z);
    }

    @Override
    public float getScaleX() {
        return this.tile.scaleX;
    }

    @Override
    public float getScaleY() {
        return this.tile.scaleY;
    }

    @Override
    public float getScaleZ() {
        return this.tile.scaleZ;
    }

    @Override
    public void setRotation(int x, int y, int z) {
        this.tile.setRotation(x % 360, y % 360, z % 360);
    }

    @Override
    public int getRotationX() {
        return this.tile.rotationX;
    }

    @Override
    public int getRotationY() {
        return this.tile.rotationY;
    }

    @Override
    public int getRotationZ() {
        return this.tile.rotationZ;
    }

    @Override
    public float getHardness() {
        return this.tile.blockHardness;
    }

    @Override
    public void setHardness(float hardness) {
        this.tile.blockHardness = hardness;
    }

    @Override
    public float getResistance() {
        return this.tile.blockResistance;
    }

    @Override
    public void setResistance(float resistance) {
        this.tile.blockResistance = resistance;
    }

    @Override
    public String executeCommand(String command) {
        if (!this.tile.getWorld().getServer().areCommandBlocksEnabled()) {
            throw new CustomNPCsException("Command blocks need to be enabled to executeCommands", new Object[0]);
        }
        FakePlayer player = EntityNPCInterface.CommandPlayer;
        ((EntityIMixin)player).setLevel((World)((ServerWorld)this.tile.getWorld()));
        player.setPosition((double)this.getX(), (double)this.getY(), (double)this.getZ());
        return NoppesUtilServer.runCommand(this.tile.getWorld(), this.tile.getPos(), "ScriptBlock: " + String.valueOf(this.tile.getPos()), command, null, (Entity)player);
    }

    @Override
    public ITextPlane getTextPlane() {
        return this.tile.text1;
    }

    @Override
    public ITextPlane getTextPlane2() {
        return this.tile.text2;
    }

    @Override
    public ITextPlane getTextPlane3() {
        return this.tile.text3;
    }

    @Override
    public ITextPlane getTextPlane4() {
        return this.tile.text4;
    }

    @Override
    public ITextPlane getTextPlane5() {
        return this.tile.text5;
    }

    @Override
    public ITextPlane getTextPlane6() {
        return this.tile.text6;
    }

    @Override
    public ITimers getTimers() {
        return this.tile.timers;
    }

    @Override
    protected void setTile(BlockEntity tile) {
        this.tile = (TileScripted)tile;
        super.setTile(tile);
    }

    @Override
    public void trigger(int id, Object ... arguments) {
        EventHooks.onScriptTriggerEvent(this.tile, id, this.level, this.getPos(), null, arguments);
    }
}

