/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.loot.context.LootContextTypes
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.world.BlockView
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.CropBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.GourdBlock
 *  net.minecraft.block.StemBlock
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.LootableContainerBlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.loot.LootTables
 *  net.minecraft.loot.LootTable
 *  net.minecraft.loot.context.LootContextParameterSet$Builder
 */
package noppes.npcs.roles;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ItemEntity;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.role.IJobFarmer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobFarmer
extends JobInterface
implements MassBlockController.IMassBlock,
IJobFarmer {
    public int chestMode = 1;
    private List<BlockPos> trackedBlocks = new ArrayList<BlockPos>();
    private int ticks = 0;
    private int walkTicks = 0;
    private int blockTicks = 800;
    private boolean waitingForBlocks = false;
    private BlockPos ripe = null;
    private BlockPos chest = null;
    private ItemStack holding = ItemStack.EMPTY;

    public JobFarmer(EntityNPCInterface npc) {
        super(npc);
        this.overrideMainHand = true;
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
    public NbtCompound save(NbtCompound compound) {
        compound.putInt("JobChestMode", this.chestMode);
        if (!this.holding.isEmpty()) {
            compound.put("JobHolding", (NbtElement)this.holding.writeNbt(new NbtCompound()));
        }
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        this.chestMode = compound.getInt("JobChestMode");
        this.holding = ItemStack.fromNbt((NbtCompound)compound.getCompound("JobHolding"));
        this.blockTicks = 1100;
    }

    public void setHolding(ItemStack item) {
        this.holding = item;
        this.npc.setJobData(this.itemToString(this.holding));
    }

    @Override
    public boolean aiShouldExecute() {
        if (!this.holding.isEmpty()) {
            if (this.chestMode == 0) {
                this.setHolding(ItemStack.EMPTY);
            } else if (this.chestMode == 1) {
                if (this.chest == null) {
                    this.dropItem(this.holding);
                    this.setHolding(ItemStack.EMPTY);
                } else {
                    this.chest();
                }
            } else if (this.chestMode == 2) {
                this.dropItem(this.holding);
                this.setHolding(ItemStack.EMPTY);
            }
            return false;
        }
        if (this.ripe != null) {
            this.pluck();
            return false;
        }
        if (!this.waitingForBlocks && this.blockTicks++ > 1200) {
            this.blockTicks = 0;
            this.waitingForBlocks = true;
            MassBlockController.Queue(this);
        }
        if (this.ticks++ < 100) {
            return false;
        }
        this.ticks = 0;
        return true;
    }

    private void dropItem(ItemStack item) {
        ItemEntity entityitem = new ItemEntity(this.npc.getWorld(), this.npc.getX(), this.npc.getY(), this.npc.getZ(), item);
        entityitem.setToDefaultPickupDelay();
        this.npc.getWorld().spawnEntity((Entity)entityitem);
    }

    private void chest() {
        BlockPos pos = this.chest;
        this.npc.getNavigation().startMovingTo((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 1.0);
        this.npc.getLookControl().lookAt((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 10.0f, (float)this.npc.getMaxLookPitchChange());
        if (this.npc.nearPosition(pos) || this.walkTicks++ > 400) {
            Inventory inventory;
            if (this.walkTicks < 400) {
                this.npc.swingHand(Hand.field_5808);
            }
            this.npc.getNavigation().stop();
            this.ticks = 100;
            this.walkTicks = 0;
            BlockState state = this.npc.getWorld().getBlockState(pos);
            BlockEntity tile = this.npc.getWorld().getBlockEntity(pos);
            Inventory class_12632 = inventory = tile instanceof Inventory ? (Inventory)tile : null;
            if (state.getBlock() instanceof ChestBlock) {
                inventory = ChestBlock.getInventory((ChestBlock)((ChestBlock)state.getBlock()), (BlockState)state, (World)this.npc.getWorld(), (BlockPos)pos, (boolean)true);
            }
            if (inventory != null) {
                int i;
                for (i = 0; !this.holding.isEmpty() && i < inventory.size(); ++i) {
                    this.holding = this.mergeStack(inventory, i, this.holding);
                }
                for (i = 0; !this.holding.isEmpty() && i < inventory.size(); ++i) {
                    ItemStack item = inventory.getStack(i);
                    if (!item.isEmpty()) continue;
                    inventory.setStack(i, this.holding);
                    this.holding = ItemStack.EMPTY;
                }
                if (!this.holding.isEmpty()) {
                    this.dropItem(this.holding);
                    this.holding = ItemStack.EMPTY;
                }
            } else {
                this.chest = null;
            }
            this.setHolding(this.holding);
        }
    }

    private ItemStack mergeStack(Inventory inventory, int slot, ItemStack item) {
        ItemStack item2 = inventory.getStack(slot);
        if (!NoppesUtilPlayer.compareItems(item, item2, false, false)) {
            return item;
        }
        int size = item2.getMaxCount() - item2.getCount();
        if (size >= item.getCount()) {
            item2.setCount(item2.getCount() + item.getCount());
            return ItemStack.EMPTY;
        }
        item2.setCount(item2.getMaxCount());
        item.setCount(item.getCount() - size);
        if (item.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return item;
    }

    private void pluck() {
        BlockPos pos = this.ripe;
        this.npc.getNavigation().startMovingTo((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 1.0);
        this.npc.getLookControl().lookAt((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 10.0f, (float)this.npc.getMaxLookPitchChange());
        if (this.npc.nearPosition(pos) || this.walkTicks++ > 400) {
            if (this.walkTicks > 400) {
                pos = NoppesUtilServer.GetClosePos(pos, this.npc.getWorld());
                this.npc.requestTeleport((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
            }
            this.ripe = null;
            this.npc.getNavigation().stop();
            this.ticks = 90;
            this.walkTicks = 0;
            this.npc.swingHand(Hand.field_5808);
            BlockState state = this.npc.getWorld().getBlockState(pos);
            Block b = state.getBlock();
            if (b instanceof CropBlock && ((CropBlock)b).isMature(state)) {
                CropBlock crop = (CropBlock)b;
                Item item = crop.getPickStack((BlockView)this.npc.getWorld(), pos, state).getItem();
                LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)this.npc.getWorld()).add(LootContextParameters.field_24424, (Object)Vec3d.ofCenter((Vec3i)pos)).add(LootContextParameters.field_1229, (Object)this.npc.getMainHandStack()).add(LootContextParameters.field_1224, (Object)state).addOptional(LootContextParameters.field_1228, (Object)this.npc.getWorld().getBlockEntity(pos));
                LootTable loottable = this.npc.getServer().getLootManager().getLootTable(b.getLootTableId());
                ObjectArrayList l = loottable.generateLoot(builder.build(LootContextTypes.field_1172));
                this.npc.getWorld().setBlockState(pos, crop.withAge(0), 2);
                if (l.isEmpty()) {
                    this.holding = ItemStack.EMPTY;
                } else if (l.size() == 1) {
                    this.holding = (ItemStack)l.get(0);
                } else {
                    ObjectArrayList fl = l.stream().filter(t -> t.getItem() != item).collect(Collectors.toList());
                    if (fl.isEmpty()) {
                        fl = l;
                    }
                    this.holding = (ItemStack)fl.get(this.npc.getRandom().nextInt(fl.size()));
                }
                this.holding.setCount(1);
            }
            if (b instanceof GourdBlock) {
                b = this.npc.getWorld().getBlockState(pos).getBlock();
                this.npc.getWorld().removeBlock(pos, false);
                this.holding = new ItemStack((ItemConvertible)b);
            }
            this.setHolding(this.holding);
        }
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public void aiUpdateTask() {
        Iterator<BlockPos> ite = this.trackedBlocks.iterator();
        while (ite.hasNext() && this.ripe == null) {
            BlockPos pos = ite.next();
            BlockState state = this.npc.getWorld().getBlockState(pos);
            Block b = state.getBlock();
            if ((b instanceof CropBlock && ((CropBlock)b).isMature(state) || b instanceof GourdBlock) && b.getLootTableId() != LootTables.EMPTY) {
                this.ripe = pos;
                continue;
            }
            ite.remove();
        }
        boolean bl = this.npc.ais.returnToStart = this.ripe == null;
        if (this.ripe != null) {
            this.npc.getNavigation().stop();
            this.npc.getLookControl().lookAt((double)this.ripe.getX(), (double)this.ripe.getY(), (double)this.ripe.getZ(), 10.0f, (float)this.npc.getMaxLookPitchChange());
        }
    }

    @Override
    public boolean isPlucking() {
        return this.ripe != null || !this.holding.isEmpty();
    }

    @Override
    public EntityNPCInterface getNpc() {
        return this.npc;
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public void processed(List<BlockData> list) {
        ArrayList<BlockPos> trackedBlocks = new ArrayList<BlockPos>();
        BlockPos chest = null;
        for (BlockData data : list) {
            BlockEntity tile = this.npc.getWorld().getBlockEntity(data.pos);
            Block b = data.state.getBlock();
            if (tile instanceof LootableContainerBlockEntity) {
                if (chest != null && !(this.npc.squaredDistanceTo(chest.getX(), chest.getY(), chest.getZ()) > this.npc.squaredDistanceTo(data.pos.getX(), data.pos.getY(), data.pos.getZ()))) continue;
                chest = data.pos;
                continue;
            }
            if (!(b instanceof CropBlock) && !(b instanceof StemBlock) || trackedBlocks.contains(data.pos)) continue;
            trackedBlocks.add(data.pos);
        }
        this.chest = chest;
        this.trackedBlocks = trackedBlocks;
        this.waitingForBlocks = false;
    }

    @Override
    public EnumSet<Goal.Control> getFlags() {
        return EnumSet.of(Goal.Control.field_18405);
    }

    @Override
    public int getType() {
        return 11;
    }
}

