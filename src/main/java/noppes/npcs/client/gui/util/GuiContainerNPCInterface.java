/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 */
package noppes.npcs.client.gui.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiBasicContainer;

public abstract class GuiContainerNPCInterface<T extends ScreenHandler>
extends GuiBasicContainer<T> {
    public EntityNPCInterface npc;

    public GuiContainerNPCInterface(EntityNPCInterface npc, T cont, PlayerInventory inv, Text titleIn) {
        super(cont, inv, titleIn);
        this.npc = npc;
    }

    public void drawNpc(DrawContext graphics, int x, int y) {
        this.wrapper.drawNpc(graphics, (LivingEntity)this.npc, x, y, 1.0f, 0, this.guiLeft, this.guiTop);
    }
}

