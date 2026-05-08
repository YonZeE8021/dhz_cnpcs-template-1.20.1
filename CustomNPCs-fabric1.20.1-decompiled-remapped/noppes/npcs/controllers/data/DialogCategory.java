/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.controllers.data.Dialog;

public class DialogCategory
implements IDialogCategory {
    public int id = -1;
    public String title = "";
    public HashMap<Integer, Dialog> dialogs = new HashMap();

    public void readNBT(NbtCompound compound) {
        this.id = compound.getInt("Slot");
        this.title = compound.getString("Title");
        NbtList dialogsList = compound.getList("Dialogs", 10);
        if (dialogsList != null) {
            for (int ii = 0; ii < dialogsList.size(); ++ii) {
                Dialog dialog = new Dialog(this);
                NbtCompound comp = dialogsList.getCompound(ii);
                dialog.readNBT(comp);
                dialog.id = comp.getInt("DialogId");
                this.dialogs.put(dialog.id, dialog);
            }
        }
    }

    public NbtCompound writeNBT(NbtCompound compound) {
        compound.putInt("Slot", this.id);
        compound.putString("Title", this.title);
        NbtList dialogs = new NbtList();
        for (Dialog dialog : this.dialogs.values()) {
            dialogs.add((Object)dialog.save(new NbtCompound()));
        }
        compound.put("Dialogs", (NbtElement)dialogs);
        return compound;
    }

    @Override
    public List<IDialog> dialogs() {
        return new ArrayList<IDialog>(this.dialogs.values());
    }

    @Override
    public String getName() {
        return this.title;
    }

    @Override
    public IDialog create() {
        return new Dialog(this);
    }
}

