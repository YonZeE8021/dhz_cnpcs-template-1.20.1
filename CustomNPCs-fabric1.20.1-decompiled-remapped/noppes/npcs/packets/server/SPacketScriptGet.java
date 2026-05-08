/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.block.entity.BlockEntity
 */
package noppes.npcs.packets.server;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.block.entity.BlockEntity;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.ForgeEventHandler;
import noppes.npcs.NBTTags;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketScriptGet
extends PacketServerBasic {
    private int type;

    public SPacketScriptGet(int type) {
        this.type = type;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.scripted_item || item.getItem() == CustomItems.scripter || item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.scripted_door_item || item.getItem() == CustomBlocks.scripted_item;
    }

    @Override
    public boolean requiresNpc() {
        return this.type == 0;
    }

    public static void encode(SPacketScriptGet msg, PacketByteBuf buf) {
        buf.writeInt(msg.type);
    }

    public static SPacketScriptGet decode(PacketByteBuf buf) {
        return new SPacketScriptGet(buf.readInt());
    }

    @Override
    protected void handle() {
        BlockEntity tile;
        PlayerData data;
        NbtCompound compound = new NbtCompound();
        if (this.type == 0) {
            this.npc.script.save(compound);
            compound.put("Methods", (NbtElement)NBTTags.nbtStringList(Arrays.stream(EnumScriptType.npcScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if (this.type == 1) {
            data = PlayerData.get((PlayerEntity)this.player);
            tile = this.player.getWorld().getBlockEntity(data.scriptBlockPos);
            if (!(tile instanceof TileScripted)) {
                return;
            }
            ((TileScripted)tile).getNBT(compound);
            compound.put("Methods", (NbtElement)NBTTags.nbtStringList(Arrays.stream(EnumScriptType.blockScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if (this.type == 2) {
            ItemScriptedWrapper iw = (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(this.player.getMainHandStack());
            compound = iw.getMCNbt();
            compound.put("Methods", (NbtElement)NBTTags.nbtStringList(Arrays.stream(EnumScriptType.itemScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if (this.type == 3) {
            ScriptController.Instance.forgeScripts.save(compound);
            compound.put("Methods", (NbtElement)NBTTags.nbtStringList(ForgeEventHandler.eventNames));
        }
        if (this.type == 4) {
            ScriptController.Instance.playerScripts.save(compound);
            compound.put("Methods", (NbtElement)NBTTags.nbtStringList(Arrays.stream(EnumScriptType.playerScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if (this.type == 5) {
            data = PlayerData.get((PlayerEntity)this.player);
            tile = this.player.getWorld().getBlockEntity(data.scriptBlockPos);
            if (!(tile instanceof TileScriptedDoor)) {
                return;
            }
            ((TileScriptedDoor)tile).getNBT(compound);
            compound.put("Methods", (NbtElement)NBTTags.nbtStringList(Arrays.stream(EnumScriptType.doorScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        compound.put("Languages", (NbtElement)ScriptController.Instance.nbtLanguages());
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

