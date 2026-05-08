/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.packets.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeRemove
extends PacketBasic {
    private final Identifier id;
    private final int type;

    public PacketSyncRecipeRemove(Identifier id, int type) {
        this.id = id;
        this.type = type;
    }

    public static void encode(PacketSyncRecipeRemove msg, PacketByteBuf buf) {
        buf.writeIdentifier(msg.id);
        buf.writeInt(msg.type);
    }

    public static PacketSyncRecipeRemove decode(PacketByteBuf buf) {
        return new PacketSyncRecipeRemove(buf.readIdentifier(), buf.readInt());
    }

    @Override
    protected void handle() {
        if (this.type == 6) {
            RecipeController.instance.globalRecipes.remove(this.id);
            RecipeController.instance.reloadGlobalRecipes();
        } else if (this.type == 7) {
            RecipeController.instance.anvilRecipes.remove(this.id);
        }
    }

    public void clientSync(boolean syncEnd) {
    }
}

