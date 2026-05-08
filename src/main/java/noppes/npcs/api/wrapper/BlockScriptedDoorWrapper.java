/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.Registries;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.block.IBlockScriptedDoor;
import noppes.npcs.api.wrapper.BlockWrapper;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.EntityIMixin;

public class BlockScriptedDoorWrapper
extends BlockWrapper
implements IBlockScriptedDoor {
    private TileScriptedDoor tile;

    public BlockScriptedDoorWrapper(World level, Block block, BlockPos pos) {
        super(level, block, pos);
        this.tile = (TileScriptedDoor)((BlockWrapper)this).tile;
    }

    @Override
    public boolean getOpen() {
        BlockState state = this.level.getMCLevel().getBlockState(this.pos);
        return ((Boolean)state.get((Property)DoorBlock.OPEN)).equals(true);
    }

    @Override
    public void setOpen(boolean open) {
        if (this.getOpen() == open || this.isRemoved()) {
            return;
        }
        BlockState state = this.level.getMCLevel().getBlockState(this.pos);
        ((DoorBlock)this.block).setOpen(null, (World)this.level.getMCLevel(), state, this.pos, open);
    }

    @Override
    public void setBlockModel(String name) {
        Block b = null;
        if (name != null) {
            b = (Block)Registries.BLOCK.get(new Identifier(name));
        }
        this.tile.setItemModel(b);
    }

    @Override
    public String getBlockModel() {
        return String.valueOf(Registries.BLOCK.getId(this.tile.blockModel));
    }

    @Override
    public ITimers getTimers() {
        return this.tile.timers;
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
    protected void setTile(BlockEntity tile) {
        this.tile = (TileScriptedDoor)tile;
        super.setTile(tile);
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
}

