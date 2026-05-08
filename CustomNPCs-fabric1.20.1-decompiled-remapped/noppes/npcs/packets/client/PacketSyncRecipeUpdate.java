/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.packets.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeUpdate
extends PacketBasic {
    private final Identifier id;
    private final int type;
    private final NbtCompound data;

    public PacketSyncRecipeUpdate(Identifier id, int type, NbtCompound data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public static void encode(PacketSyncRecipeUpdate msg, PacketByteBuf buf) {
        buf.writeIdentifier(msg.id);
        buf.writeInt(msg.type);
        buf.writeNbt(msg.data);
    }

    public static PacketSyncRecipeUpdate decode(PacketByteBuf buf) {
        return new PacketSyncRecipeUpdate(buf.readIdentifier(), buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        if (this.type == 6) {
            RecipeCarpentry recipe = RecipeCarpentry.load(this.data);
            RecipeController.instance.globalRecipes.put(recipe.getId(), recipe);
            RecipeController.instance.reloadGlobalRecipes();
        } else if (this.type == 7) {
            RecipeCarpentry recipe = RecipeCarpentry.load(this.data);
            RecipeController.instance.anvilRecipes.put(recipe.getId(), recipe);
        }
    }

    public void clientSync(boolean syncEnd) {
    }
}

