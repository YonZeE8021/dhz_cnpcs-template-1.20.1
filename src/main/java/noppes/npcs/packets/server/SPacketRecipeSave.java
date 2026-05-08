/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketRecipeGet;
import noppes.npcs.packets.server.SPacketRecipesGet;

public class SPacketRecipeSave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketRecipeSave(NbtCompound data) {
        this.data = data;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_RECIPE;
    }

    public static void encode(SPacketRecipeSave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketRecipeSave decode(PacketByteBuf buf) {
        return new SPacketRecipeSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        RecipeCarpentry recipe = RecipeCarpentry.load(this.data);
        RecipeController.instance.saveRecipe(recipe);
        SPacketRecipesGet.sendRecipeData(this.player, recipe.isGlobal ? 3 : 4);
        SPacketRecipeGet.setRecipeGui(this.player, recipe);
    }
}

