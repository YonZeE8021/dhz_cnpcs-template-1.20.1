/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.client.ChatMessages;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.shared.common.PacketBasic;

public class PacketChatBubble
extends PacketBasic {
    private final int id;
    private final Text message;
    private final boolean showMessage;

    public PacketChatBubble(int id, Text message, boolean showMessage) {
        this.id = id;
        this.message = message;
        this.showMessage = showMessage;
    }

    public static void encode(PacketChatBubble msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeText(msg.message);
        buf.writeBoolean(msg.showMessage);
    }

    public static PacketChatBubble decode(PacketByteBuf buf) {
        return new PacketChatBubble(buf.readInt(), buf.readText(), buf.readBoolean());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.id);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        EntityNPCInterface npc = (EntityNPCInterface)entity;
        if (npc.messages == null) {
            npc.messages = new ChatMessages();
        }
        String text = NoppesStringUtils.formatText(this.message, new Object[]{this.player, npc});
        npc.messages.addMessage(text, npc);
        if (this.showMessage) {
            this.player.sendMessage((Text)Text.literal((String)(npc.getName().getString() + ": ")).append((Text)Text.translatable((String)text)));
        }
    }
}

