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
import noppes.npcs.api.wrapper.OverlayWrapper;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverlayShow
extends PacketBasic {
    private final NbtCompound compound;

    public PacketOverlayShow(NbtCompound compound) {
        this.compound = compound;
    }

    public static void encode(PacketOverlayShow msg, PacketByteBuf buf) {
        buf.writeNbt(msg.compound);
    }

    public static PacketOverlayShow decode(PacketByteBuf buf) {
        return new PacketOverlayShow(buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        OverlayWrapper wrapper = new OverlayWrapper(0);
        wrapper.fromNbt(this.compound);
        OverlayController.getInstance().addOverlay(wrapper);
    }
}

