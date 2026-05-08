/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.blocks.tiles;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.BlockState;
import noppes.npcs.CustomBlocks;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.event.Event;
import noppes.npcs.api.wrapper.BlockScriptedDoorWrapper;
import noppes.npcs.blocks.tiles.TileDoor;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;

public class TileScriptedDoor
extends TileDoor
implements IScriptBlockHandler {
    public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
    public boolean shouldRefreshData = false;
    public String scriptLanguage = "ECMAScript";
    public boolean enabled = false;
    private IBlock blockDummy = null;
    public DataTimers timers = new DataTimers(this);
    public long lastInited = -1L;
    private short tickCount = 0;
    public int newPower = 0;
    public int prevPower = 0;
    public float blockHardness = 5.0f;
    public float blockResistance = 10.0f;

    public TileScriptedDoor(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_scripteddoor, pos, state);
    }

    @Override
    public IBlock getBlock() {
        if (this.blockDummy == null) {
            this.blockDummy = new BlockScriptedDoorWrapper(this.getWorld(), CustomBlocks.scripted_door, this.getPos());
        }
        return this.blockDummy;
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.setNBT(compound);
        this.timers.load(compound);
    }

    public void setNBT(NbtCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
        this.prevPower = compound.getInt("BlockPrevPower");
        if (compound.contains("BlockHardness")) {
            this.blockHardness = compound.getFloat("BlockHardness");
            this.blockResistance = compound.getFloat("BlockResistance");
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        this.getNBT(compound);
        this.timers.save(compound);
        super.writeNbt(compound);
    }

    public NbtCompound getNBT(NbtCompound compound) {
        compound.put("Scripts", (NbtElement)NBTTags.NBTScript(this.scripts));
        compound.putString("ScriptLanguage", this.scriptLanguage);
        compound.putBoolean("ScriptEnabled", this.enabled);
        compound.putInt("BlockPrevPower", this.prevPower);
        compound.putFloat("BlockHardness", this.blockHardness);
        compound.putFloat("BlockResistance", this.blockResistance);
        return compound;
    }

    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastInited) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT) {
                EventHooks.onScriptBlockInit(this);
            }
        }
        for (ScriptContainer script : this.scripts) {
            script.run(type, event);
        }
    }

    private boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && !this.world.isClient;
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileScriptedDoor tile) {
        tile.tickCount = (short)(tile.tickCount + 1);
        if (tile.prevPower != tile.newPower) {
            EventHooks.onScriptBlockRedstonePower(tile, tile.prevPower, tile.newPower);
            tile.prevPower = tile.newPower;
        }
        tile.timers.update();
        if (tile.tickCount >= 10) {
            EventHooks.onScriptBlockUpdate(tile);
            tile.tickCount = 0;
        }
    }

    @Override
    public boolean isClient() {
        return this.getWorld().isClient;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }

    @Override
    public String getLanguage() {
        return this.scriptLanguage;
    }

    @Override
    public void setLanguage(String lang) {
        this.scriptLanguage = lang;
    }

    @Override
    public List<ScriptContainer> getScripts() {
        return this.scripts;
    }

    @Override
    public String noticeString() {
        BlockPos pos = this.getPos();
        return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }

    @Override
    public Map<Long, String> getConsoleText() {
        TreeMap<Long, String> map = new TreeMap<Long, String>();
        int tab = 0;
        for (ScriptContainer script : this.getScripts()) {
            ++tab;
            for (Map.Entry<Long, String> entry : script.console.entrySet()) {
                map.put(entry.getKey(), " tab " + tab + ":\n" + entry.getValue());
            }
        }
        return map;
    }

    @Override
    public void clearConsole() {
        for (ScriptContainer script : this.getScripts()) {
            script.console.clear();
        }
    }
}

