/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogOption;

public class DialogEvent
extends NpcEvent {
    public final IDialog dialog;
    public final IPlayer player;

    public DialogEvent(ICustomNpc npc, PlayerEntity player, IDialog dialog) {
        super(npc);
        this.dialog = dialog;
        this.player = (IPlayer)NpcAPI.Instance().getIEntity((Entity)player);
    }

    public static class OptionEvent
    extends DialogEvent {
        public final IDialogOption option;

        public OptionEvent(ICustomNpc npc, PlayerEntity player, IDialog dialog, IDialogOption option) {
            super(npc, player, dialog);
            this.option = option;
        }
    }

    public static class CloseEvent
    extends DialogEvent {
        public CloseEvent(ICustomNpc npc, PlayerEntity player, IDialog dialog) {
            super(npc, player, dialog);
        }
    }

    public static class OpenEvent
    extends DialogEvent {
        public OpenEvent(ICustomNpc npc, PlayerEntity player, IDialog dialog) {
            super(npc, player, dialog);
        }
    }
}

