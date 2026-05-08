/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.block.entity.BlockEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.block.entity.BlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketScriptSave
extends PacketServerBasic {
    private int type;
    private NbtCompound data;

    public SPacketScriptSave(int type, NbtCompound data) {
        this.type = type;
        this.data = data;
    }

    public SPacketScriptSave(PacketByteBuf buf) {
        this.type = buf.readInt();
        this.data = buf.readNbt();
    }

    public static SPacketScriptSave decode(PacketByteBuf buf) {
        return new SPacketScriptSave(buf);
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.scripter || item.getItem() == CustomBlocks.scripted_door_item || item.getItem() == CustomItems.wand || item.getItem() == CustomItems.scripted_item || item.getItem() == CustomBlocks.scripted_item;
    }

    @Override
    public boolean requiresNpc() {
        return this.type == 0;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.TOOL_SCRIPTER;
    }

    @Override
    protected void handle() {
        TileNpcEntity script;
        BlockEntity tile;
        PlayerData pd;
        if (this.type == 0) {
            this.npc.script.load(this.data);
            this.npc.updateAI = true;
            this.npc.script.lastInited = -1L;
        }
        if (this.type == 1) {
            pd = PlayerData.get((PlayerEntity)this.player);
            tile = this.player.getWorld().getBlockEntity(pd.scriptBlockPos);
            if (!(tile instanceof TileScripted)) {
                return;
            }
            script = (TileScripted)tile;
            ((TileScripted)script).setNBT(this.data);
            ((TileScripted)script).lastInited = -1L;
            this.player.getWorld().markDirty(pd.scriptBlockPos);
        }
        if (this.type == 2) {
            if (!this.player.isCreative()) {
                return;
            }
            ItemScriptedWrapper wrapper = (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(this.player.getMainHandStack());
            wrapper.setMCNbt(this.data);
            wrapper.lastInited = -1L;
            wrapper.saveScriptData();
            wrapper.updateClient = true;
            this.player.currentScreenHandler.syncState();
        }
        if (this.type == 3) {
            ScriptController.Instance.setForgeScripts(this.data);
        }
        if (this.type == 4) {
            ScriptController.Instance.setPlayerScripts(this.data);
        }
        if (this.type == 5) {
            pd = PlayerData.get((PlayerEntity)this.player);
            tile = this.player.getWorld().getBlockEntity(pd.scriptBlockPos);
            if (!(tile instanceof TileScriptedDoor)) {
                return;
            }
            script = (TileScriptedDoor)tile;
            ((TileScriptedDoor)script).setNBT(this.data);
            ((TileScriptedDoor)script).lastInited = -1L;
        }
    }

    public static void encode(SPacketScriptSave msg, PacketByteBuf buf) {
        buf.writeInt(msg.type);
        buf.writeNbt(msg.data);
    }
}

