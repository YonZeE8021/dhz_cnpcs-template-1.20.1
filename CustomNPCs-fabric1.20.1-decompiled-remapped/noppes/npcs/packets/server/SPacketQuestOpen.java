/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketQuestOpen
extends PacketServerBasic {
    private EnumGuiType gui;
    private NbtCompound data;

    public SPacketQuestOpen(EnumGuiType gui, NbtCompound data) {
        this.gui = gui;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    public static void encode(SPacketQuestOpen msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.gui);
        buf.writeNbt(msg.data);
    }

    public static SPacketQuestOpen decode(PacketByteBuf buf) {
        return new SPacketQuestOpen((EnumGuiType)buf.readEnumConstant(EnumGuiType.class), buf.readNbt());
    }

    @Override
    protected void handle() {
        Quest quest = new Quest(null);
        quest.readNBT(this.data);
        NoppesUtilServer.setEditingQuest((PlayerEntity)this.player, quest);
        NoppesUtilServer.openContainerGui(this.player, this.gui, buf -> buf.writeBlockPos(BlockPos.ORIGIN));
    }
}

