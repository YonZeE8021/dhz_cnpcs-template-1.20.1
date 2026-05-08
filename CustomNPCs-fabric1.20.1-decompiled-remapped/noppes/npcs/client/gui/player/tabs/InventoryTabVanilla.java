/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 */
package noppes.npcs.client.gui.player.tabs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import noppes.npcs.client.gui.player.tabs.AbstractTab;

public class InventoryTabVanilla
extends AbstractTab {
    public InventoryTabVanilla() {
        super(0, 0, 0, new ItemStack((ItemConvertible)Blocks.field_9980));
    }

    @Override
    public void onTabClicked() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
        InventoryScreen inventory = new InventoryScreen((PlayerEntity)mc.player);
        mc.setScreen((Screen)inventory);
    }

    @Override
    public boolean shouldAddToList() {
        return true;
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }
}

