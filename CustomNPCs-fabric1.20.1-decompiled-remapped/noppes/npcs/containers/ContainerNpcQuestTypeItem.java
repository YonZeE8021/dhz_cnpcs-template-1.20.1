/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.quests.QuestItem;

public class ContainerNpcQuestTypeItem
extends ScreenHandler {
    public ContainerNpcQuestTypeItem(int containerId, PlayerInventory playerInventory) {
        super(CustomContainer.container_questtypeitem, containerId);
        int i1;
        Quest quest = NoppesUtilServer.getEditingQuest(playerInventory.player);
        for (i1 = 0; i1 < 3; ++i1) {
            this.addSlot(new Slot((Inventory)((QuestItem)quest.questInterface).items, i1, 44, 39 + i1 * 25));
        }
        for (i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 113 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return null;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }
}

