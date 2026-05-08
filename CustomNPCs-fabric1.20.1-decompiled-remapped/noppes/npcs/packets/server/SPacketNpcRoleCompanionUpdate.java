/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketNpcRoleCompanionUpdate
extends PacketServerBasic {
    private EnumCompanionStage stage;

    public SPacketNpcRoleCompanionUpdate(EnumCompanionStage stage) {
        this.stage = stage;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcRoleCompanionUpdate msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.stage);
    }

    public static SPacketNpcRoleCompanionUpdate decode(PacketByteBuf buf) {
        return new SPacketNpcRoleCompanionUpdate((EnumCompanionStage)buf.readEnumConstant(EnumCompanionStage.class));
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 6) {
            return;
        }
        ((RoleCompanion)this.npc.role).matureTo(this.stage);
        this.npc.updateClient = true;
    }
}

