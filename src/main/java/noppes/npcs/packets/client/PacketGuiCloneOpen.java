/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiCloneOpen
extends PacketBasic {
    private final NbtCompound data;

    public PacketGuiCloneOpen(NbtCompound data) {
        this.data = data;
    }

    public static void encode(PacketGuiCloneOpen msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketGuiCloneOpen decode(PacketByteBuf buf) {
        return new PacketGuiCloneOpen(buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        NoppesUtil.openGUI(this.player, new GuiNpcMobSpawnerAdd(this.data));
    }
}

