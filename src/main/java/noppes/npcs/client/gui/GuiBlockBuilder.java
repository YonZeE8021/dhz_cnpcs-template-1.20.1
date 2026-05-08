/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtHelper
 *  net.minecraft.text.Text
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.registry.RegistryEntryLookup
 *  net.minecraft.registry.RegistryKeys
 */
package noppes.npcs.client.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSchematicsTileBuild;
import noppes.npcs.packets.server.SPacketSchematicsTileGet;
import noppes.npcs.packets.server.SPacketSchematicsTileSave;
import noppes.npcs.packets.server.SPacketSchematicsTileSet;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

public class GuiBlockBuilder
extends GuiNPCInterface
implements IGuiData,
ICustomScrollListener,
IScrollData,
BooleanConsumer {
    private BlockPos pos;
    private TileBuilder tile;
    private GuiCustomScrollNop scroll;
    private ISchematic selected = null;

    public GuiBlockBuilder(BlockPos pos) {
        this.pos = pos;
        this.setBackground("menubg.png");
        this.imageWidth = 256;
        this.imageHeight = 216;
        this.tile = (TileBuilder)this.player.getWorld().getBlockEntity(pos);
        Packets.sendServer(new SPacketSchematicsTileGet(pos));
    }

    @Override
    public void init() {
        super.init();
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop(this, 0);
            this.scroll.setSize(125, 208);
        }
        this.scroll.guiLeft = this.guiLeft + 4;
        this.scroll.guiTop = this.guiTop + 4;
        this.addScroll(this.scroll);
        if (this.selected != null) {
            int y = this.guiTop + 4;
            int size = this.selected.getWidth() * this.selected.getHeight() * this.selected.getLength();
            this.addButton(new GuiButtonYesNo((IGuiInterface)this, 3, this.guiLeft + 200, y, TileBuilder.DrawPos != null && this.tile.getPos().equals(TileBuilder.DrawPos)));
            this.addLabel(new GuiLabel(3, "schematic.preview", this.guiLeft + 130, y + 5));
            this.addLabel(new GuiLabel(0, I18n.translate((String)"schematic.width", (Object[])new Object[0]) + ": " + this.selected.getWidth(), this.guiLeft + 130, y += 21));
            this.addLabel(new GuiLabel(1, I18n.translate((String)"schematic.length", (Object[])new Object[0]) + ": " + this.selected.getLength(), this.guiLeft + 130, y += 11));
            this.addLabel(new GuiLabel(2, I18n.translate((String)"schematic.height", (Object[])new Object[0]) + ": " + this.selected.getHeight(), this.guiLeft + 130, y += 11));
            this.addButton(new GuiButtonYesNo((IGuiInterface)this, 4, this.guiLeft + 200, y += 14, this.tile.enabled));
            this.addLabel(new GuiLabel(4, I18n.translate((String)"gui.enabled", (Object[])new Object[0]), this.guiLeft + 130, y + 5));
            this.addButton(new GuiButtonYesNo((IGuiInterface)this, 7, this.guiLeft + 200, y += 22, this.tile.finished));
            this.addLabel(new GuiLabel(7, I18n.translate((String)"gui.finished", (Object[])new Object[0]), this.guiLeft + 130, y + 5));
            this.addButton(new GuiButtonYesNo((IGuiInterface)this, 8, this.guiLeft + 200, y += 22, this.tile.started));
            this.addLabel(new GuiLabel(8, I18n.translate((String)"gui.started", (Object[])new Object[0]), this.guiLeft + 130, y + 5));
            this.addTextField(new GuiTextFieldNop(9, (Screen)this, this.guiLeft + 200, y += 22, 50, 20, "" + this.tile.yOffest));
            this.addLabel(new GuiLabel(9, I18n.translate((String)"gui.yoffset", (Object[])new Object[0]), this.guiLeft + 130, y + 5));
            this.getTextField((int)9).numbersOnly = true;
            this.getTextField(9).setMinMaxDefault(-10, 10, 0);
            this.addButton(new GuiButtonNop((IGuiInterface)this, 5, this.guiLeft + 200, y += 22, 50, 20, new String[]{"0", "90", "180", "270"}, this.tile.rotation));
            this.addLabel(new GuiLabel(5, I18n.translate((String)"movement.rotation", (Object[])new Object[0]), this.guiLeft + 130, y + 5));
            this.addButton(new GuiButtonNop(this, 6, this.guiLeft + 130, y += 22, 120, 20, "availability.options"));
            this.addButton(new GuiButtonNop(this, 10, this.guiLeft + 130, y += 22, 120, 20, "schematic.instantBuild"));
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 3) {
            GuiButtonYesNo button = (GuiButtonYesNo)guibutton;
            if (button.getBoolean()) {
                TileBuilder.SetDrawPos(this.pos);
                this.tile.setDrawSchematic(new SchematicWrapper(this.selected));
            } else {
                TileBuilder.SetDrawPos(null);
                this.tile.setDrawSchematic(null);
            }
        }
        if (guibutton.id == 4) {
            this.tile.enabled = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 5) {
            this.tile.rotation = guibutton.getValue();
            TileBuilder.Compiled = false;
        }
        if (guibutton.id == 6) {
            this.setSubGui(new SubGuiNpcAvailability(this.tile.availability));
        }
        if (guibutton.id == 7) {
            this.tile.finished = ((GuiButtonYesNo)guibutton).getBoolean();
            Packets.sendServer(new SPacketSchematicsTileSet(this.pos, this.scroll.getSelected()));
        }
        if (guibutton.id == 8) {
            this.tile.started = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 10) {
            this.save();
            ConfirmScreen guiyesno = new ConfirmScreen((BooleanConsumer)this, (Text)Text.empty(), (Text)Text.translatable((String)"schematic.instantBuildText"));
            this.setScreen((Screen)guiyesno);
        }
    }

    @Override
    public void save() {
        if (this.tile == null) {
            return;
        }
        if (this.getTextField(9) != null) {
            this.tile.yOffest = this.getTextField(9).getInteger();
        }
        Packets.sendServer(new SPacketSchematicsTileSave(this.pos, this.tile.writePartNBT(new NbtCompound())));
    }

    @Override
    public void setGuiData(final NbtCompound compound) {
        if (compound.contains("Width")) {
            final ArrayList<BlockState> states = new ArrayList<BlockState>();
            NbtList list = compound.getList("Data", 10);
            for (int i = 0; i < list.size(); ++i) {
                states.add(NbtHelper.toBlockState((RegistryEntryLookup)this.player.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), (NbtCompound)list.getCompound(i)));
            }
            this.selected = new ISchematic(){

                @Override
                public short getWidth() {
                    return compound.getShort("Width");
                }

                @Override
                public int getBlockEntityDimensions() {
                    return 0;
                }

                @Override
                public NbtCompound getBlockEntity(int i) {
                    return null;
                }

                @Override
                public String getName() {
                    return compound.getString("SchematicName");
                }

                @Override
                public short getLength() {
                    return compound.getShort("Length");
                }

                @Override
                public short getHeight() {
                    return compound.getShort("Height");
                }

                @Override
                public BlockState getBlockState(int i) {
                    return (BlockState)states.get(i);
                }

                @Override
                public BlockState getBlockState(int x, int y, int z) {
                    return this.getBlockState((y * this.getLength() + z) * this.getWidth() + x);
                }

                @Override
                public NbtCompound getNBT() {
                    return null;
                }
            };
            if (TileBuilder.DrawPos != null && TileBuilder.DrawPos.equals(this.tile.getPos())) {
                SchematicWrapper wrapper = new SchematicWrapper(this.selected);
                wrapper.rotation = this.tile.rotation;
                this.tile.setDrawSchematic(wrapper);
            }
            this.scroll.setSelected(this.selected.getName());
            this.scroll.scrollTo(this.selected.getName());
        } else {
            this.tile.readPartNBT(compound);
        }
        this.init();
    }

    public void accept(boolean flag) {
        if (flag) {
            Packets.sendServer(new SPacketSchematicsTileBuild(this.pos));
            this.close();
            this.selected = null;
        } else {
            NoppesUtil.openGUI((PlayerEntity)this.player, this);
        }
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
        if (!scroll.hasSelected()) {
            return;
        }
        if (this.selected != null) {
            this.getButton(3).setDisplay(0);
        }
        TileBuilder.SetDrawPos(null);
        this.tile.setDrawSchematic(null);
        Packets.sendServer(new SPacketSchematicsTileSet(this.pos, scroll.getSelected()));
    }

    @Override
    public void setData(Vector<String> list, Map<String, Integer> data) {
        this.scroll.setList(list);
        if (this.selected != null) {
            this.scroll.setSelected(this.selected.getName());
        }
        this.init();
    }

    @Override
    public void setSelected(String selected) {
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }
}

