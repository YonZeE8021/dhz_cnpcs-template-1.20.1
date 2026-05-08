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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollData
extends PacketBasic {
    private final Map<String, Integer> data;

    public PacketGuiScrollData(Map<String, Integer> data) {
        this.data = data;
    }

    public static void encode(PacketGuiScrollData msg, PacketByteBuf buf) {
        buf.writeInt(msg.data.size());
        for (Map.Entry<String, Integer> e : msg.data.entrySet()) {
            buf.writeString(e.getKey());
            buf.writeInt(e.getValue().intValue());
        }
    }

    public static PacketGuiScrollData decode(PacketByteBuf buf) {
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        int size = buf.readInt();
        for (int i = 0; i < size; ++i) {
            data.put(buf.readString(Short.MAX_VALUE), buf.readInt());
        }
        return new PacketGuiScrollData(data);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null) {
            return;
        }
        if (gui instanceof GuiNPCInterface && ((GuiNPCInterface)gui).hasSubGui()) {
            gui = ((GuiNPCInterface)gui).getSubGui();
        }
        if (gui instanceof GuiContainerNPCInterface && ((GuiContainerNPCInterface)gui).hasSubGui()) {
            gui = ((GuiContainerNPCInterface)gui).getSubGui();
        }
        if (gui instanceof IScrollData) {
            ((IScrollData)gui).setData(new Vector<String>(this.data.keySet()), this.data);
        }
    }
}

