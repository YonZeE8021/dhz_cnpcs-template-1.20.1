/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSync
extends PacketBasic {
    private final int type;
    private final NbtCompound data;
    private final boolean syncEnd;

    public PacketSync(int type, NbtCompound data, boolean syncEnd) {
        this.type = type;
        this.data = data;
        this.syncEnd = syncEnd;
    }

    public static void encode(PacketSync msg, PacketByteBuf buf) {
        buf.writeInt(msg.type);
        buf.writeNbt(msg.data);
        buf.writeBoolean(msg.syncEnd);
    }

    public static PacketSync decode(PacketByteBuf buf) {
        return new PacketSync(buf.readInt(), buf.readNbt(), buf.readBoolean());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        if (this.type == 1) {
            NbtList list = this.data.getList("Data", 10);
            for (int i = 0; i < list.size(); ++i) {
                Faction faction = new Faction();
                faction.readNBT(list.getCompound(i));
                FactionController.instance.factionsSync.put(faction.id, faction);
            }
            if (this.syncEnd) {
                FactionController.instance.factions = FactionController.instance.factionsSync;
                FactionController.instance.factionsSync = new HashMap();
            }
        } else if (this.type == 3) {
            if (!this.data.isEmpty()) {
                QuestCategory category = new QuestCategory();
                category.readNBT(this.data);
                QuestController.instance.categoriesSync.put(category.id, category);
            }
            if (this.syncEnd) {
                HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
                for (QuestCategory category : QuestController.instance.categoriesSync.values()) {
                    for (Quest quest : category.quests.values()) {
                        quests.put(quest.id, quest);
                    }
                }
                QuestController.instance.categories = QuestController.instance.categoriesSync;
                QuestController.instance.quests = quests;
                QuestController.instance.categoriesSync = new HashMap();
            }
        } else if (this.type == 5) {
            if (!this.data.isEmpty()) {
                DialogCategory category = new DialogCategory();
                category.readNBT(this.data);
                DialogController.instance.categoriesSync.put(category.id, category);
            }
            if (this.syncEnd) {
                HashMap<Integer, Dialog> dialogs = new HashMap<Integer, Dialog>();
                for (DialogCategory category : DialogController.instance.categoriesSync.values()) {
                    for (Dialog dialog : category.dialogs.values()) {
                        dialogs.put(dialog.id, dialog);
                    }
                }
                DialogController.instance.categories = DialogController.instance.categoriesSync;
                DialogController.instance.dialogs = dialogs;
                DialogController.instance.categoriesSync = new HashMap();
            }
        } else if (this.type == 6) {
            NbtList list = this.data.getList("Data", 10);
            for (int i = 0; i < list.size(); ++i) {
                RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
                RecipeController.syncRecipes.put(recipe.getId(), recipe);
            }
            if (this.syncEnd) {
                RecipeController.instance.globalRecipes = RecipeController.syncRecipes;
                RecipeController.instance.reloadGlobalRecipes();
                RecipeController.syncRecipes = new HashMap();
            }
        } else if (this.type == 7) {
            NbtList list = this.data.getList("Data", 10);
            for (int i = 0; i < list.size(); ++i) {
                RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
                RecipeController.syncRecipes.put(recipe.getId(), recipe);
            }
            if (this.syncEnd) {
                RecipeController.instance.anvilRecipes = RecipeController.syncRecipes;
                RecipeController.syncRecipes = new HashMap();
            }
        } else if (this.type == 8) {
            ClientProxy.playerData.setNBT(this.data);
        }
    }

    public void clientSync(boolean syncEnd) {
    }
}

