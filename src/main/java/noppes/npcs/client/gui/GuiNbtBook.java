/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.BlockView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.StringNbtReader
 *  net.minecraft.text.Text
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.client.gui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNbtBookBlockSave;
import noppes.npcs.packets.server.SPacketNbtBookEntitySave;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNbtBook
extends GuiNPCInterface
implements IGuiData {
    private BlockPos pos;
    private BlockEntity tile;
    private BlockState state;
    private ItemStack blockStack;
    private int entityId;
    private Entity entity;
    private NbtCompound originalCompound;
    private NbtCompound compound;
    private String faultyText = null;
    private String errorMessage = null;

    public GuiNbtBook(BlockPos pos) {
        this.pos = pos;
        this.setBackground("menubg.png");
        this.imageWidth = 256;
        this.imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        int y = this.guiTop + 40;
        if (this.state != null) {
            this.addLabel(new GuiLabel(11, "x: " + this.pos.getX() + ", y: " + this.pos.getY() + ", z: " + this.pos.getZ(), this.guiLeft + 60, this.guiTop + 6));
            this.addLabel(new GuiLabel(12, "id: " + String.valueOf(Registries.BLOCK.getId(this.state.getBlock())), this.guiLeft + 60, this.guiTop + 16));
        }
        if (this.entity != null) {
            this.addLabel(new GuiLabel(12, "id: " + this.entity.getType().getTranslationKey(), this.guiLeft + 60, this.guiTop + 6));
        }
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 38, this.guiTop + 144, 180, 20, "nbt.edit"));
        this.getButton((int)0).active = this.compound != null && !this.compound.isEmpty();
        this.addLabel(new GuiLabel(0, "", this.guiLeft + 4, this.guiTop + 167));
        this.addLabel(new GuiLabel(1, "", this.guiLeft + 4, this.guiTop + 177));
        this.addButton(new GuiButtonNop(this, 66, this.guiLeft + 128, this.guiTop + 190, 120, 20, "gui.close"));
        this.addButton(new GuiButtonNop(this, 67, this.guiLeft + 4, this.guiTop + 190, 120, 20, "gui.save"));
        if (this.errorMessage != null) {
            this.getButton((int)67).active = false;
            int i = this.errorMessage.indexOf(" at: ");
            if (i > 0) {
                this.getLabel(0).setMessage((Text)Text.translatable((String)this.errorMessage.substring(0, i)));
                this.getLabel(1).setMessage((Text)Text.translatable((String)this.errorMessage.substring(i)));
            } else {
                this.getLabel(0).setMessage((Text)Text.translatable((String)this.errorMessage));
            }
        }
        if (this.getButton((int)67).active && this.originalCompound != null) {
            this.getButton((int)67).active = !this.originalCompound.equals(this.compound);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 0) {
            if (this.faultyText != null) {
                this.setSubGui(new GuiTextAreaScreen(this.compound.toString(), this.faultyText).enableHighlighting());
            } else {
                this.setSubGui(new GuiTextAreaScreen(this.compound.toString()).enableHighlighting());
            }
        }
        if (id == 67) {
            this.getLabel(0).setMessage((Text)Text.translatable((String)"Saved"));
            if (this.compound.equals(this.originalCompound)) {
                return;
            }
            if (this.tile == null) {
                Packets.sendServer(new SPacketNbtBookEntitySave(this.entityId, this.compound));
                return;
            }
            Packets.sendServer(new SPacketNbtBookBlockSave(this.pos, this.compound));
            this.originalCompound = this.compound.copy();
            this.getButton((int)67).active = false;
        }
        if (id == 66) {
            this.close();
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.hasSubGui()) {
            return;
        }
        MatrixStack matrixStack = graphics.getMatrices();
        if (this.state != null) {
            matrixStack.push();
            matrixStack.translate((float)(this.guiLeft + 4), (float)(this.guiTop + 4), 0.0f);
            matrixStack.scale(3.0f, 3.0f, 3.0f);
            graphics.drawItem(this.blockStack, 0, 0);
            graphics.drawItemInSlot(this.textRenderer, this.blockStack, 0, 0);
            matrixStack.pop();
        }
        if (this.entity instanceof LivingEntity) {
            this.drawNpc(graphics, (LivingEntity)this.entity, 20, 80, 1.0f, 0);
        }
    }

    @Override
    public void subGuiClosed(Screen gui) {
        if (gui instanceof GuiTextAreaScreen) {
            try {
                this.compound = StringNbtReader.parse((String)((GuiTextAreaScreen)gui).text);
                this.faultyText = null;
                this.errorMessage = null;
            }
            catch (CommandSyntaxException e) {
                this.errorMessage = e.getLocalizedMessage();
                this.faultyText = ((GuiTextAreaScreen)gui).text;
            }
            this.init();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        if (compound.contains("EntityId")) {
            this.entityId = compound.getInt("EntityId");
            this.entity = this.player.getWorld().getEntityById(this.entityId);
        } else {
            this.tile = this.player.getWorld().getBlockEntity(this.pos);
            this.state = this.player.getWorld().getBlockState(this.pos);
            this.blockStack = this.state.getBlock().getPickStack((BlockView)this.player.getWorld(), this.pos, this.state);
        }
        this.originalCompound = compound.getCompound("Data");
        this.compound = this.originalCompound.copy();
        this.init();
    }
}

