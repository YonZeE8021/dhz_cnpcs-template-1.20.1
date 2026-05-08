/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiBasic;

public abstract class GuiNPCInterface
extends GuiBasic {
    public EntityNPCInterface npc;

    public GuiNPCInterface(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public GuiNPCInterface() {
    }

    @Override
    public void setSubGui(Screen gui) {
        if (gui instanceof GuiNPCInterface) {
            ((GuiNPCInterface)gui).npc = this.npc;
        }
        if (gui instanceof GuiContainerNPCInterface) {
            ((GuiContainerNPCInterface)gui).npc = this.npc;
        }
        super.setSubGui(gui);
    }

    public void drawNpc(DrawContext graphics, int x, int y) {
        this.drawNpc(graphics, (LivingEntity)this.npc, x, y, 1.0f, 0);
    }
}

