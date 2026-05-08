/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketTileEntitySave;

public class SPacketSchematicsStore
extends PacketServerBasic {
    private String name;
    private NbtCompound data;

    public SPacketSchematicsStore(String name, NbtCompound data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsStore msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeNbt(msg.data);
    }

    public static SPacketSchematicsStore decode(PacketByteBuf buf) {
        return new SPacketSchematicsStore(buf.readString(Short.MAX_VALUE), buf.readNbt());
    }

    @Override
    protected void handle() {
        TileCopy tile = (TileCopy)SPacketTileEntitySave.saveTileEntity(this.player, this.data);
        if (tile == null || this.name.isEmpty()) {
            return;
        }
        SchematicController.Instance.save(this.player.getCommandSource(), this.name, tile.getPos(), tile.height, tile.width, tile.length);
    }
}

