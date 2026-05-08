/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketRecipeGet
extends PacketServerBasic {
    private Identifier recipe;

    public SPacketRecipeGet(Identifier recipe) {
        this.recipe = recipe;
    }

    public static void encode(SPacketRecipeGet msg, PacketByteBuf buf) {
        buf.writeIdentifier(msg.recipe);
    }

    public static SPacketRecipeGet decode(PacketByteBuf buf) {
        return new SPacketRecipeGet(buf.readIdentifier());
    }

    @Override
    protected void handle() {
        RecipeCarpentry r = RecipeController.instance.getRecipe(this.recipe);
        SPacketRecipeGet.setRecipeGui(this.player, r);
    }

    public static void setRecipeGui(ServerPlayerEntity player, RecipeCarpentry recipe) {
        if (recipe == null) {
            return;
        }
        if (!(player.currentScreenHandler instanceof ContainerManageRecipes)) {
            return;
        }
        ContainerManageRecipes container = (ContainerManageRecipes)player.currentScreenHandler;
        container.setRecipe(recipe, player.getWorld().getRegistryManager());
        Packets.send(player, new PacketGuiData(recipe.writeNBT()));
    }
}

