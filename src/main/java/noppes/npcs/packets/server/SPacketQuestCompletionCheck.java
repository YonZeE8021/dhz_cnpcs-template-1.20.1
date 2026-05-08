/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundEvents
 */
package noppes.npcs.packets.server;

import java.util.ArrayList;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.EntityIMixin;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketQuestCompletionCheck
extends PacketServerBasic {
    private final int questId;

    public SPacketQuestCompletionCheck(int questId) {
        this.questId = questId;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketQuestCompletionCheck msg, PacketByteBuf buf) {
        buf.writeInt(msg.questId);
    }

    public static SPacketQuestCompletionCheck decode(PacketByteBuf buf) {
        return new SPacketQuestCompletionCheck(buf.readInt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get((PlayerEntity)this.player);
        PlayerQuestData playerdata = data.questData;
        QuestData questdata = playerdata.activeQuests.get(this.questId);
        if (questdata == null) {
            return;
        }
        Quest quest = questdata.quest;
        if (!quest.questInterface.isCompleted((PlayerEntity)this.player)) {
            return;
        }
        QuestEvent.QuestTurnedInEvent event = new QuestEvent.QuestTurnedInEvent(data.scriptData.getPlayer(), quest);
        event.expReward = quest.rewardExp;
        ArrayList<IItemStack> list = new ArrayList<IItemStack>();
        for (ItemStack item : quest.rewardItems.items) {
            if (item.isEmpty()) continue;
            list.add(NpcAPI.Instance().getIItemStack(item));
        }
        if (!quest.randomReward) {
            event.itemRewards = list.toArray(new IItemStack[list.size()]);
        } else if (!list.isEmpty()) {
            event.itemRewards = new IItemStack[]{(IItemStack)list.get(this.player.getRandom().nextInt(list.size()))};
        }
        EventHooks.onQuestTurnedIn(data.scriptData, event);
        for (IItemStack item : event.itemRewards) {
            if (item == null) continue;
            NoppesUtilServer.GivePlayerItem((Entity)this.player, (PlayerEntity)this.player, item.getMCItemStack());
        }
        quest.questInterface.handleComplete((PlayerEntity)this.player);
        if (event.expReward > 0) {
            NoppesUtilServer.playSound((LivingEntity)this.player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f * ((this.player.getWorld().random.nextFloat() - this.player.getWorld().random.nextFloat()) * 0.7f + 1.8f));
            this.player.addExperience(event.expReward);
        }
        quest.factionOptions.addPoints((PlayerEntity)this.player);
        if (quest.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(this.player.getServer(), this.player.getName().getString(), quest.mail);
        }
        if (!quest.command.isEmpty()) {
            FakePlayer cplayer = EntityNPCInterface.CommandPlayer;
            ((EntityIMixin)cplayer).setLevel((World)((ServerWorld)this.player.getWorld()));
            cplayer.setPosition(this.player.getX(), this.player.getY(), this.player.getZ());
            NoppesUtilServer.runCommand((Entity)cplayer, "QuestCompletion", quest.command, (PlayerEntity)this.player);
        }
        PlayerQuestController.setQuestFinished(quest, (PlayerEntity)this.player);
        if (quest.hasNewQuest()) {
            PlayerQuestController.addActiveQuest(quest.getNextQuest(), (PlayerEntity)this.player);
        }
    }
}

