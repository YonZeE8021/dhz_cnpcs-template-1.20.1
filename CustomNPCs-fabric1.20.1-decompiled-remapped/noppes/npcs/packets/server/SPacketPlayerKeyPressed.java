/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerKeyPressed
extends PacketServerBasic {
    private final int button;
    private final boolean ctrlDown;
    private final boolean shiftDown;
    private final boolean altDown;
    private final boolean metaDown;
    private final boolean released;
    private final String openGui;

    public SPacketPlayerKeyPressed(int button, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean metaDown, boolean released, String openGui) {
        this.button = button;
        this.ctrlDown = ctrlDown;
        this.shiftDown = shiftDown;
        this.altDown = altDown;
        this.metaDown = metaDown;
        this.released = released;
        this.openGui = openGui;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerKeyPressed msg, PacketByteBuf buf) {
        buf.writeInt(msg.button);
        buf.writeBoolean(msg.ctrlDown);
        buf.writeBoolean(msg.shiftDown);
        buf.writeBoolean(msg.altDown);
        buf.writeBoolean(msg.metaDown);
        buf.writeBoolean(msg.released);
        buf.writeString(msg.openGui == null ? "" : msg.openGui);
    }

    public static SPacketPlayerKeyPressed decode(PacketByteBuf buf) {
        return new SPacketPlayerKeyPressed(buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        if (!CustomNpcs.EnableScripting || ScriptController.Instance.languages.isEmpty()) {
            return;
        }
        EventHooks.onPlayerKeyEvent(this.player, this.button, this.ctrlDown, this.shiftDown, this.altDown, this.metaDown, this.released, this.openGui);
    }
}

