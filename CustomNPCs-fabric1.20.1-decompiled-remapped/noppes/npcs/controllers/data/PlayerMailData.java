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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.data.PlayerMail;

public class PlayerMailData {
    public ArrayList<PlayerMail> playermail = new ArrayList();

    public void loadNBTData(NbtCompound compound) {
        ArrayList<PlayerMail> newmail = new ArrayList<PlayerMail>();
        NbtList list = compound.getList("MailData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            PlayerMail mail = new PlayerMail();
            mail.readNBT(list.getCompound(i));
            newmail.add(mail);
        }
        this.playermail = newmail;
    }

    public NbtCompound saveNBTData(NbtCompound compound) {
        NbtList list = new NbtList();
        for (PlayerMail mail : this.playermail) {
            list.add((Object)mail.writeNBT());
        }
        compound.put("MailData", (NbtElement)list);
        return compound;
    }

    public boolean hasMail() {
        for (PlayerMail mail : this.playermail) {
            if (mail.beenRead) continue;
            return true;
        }
        return false;
    }
}

