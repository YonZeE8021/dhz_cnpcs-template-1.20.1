/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.packets.client;

import java.util.Vector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollList
extends PacketBasic {
    private final Vector<String> data;

    public PacketGuiScrollList(Vector<String> data) {
        this.data = data;
    }

    public static void encode(PacketGuiScrollList msg, PacketByteBuf buf) {
        buf.writeInt(msg.data.size());
        for (String s : msg.data) {
            buf.writeString(s);
        }
    }

    public static PacketGuiScrollList decode(PacketByteBuf buf) {
        Vector<String> data = new Vector<String>();
        int size = buf.readInt();
        for (int i = 0; i < size; ++i) {
            data.add(buf.readString(Short.MAX_VALUE));
        }
        return new PacketGuiScrollList(data);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui instanceof GuiNPCInterface && ((GuiNPCInterface)gui).hasSubGui()) {
            gui = ((GuiNPCInterface)gui).getSubGui();
        }
        if (gui == null || !(gui instanceof IScrollData)) {
            return;
        }
        ((IScrollData)gui).setData(this.data, null);
    }
}

