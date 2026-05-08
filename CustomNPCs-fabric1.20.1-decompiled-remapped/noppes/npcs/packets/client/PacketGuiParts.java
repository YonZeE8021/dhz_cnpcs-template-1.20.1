/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtTagSizeTracker
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.custom.GuiCreationNewParts;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiParts
extends PacketBasic {
    private final int id;
    private final NbtCompound data;

    public PacketGuiParts(int id, NbtCompound data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketGuiParts msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketGuiParts decode(PacketByteBuf buf) {
        return new PacketGuiParts(buf.readInt(), buf.readNbt(new NbtTagSizeTracker(Long.MAX_VALUE)));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = this.player.getWorld().getEntityById(this.id);
        Screen class_4372 = MinecraftClient.getInstance().currentScreen;
        if (class_4372 instanceof GuiCustom) {
            GuiCustom gui = (GuiCustom)class_4372;
            if (entity instanceof EntityCustomNpc) {
                EntityCustomNpc npc = (EntityCustomNpc)entity;
                GuiCreationNewParts parts = new GuiCreationNewParts(gui, npc);
                gui.initCallback = () -> {
                    gui.add(parts);
                    parts.init();
                };
                gui.setGuiData(this.data);
            }
        }
    }
}

