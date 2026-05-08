/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.toast.Toast
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import noppes.npcs.client.gui.GuiAchievement;
import noppes.npcs.shared.common.PacketBasic;

public class PacketAchievement
extends PacketBasic {
    private final Text title;
    private final Text message;
    private final int type;

    public PacketAchievement(Text title, Text message, int type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public static void encode(PacketAchievement msg, PacketByteBuf buf) {
        buf.writeText(msg.title);
        buf.writeText(msg.message);
        buf.writeInt(msg.type);
    }

    public static PacketAchievement decode(PacketByteBuf buf) {
        return new PacketAchievement(buf.readText(), buf.readText(), buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        MinecraftClient.getInstance().getToastManager().add((Toast)new GuiAchievement(this.title, this.message, this.type));
    }
}

