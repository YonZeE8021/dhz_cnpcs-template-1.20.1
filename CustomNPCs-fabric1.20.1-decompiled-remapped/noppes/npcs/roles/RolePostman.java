/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleInterface;

public class RolePostman
extends RoleInterface {
    public NpcMiscInventory inventory = new NpcMiscInventory(1);
    private List<PlayerEntity> recentlyChecked = new ArrayList<PlayerEntity>();
    private List<PlayerEntity> toCheck;

    public RolePostman(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public boolean aiShouldExecute() {
        if (this.npc.age % 20 != 0) {
            return false;
        }
        this.toCheck = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand(10.0, 10.0, 10.0));
        this.toCheck.removeAll(this.recentlyChecked);
        List listMax = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand(20.0, 20.0, 20.0));
        this.recentlyChecked.retainAll(listMax);
        this.recentlyChecked.addAll(this.toCheck);
        for (PlayerEntity player : this.toCheck) {
            if (!PlayerData.get((PlayerEntity)player).mailData.hasMail()) continue;
            this.npc.say(player, new Line("mailbox.gotmail"));
        }
        return false;
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.put("PostInv", (NbtElement)this.inventory.getToNBT());
        return nbttagcompound;
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.inventory.setFromNBT(nbttagcompound.getCompound("PostInv"));
    }

    @Override
    public void interact(PlayerEntity player) {
        NoppesUtilServer.openContainerGui((ServerPlayerEntity)player, EnumGuiType.PlayerMailman, buf -> {
            buf.writeBoolean(true);
            buf.writeBoolean(true);
        });
    }

    @Override
    public int getType() {
        return 5;
    }
}

