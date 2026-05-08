/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.shared.common.PacketBasic;

public class PacketItemUpdate
extends PacketBasic {
    private final int id;
    private NbtCompound data;

    public PacketItemUpdate(int id, NbtCompound data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketItemUpdate msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketItemUpdate decode(PacketByteBuf buf) {
        return new PacketItemUpdate(buf.readInt(), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        ItemStack stack = this.player.getInventory().getStack(this.id);
        if (!stack.isEmpty()) {
            ((ItemStackWrapper)NpcAPI.Instance().getIItemStack(stack)).setMCNbt(this.data);
        }
    }
}

