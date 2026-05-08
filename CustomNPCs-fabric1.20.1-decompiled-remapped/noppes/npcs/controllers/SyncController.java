/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.controllers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;

public class SyncController {
    public static void syncPlayer(ServerPlayerEntity player) {
        NbtList list = new NbtList();
        for (Faction faction : FactionController.instance.factions.values()) {
            list.add((Object)faction.writeNBT(new NbtCompound()));
        }
        NbtCompound compound = new NbtCompound();
        compound.put("Data", (NbtElement)list);
        Packets.send(player, new PacketSync(1, compound, true));
        for (QuestCategory questCategory : QuestController.instance.categories.values()) {
            Packets.send(player, new PacketSync(3, questCategory.writeNBT(new NbtCompound()), false));
        }
        Packets.send(player, new PacketSync(3, new NbtCompound(), true));
        for (DialogCategory dialogCategory : DialogController.instance.categories.values()) {
            Packets.send(player, new PacketSync(5, dialogCategory.writeNBT(new NbtCompound()), false));
        }
        Packets.send(player, new PacketSync(5, new NbtCompound(), true));
        list = new NbtList();
        for (RecipeCarpentry recipeCarpentry : RecipeController.instance.globalRecipes.values()) {
            list.add((Object)recipeCarpentry.writeNBT());
            if (list.size() <= 10) continue;
            compound = new NbtCompound();
            compound.put("Data", (NbtElement)list);
            Packets.send(player, new PacketSync(6, compound, false));
            list = new NbtList();
        }
        compound = new NbtCompound();
        compound.put("Data", (NbtElement)list);
        Packets.send(player, new PacketSync(6, compound, true));
        list = new NbtList();
        for (RecipeCarpentry recipeCarpentry : RecipeController.instance.anvilRecipes.values()) {
            list.add((Object)recipeCarpentry.writeNBT());
            if (list.size() <= 10) continue;
            compound = new NbtCompound();
            compound.put("Data", (NbtElement)list);
            Packets.send(player, new PacketSync(7, compound, false));
            list = new NbtList();
        }
        compound = new NbtCompound();
        compound.put("Data", (NbtElement)list);
        Packets.send(player, new PacketSync(7, compound, true));
        PlayerData playerData = PlayerData.get((PlayerEntity)player);
        Packets.send(player, new PacketSync(8, playerData.getNBT(), true));
    }

    public static void syncAllDialogs() {
        for (DialogCategory category : DialogController.instance.categories.values()) {
            Packets.sendAll(new PacketSync(5, category.writeNBT(new NbtCompound()), false));
        }
        Packets.sendAll(new PacketSync(5, new NbtCompound(), true));
    }

    public static void syncAllQuests() {
        for (QuestCategory category : QuestController.instance.categories.values()) {
            Packets.sendAll(new PacketSync(3, category.writeNBT(new NbtCompound()), false));
        }
        Packets.sendAll(new PacketSync(3, new NbtCompound(), true));
    }
}

