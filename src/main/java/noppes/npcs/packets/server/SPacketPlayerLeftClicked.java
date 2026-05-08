/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerLeftClicked
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerLeftClicked msg, PacketByteBuf buf) {
    }

    public static SPacketPlayerLeftClicked decode(PacketByteBuf buf) {
        return new SPacketPlayerLeftClicked();
    }

    @Override
    protected void handle() {
        if (!CustomNpcs.EnableScripting || ScriptController.Instance.languages.isEmpty()) {
            return;
        }
        ItemStack item = this.player.getMainHandStack();
        PlayerScriptData handler = PlayerData.get((PlayerEntity)this.player).scriptData;
        PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 0, null);
        EventHooks.onPlayerAttack(handler, ev);
        if (item.getItem() == CustomItems.scripted_item) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(item);
            ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent((IItemScripted)isw, handler.getPlayer(), 0, null);
            EventHooks.onScriptItemAttack(isw, eve);
        }
    }
}

