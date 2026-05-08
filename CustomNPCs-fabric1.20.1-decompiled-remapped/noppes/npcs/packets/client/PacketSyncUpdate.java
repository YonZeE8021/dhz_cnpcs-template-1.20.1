/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncUpdate
extends PacketBasic {
    private final int id;
    private final int type;
    private final NbtCompound data;

    public PacketSyncUpdate(int id, int type, NbtCompound data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public static void encode(PacketSyncUpdate msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.type);
        buf.writeNbt(msg.data);
    }

    public static PacketSyncUpdate decode(PacketByteBuf buf) {
        return new PacketSyncUpdate(buf.readInt(), buf.readInt(), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        if (this.type == 1) {
            Faction faction = new Faction();
            faction.readNBT(this.data);
            FactionController.instance.factions.put(faction.id, faction);
        } else if (this.type == 4) {
            DialogCategory category = DialogController.instance.categories.get(this.id);
            Dialog dialog = new Dialog(category);
            dialog.readNBT(this.data);
            DialogController.instance.dialogs.put(dialog.id, dialog);
            category.dialogs.put(dialog.id, dialog);
        } else if (this.type == 5) {
            DialogCategory category = new DialogCategory();
            category.readNBT(this.data);
            DialogController.instance.categories.put(category.id, category);
        } else if (this.type == 2) {
            QuestCategory category = QuestController.instance.categories.get(this.id);
            Quest quest = new Quest(category);
            quest.readNBT(this.data);
            QuestController.instance.quests.put(quest.id, quest);
            category.quests.put(quest.id, quest);
        } else if (this.type == 3) {
            QuestCategory category = new QuestCategory();
            category.readNBT(this.data);
            QuestController.instance.categories.put(category.id, category);
        }
    }

    public void clientSync(boolean syncEnd) {
    }
}

