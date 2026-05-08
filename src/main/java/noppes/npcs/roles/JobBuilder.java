/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.roles;

import java.util.Stack;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.role.IJobBuilder;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobBuilder
extends JobInterface
implements IJobBuilder {
    public TileBuilder build = null;
    private BlockPos possibleBuildPos = null;
    private Stack<BlockData> placingList = null;
    private BlockData placing = null;
    private int tryTicks = 0;
    private int ticks = 0;

    public JobBuilder(EntityNPCInterface npc) {
        super(npc);
        this.overrideMainHand = true;
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        if (this.build != null) {
            compound.putInt("BuildX", this.build.getPos().getX());
            compound.putInt("BuildY", this.build.getPos().getY());
            compound.putInt("BuildZ", this.build.getPos().getZ());
            if (this.placingList != null && !this.placingList.isEmpty()) {
                NbtList list = new NbtList();
                for (BlockData data : this.placingList) {
                    list.add(data.getNBT());
                }
                if (this.placing != null) {
                    list.add(this.placing.getNBT());
                }
                compound.put("Placing", (NbtElement)list);
            }
        }
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        if (compound.contains("BuildX")) {
            this.possibleBuildPos = new BlockPos(compound.getInt("BuildX"), compound.getInt("BuildY"), compound.getInt("BuildZ"));
        }
        if (this.possibleBuildPos != null && compound.contains("Placing")) {
            Stack<BlockData> placing = new Stack<BlockData>();
            NbtList list = compound.getList("Placing", 10);
            for (int i = 0; i < list.size(); ++i) {
                BlockData data = BlockData.getData(list.getCompound(i));
                if (data == null) continue;
                placing.add(data);
            }
            this.placingList = placing;
        }
        this.npc.ais.doorInteract = 1;
    }

    @Override
    public IItemStack getMainhand() {
        String name = this.npc.getJobData();
        ItemStack item = this.stringToItem(name);
        if (item.isEmpty()) {
            return this.npc.inventory.weapons.get(0);
        }
        return NpcAPI.Instance().getIItemStack(item);
    }

    @Override
    public boolean aiShouldExecute() {
        if (this.possibleBuildPos != null) {
            BlockEntity tile = this.npc.getWorld().getBlockEntity(this.possibleBuildPos);
            if (tile instanceof TileBuilder) {
                this.build = (TileBuilder)tile;
            } else {
                this.placingList.clear();
            }
            this.possibleBuildPos = null;
        }
        return this.build != null;
    }

    @Override
    public void aiUpdateTask() {
        if (this.build.finished && this.placingList == null || !this.build.enabled || this.build.isRemoved()) {
            this.build = null;
            this.npc.getNavigation().startMovingTo((double)this.npc.getStartXPos(), this.npc.getStartYPos(), (double)this.npc.getStartZPos(), 1.0);
            return;
        }
        if (this.ticks++ < 10) {
            return;
        }
        this.ticks = 0;
        if ((this.placingList == null || this.placingList.isEmpty()) && this.placing == null) {
            this.placingList = this.build.getBlock();
            this.npc.setJobData("");
            return;
        }
        if (this.placing == null) {
            this.placing = this.placingList.pop();
            if (this.placing.state.getBlock() == Blocks.STRUCTURE_VOID) {
                this.placing = null;
                return;
            }
            this.tryTicks = 0;
            this.npc.setJobData(this.blockToString(this.placing));
        }
        this.npc.getNavigation().startMovingTo((double)this.placing.pos.getX(), (double)(this.placing.pos.getY() + 1), (double)this.placing.pos.getZ(), 1.0);
        if (this.tryTicks++ > 40 || this.npc.nearPosition(this.placing.pos)) {
            BlockPos blockPos = this.placing.pos;
            this.placeBlock();
            if (this.tryTicks > 40) {
                blockPos = NoppesUtilServer.GetClosePos(blockPos, this.npc.getWorld());
                this.npc.requestTeleport((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
            }
        }
    }

    private String blockToString(BlockData data) {
        if (data.state.getBlock() == Blocks.AIR) {
            return Registries.ITEM.getId(Items.IRON_PICKAXE).toString();
        }
        return this.itemToString(data.getStack());
    }

    @Override
    public void stop() {
        this.reset();
    }

    @Override
    public void reset() {
        this.build = null;
        this.npc.setJobData("");
    }

    public void placeBlock() {
        BlockEntity tile;
        if (this.placing == null) {
            return;
        }
        this.npc.getNavigation().stop();
        this.npc.swingHand(Hand.MAIN_HAND);
        this.npc.getWorld().setBlockState(this.placing.pos, this.placing.state, 2);
        if (this.placing.state.getBlock() instanceof BlockEntityProvider && this.placing.tile != null && (tile = this.npc.getWorld().getBlockEntity(this.placing.pos)) != null) {
            try {
                tile.readNbt(this.placing.tile);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.placing = null;
    }

    @Override
    public boolean isBuilding() {
        return this.build != null && this.build.enabled && !this.build.finished && this.build.started;
    }

    @Override
    public int getType() {
        return 10;
    }
}

