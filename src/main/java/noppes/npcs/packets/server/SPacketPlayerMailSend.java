/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerMailSend
extends PacketServerBasic {
    private final NbtCompound data;
    private final String username;

    public SPacketPlayerMailSend(String username, NbtCompound data) {
        this.username = username;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerMailSend msg, PacketByteBuf buf) {
        buf.writeString(msg.username);
        buf.writeNbt(msg.data);
    }

    public static SPacketPlayerMailSend decode(PacketByteBuf buf) {
        return new SPacketPlayerMailSend(buf.readString(Short.MAX_VALUE), buf.readNbt());
    }

    @Override
    protected void handle() {
        String username = PlayerDataController.instance.hasPlayer(this.username);
        if (username.isEmpty()) {
            NoppesUtilServer.sendGuiError((PlayerEntity)this.player, 0);
            return;
        }
        PlayerMail mail = new PlayerMail();
        String s = this.player.getDisplayName().getString();
        if (!s.equals(this.player.getName().getString())) {
            s = s + "(" + this.player.getName().getString() + ")";
        }
        mail.readNBT(this.data);
        mail.sender = s;
        if (mail.subject.isEmpty()) {
            NoppesUtilServer.sendGuiError((PlayerEntity)this.player, 1);
            return;
        }
        mail.items = ((ContainerMail)this.player.currentScreenHandler).mail.items;
        NbtCompound comp = new NbtCompound();
        comp.putString("username", username);
        NoppesUtilServer.sendGuiClose(this.player, 1, comp);
        this.player.closeHandledScreen();
        EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((PlayerEntity)this.player);
        if (npc != null && EventHooks.onNPCRole(npc, new RoleEvent.MailmanEvent((PlayerEntity)this.player, npc.wrappedNPC, mail))) {
            return;
        }
        PlayerDataController.instance.addPlayerMessage(this.player.getServer(), username, mail);
    }
}

