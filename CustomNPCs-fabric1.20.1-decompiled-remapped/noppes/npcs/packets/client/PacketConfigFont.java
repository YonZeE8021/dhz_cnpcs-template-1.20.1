/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.common.PacketBasic;

public class PacketConfigFont
extends PacketBasic {
    private final String font;
    private final int size;

    public PacketConfigFont(String font, int size) {
        this.font = font;
        this.size = size;
    }

    public static void encode(PacketConfigFont msg, PacketByteBuf buf) {
        buf.writeString(msg.font);
        buf.writeInt(msg.size);
    }

    public static PacketConfigFont decode(PacketByteBuf buf) {
        return new PacketConfigFont(buf.readString(Short.MAX_VALUE), buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Runnable run = () -> {
            if (!this.font.isEmpty()) {
                CustomNpcs.FontType = this.font;
                CustomNpcs.FontSize = this.size;
                ClientProxy.Font.clear();
                ClientProxy.Font = new ClientProxy.FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
                CustomNpcs.Config.updateConfig();
                this.player.sendMessage((Text)Text.translatable((String)"Font set to %s", (Object[])new Object[]{ClientProxy.Font.getName()}));
            } else {
                this.player.sendMessage((Text)Text.translatable((String)("Current font is " + ClientProxy.Font.getName())));
            }
        };
        MinecraftClient.getInstance().submit(run);
    }
}

