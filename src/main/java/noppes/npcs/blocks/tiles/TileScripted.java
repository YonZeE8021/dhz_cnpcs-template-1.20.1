/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.Identifier
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.blocks.tiles;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.TextBlock;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.event.Event;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.util.ValueUtil;

public class TileScripted
extends TileNpcEntity
implements IScriptBlockHandler {
    public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
    public String scriptLanguage = "ECMAScript";
    public boolean enabled = false;
    private IBlock blockDummy = null;
    public DataTimers timers = new DataTimers(this);
    public long lastInited = -1L;
    private short tickCount = 0;
    public ItemStack itemModel = new ItemStack((ItemConvertible)CustomBlocks.scripted);
    public Block blockModel = null;
    public boolean needsClientUpdate = false;
    public int powering = 0;
    public int activePowering = 0;
    public int newPower = 0;
    public int prevPower = 0;
    public boolean isPassible = false;
    public boolean isLadder = false;
    public int lightValue = 0;
    public float blockHardness = 5.0f;
    public float blockResistance = 10.0f;
    public int rotationX = 0;
    public int rotationY = 0;
    public int rotationZ = 0;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    public BlockEntity renderTile;
    public BlockState renderState;
    public boolean renderTileErrored = true;
    public BlockEntityTicker renderTileUpdate = null;
    public TextPlane text1 = new TextPlane();
    public TextPlane text2 = new TextPlane();
    public TextPlane text3 = new TextPlane();
    public TextPlane text4 = new TextPlane();
    public TextPlane text5 = new TextPlane();
    public TextPlane text6 = new TextPlane();

    public TileScripted(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_scripted, pos, state);
    }

    @Override
    public IBlock getBlock() {
        if (this.blockDummy == null) {
            this.blockDummy = new BlockScriptedWrapper(this.getWorld(), CustomBlocks.scripted, this.getPos());
        }
        return this.blockDummy;
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.setNBT(compound);
        this.setDisplayNBT(compound);
        this.timers.load(compound);
    }

    public void setNBT(NbtCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
        this.activePowering = this.powering = compound.getInt("BlockPowering");
        this.prevPower = compound.getInt("BlockPrevPower");
        if (compound.contains("BlockHardness")) {
            this.blockHardness = compound.getFloat("BlockHardness");
            this.blockResistance = compound.getFloat("BlockResistance");
        }
    }

    public void setDisplayNBT(NbtCompound compound) {
        this.itemModel = ItemStack.fromNbt((NbtCompound)compound.getCompound("ScriptBlockModel"));
        if (this.itemModel.isEmpty()) {
            this.itemModel = new ItemStack((ItemConvertible)CustomBlocks.scripted);
        }
        if (compound.contains("ScriptBlockModelBlock")) {
            this.blockModel = (Block)Registries.BLOCK.get(new Identifier(compound.getString("ScriptBlockModelBlock")));
        }
        this.renderTileUpdate = null;
        this.renderTile = null;
        this.renderTileErrored = false;
        this.lightValue = compound.getInt("LightValue");
        this.isLadder = compound.getBoolean("IsLadder");
        this.isPassible = compound.getBoolean("IsPassible");
        this.rotationX = compound.getInt("RotationX");
        this.rotationY = compound.getInt("RotationY");
        this.rotationZ = compound.getInt("RotationZ");
        this.scaleX = compound.getFloat("ScaleX");
        this.scaleY = compound.getFloat("ScaleY");
        this.scaleZ = compound.getFloat("ScaleZ");
        if (this.scaleX <= 0.0f) {
            this.scaleX = 1.0f;
        }
        if (this.scaleY <= 0.0f) {
            this.scaleY = 1.0f;
        }
        if (this.scaleZ <= 0.0f) {
            this.scaleZ = 1.0f;
        }
        if (compound.contains("Text3")) {
            this.text1.setNBT(compound.getCompound("Text1"));
            this.text2.setNBT(compound.getCompound("Text2"));
            this.text3.setNBT(compound.getCompound("Text3"));
            this.text4.setNBT(compound.getCompound("Text4"));
            this.text5.setNBT(compound.getCompound("Text5"));
            this.text6.setNBT(compound.getCompound("Text6"));
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        this.getNBT(compound);
        this.getDisplayNBT(compound);
        this.timers.save(compound);
        super.writeNbt(compound);
    }

    public NbtCompound getNBT(NbtCompound compound) {
        compound.put("Scripts", (NbtElement)NBTTags.NBTScript(this.scripts));
        compound.putString("ScriptLanguage", this.scriptLanguage);
        compound.putBoolean("ScriptEnabled", this.enabled);
        compound.putInt("BlockPowering", this.powering);
        compound.putInt("BlockPrevPower", this.prevPower);
        compound.putFloat("BlockHardness", this.blockHardness);
        compound.putFloat("BlockResistance", this.blockResistance);
        return compound;
    }

    public NbtCompound getDisplayNBT(NbtCompound compound) {
        NbtCompound itemcompound = new NbtCompound();
        this.itemModel.writeNbt(itemcompound);
        if (this.blockModel != null) {
            Identifier resourcelocation = Registries.BLOCK.getId(this.blockModel);
            compound.putString("ScriptBlockModelBlock", resourcelocation == null ? "" : resourcelocation.toString());
        }
        compound.put("ScriptBlockModel", (NbtElement)itemcompound);
        compound.putInt("LightValue", this.lightValue);
        compound.putBoolean("IsLadder", this.isLadder);
        compound.putBoolean("IsPassible", this.isPassible);
        compound.putInt("RotationX", this.rotationX);
        compound.putInt("RotationY", this.rotationY);
        compound.putInt("RotationZ", this.rotationZ);
        compound.putFloat("ScaleX", this.scaleX);
        compound.putFloat("ScaleY", this.scaleY);
        compound.putFloat("ScaleZ", this.scaleZ);
        compound.put("Text1", (NbtElement)this.text1.getNBT());
        compound.put("Text2", (NbtElement)this.text2.getNBT());
        compound.put("Text3", (NbtElement)this.text3.getNBT());
        compound.put("Text4", (NbtElement)this.text4.getNBT());
        compound.put("Text5", (NbtElement)this.text5.getNBT());
        compound.put("Text6", (NbtElement)this.text6.getNBT());
        return compound;
    }

    private boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && !this.world.isClient;
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileScripted tile) {
        if (tile.renderTileUpdate != null) {
            try {
                tile.renderTileUpdate.tick(level, pos, tile.renderState, tile.renderTile);
            }
            catch (Exception e) {
                tile.renderTileUpdate = null;
            }
        }
        tile.tickCount = (short)(tile.tickCount + 1);
        if (tile.prevPower != tile.newPower && tile.powering <= 0) {
            EventHooks.onScriptBlockRedstonePower(tile, tile.prevPower, tile.newPower);
            tile.prevPower = tile.newPower;
        }
        tile.timers.update();
        if (tile.tickCount >= 10) {
            EventHooks.onScriptBlockUpdate(tile);
            tile.tickCount = 0;
            if (tile.needsClientUpdate) {
                tile.markDirty();
                level.updateListeners(pos, state, state, 3);
                tile.needsClientUpdate = false;
            }
        }
    }

    public BlockEntityUpdateS2CPacket getUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("x", this.pos.getX());
        compound.putInt("y", this.pos.getY());
        compound.putInt("z", this.pos.getZ());
        this.getDisplayNBT(compound);
        return compound;
    }

    public void setItemModel(ItemStack item, Block b) {
        if (item == null || item.isEmpty()) {
            item = new ItemStack((ItemConvertible)CustomBlocks.scripted);
        }
        if (NoppesUtilPlayer.compareItems(item, this.itemModel, false, false) && b != this.blockModel) {
            return;
        }
        this.itemModel = item;
        this.blockModel = b;
        this.needsClientUpdate = true;
    }

    public void setLightValue(int value) {
        if (value == this.lightValue) {
            return;
        }
        this.lightValue = ValueUtil.CorrectInt(value, 0, 15);
        this.needsClientUpdate = true;
    }

    public void setRedstonePower(int strength) {
        if (this.powering == strength) {
            return;
        }
        this.prevPower = this.activePowering = ValueUtil.CorrectInt(strength, 0, 15);
        this.world.updateNeighborsAlways(this.pos, CustomBlocks.scripted);
        this.powering = this.activePowering;
    }

    public void setScale(float x, float y, float z) {
        if (this.scaleX == x && this.scaleY == y && this.scaleZ == z) {
            return;
        }
        this.scaleX = ValueUtil.correctFloat(x, 0.0f, 10.0f);
        this.scaleY = ValueUtil.correctFloat(y, 0.0f, 10.0f);
        this.scaleZ = ValueUtil.correctFloat(z, 0.0f, 10.0f);
        this.needsClientUpdate = true;
    }

    public void setRotation(int x, int y, int z) {
        if (this.rotationX == x && this.rotationY == y && this.rotationZ == z) {
            return;
        }
        this.rotationX = ValueUtil.CorrectInt(x, 0, 359);
        this.rotationY = ValueUtil.CorrectInt(y, 0, 359);
        this.rotationZ = ValueUtil.CorrectInt(z, 0, 359);
        this.needsClientUpdate = true;
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
    public String noticeString() {
        BlockPos pos = this.getPos();
        return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
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

    public class TextPlane
    implements ITextPlane {
        public boolean textHasChanged = true;
        public TextBlock textBlock;
        public String text = "";
        public int rotationX = 0;
        public int rotationY = 0;
        public int rotationZ = 0;
        public float offsetX = 0.0f;
        public float offsetY = 0.0f;
        public float offsetZ = 0.5f;
        public float scale = 1.0f;

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public void setText(String text) {
            if (this.text.equals(text)) {
                return;
            }
            this.text = text;
            this.textHasChanged = true;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public int getRotationX() {
            return this.rotationX;
        }

        @Override
        public int getRotationY() {
            return this.rotationY;
        }

        @Override
        public int getRotationZ() {
            return this.rotationZ;
        }

        @Override
        public void setRotationX(int x) {
            if (this.rotationX == (x = ValueUtil.CorrectInt(x % 360, 0, 359))) {
                return;
            }
            this.rotationX = x;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public void setRotationY(int y) {
            if (this.rotationY == (y = ValueUtil.CorrectInt(y % 360, 0, 359))) {
                return;
            }
            this.rotationY = y;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public void setRotationZ(int z) {
            if (this.rotationZ == (z = ValueUtil.CorrectInt(z % 360, 0, 359))) {
                return;
            }
            this.rotationZ = z;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public float getOffsetX() {
            return this.offsetX;
        }

        @Override
        public float getOffsetY() {
            return this.offsetY;
        }

        @Override
        public float getOffsetZ() {
            return this.offsetZ;
        }

        @Override
        public void setOffsetX(float x) {
            if (this.offsetX == (x = ValueUtil.correctFloat(x, -1.0f, 1.0f))) {
                return;
            }
            this.offsetX = x;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public void setOffsetY(float y) {
            if (this.offsetY == (y = ValueUtil.correctFloat(y, -1.0f, 1.0f))) {
                return;
            }
            this.offsetY = y;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public void setOffsetZ(float z) {
            if (this.offsetZ == (z = ValueUtil.correctFloat(z, -1.0f, 1.0f))) {
                return;
            }
            System.out.println(this.rotationZ);
            this.offsetZ = z;
            TileScripted.this.needsClientUpdate = true;
        }

        @Override
        public float getScale() {
            return this.scale;
        }

        @Override
        public void setScale(float scale) {
            if (this.scale == scale) {
                return;
            }
            this.scale = scale;
            TileScripted.this.needsClientUpdate = true;
        }

        public NbtCompound getNBT() {
            NbtCompound compound = new NbtCompound();
            compound.putString("Text", this.text);
            compound.putInt("RotationX", this.rotationX);
            compound.putInt("RotationY", this.rotationY);
            compound.putInt("RotationZ", this.rotationZ);
            compound.putFloat("OffsetX", this.offsetX);
            compound.putFloat("OffsetY", this.offsetY);
            compound.putFloat("OffsetZ", this.offsetZ);
            compound.putFloat("Scale", this.scale);
            return compound;
        }

        public void setNBT(NbtCompound compound) {
            this.setText(compound.getString("Text"));
            this.rotationX = compound.getInt("RotationX");
            this.rotationY = compound.getInt("RotationY");
            this.rotationZ = compound.getInt("RotationZ");
            this.offsetX = compound.getFloat("OffsetX");
            this.offsetY = compound.getFloat("OffsetY");
            this.offsetZ = compound.getFloat("OffsetZ");
            this.scale = compound.getFloat("Scale");
        }
    }
}

