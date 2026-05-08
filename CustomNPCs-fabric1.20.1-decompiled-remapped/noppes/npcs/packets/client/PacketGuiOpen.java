/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.packets.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.common.util.LogWriter;

public class PacketGuiOpen
extends PacketBasic {
    private final EnumGuiType gui;
    private final BlockPos pos;

    public PacketGuiOpen(EnumGuiType gui, BlockPos pos) {
        this.gui = gui;
        this.pos = pos;
    }

    public static void encode(PacketGuiOpen msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.gui);
        buf.writeBlockPos(msg.pos);
    }

    public static PacketGuiOpen decode(PacketByteBuf buf) {
        return new PacketGuiOpen((EnumGuiType)buf.readEnumConstant(EnumGuiType.class), buf.readBlockPos());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        try {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeBlockPos(this.pos);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            minecraft.setScreen(ClientProxy.getGui(this.gui, NoppesUtil.getLastNpc(), buffer));
        }
        catch (Exception e) {
            LogWriter.error("Error in gui: " + String.valueOf((Object)this.gui), e);
        }
    }
}

