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
import noppes.npcs.client.controllers.ClientSkinController;
import noppes.npcs.controllers.data.PlayerSkinData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncSkin
extends PacketBasic {
    private final String name;
    private final PlayerSkinData skinData;

    public PacketSyncSkin(String name, PlayerSkinData skinData) {
        this.name = name;
        this.skinData = skinData;
    }

    public static void encode(PacketSyncSkin msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        NbtCompound tag = new NbtCompound();
        msg.skinData.saveNBTData(tag);
        buf.writeNbt(tag);
    }

    public static PacketSyncSkin decode(PacketByteBuf buf) {
        String name = buf.readString();
        NbtCompound tag = buf.readNbt();
        PlayerSkinData skinData = new PlayerSkinData();
        skinData.loadNBTData(tag);
        return new PacketSyncSkin(name, skinData);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        ClientSkinController.addSkinForPlayer(this.name, this.skinData);
    }
}

