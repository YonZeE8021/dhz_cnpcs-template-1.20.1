/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketRecipeGet;
import noppes.npcs.packets.server.SPacketRecipesGet;

public class SPacketRecipeRemove
extends PacketServerBasic {
    private Identifier recipe;

    public SPacketRecipeRemove(Identifier recipe) {
        this.recipe = recipe;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_RECIPE;
    }

    public static void encode(SPacketRecipeRemove msg, PacketByteBuf buf) {
        buf.writeIdentifier(msg.recipe);
    }

    public static SPacketRecipeRemove decode(PacketByteBuf buf) {
        return new SPacketRecipeRemove(buf.readIdentifier());
    }

    @Override
    protected void handle() {
        RecipeCarpentry r = RecipeController.instance.delete(this.recipe);
        SPacketRecipesGet.sendRecipeData(this.player, r.isGlobal ? 3 : 4);
        SPacketRecipeGet.setRecipeGui(this.player, new RecipeCarpentry(new Identifier("customnpcs", ""), ""));
    }
}

