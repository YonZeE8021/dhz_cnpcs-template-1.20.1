/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.registry.Registries;

public class CobblemonHelper {
    public static boolean Enabled = FabricLoader.getInstance().isModLoaded("cobblemon");

    public static boolean isPokemon(Entity entity) {
        if (entity == null) {
            return false;
        }
        Identifier typeResLoc = Registries.ENTITY_TYPE.getId((Object)entity.getType());
        return typeResLoc.equals((Object)new Identifier("cobblemon", "pokemon"));
    }

    public static Identifier getType(Entity entity) {
        if (!CobblemonHelper.isPokemon(entity)) {
            return null;
        }
        try {
            Object pokemon = entity.getClass().getMethod("getPokemon", new Class[0]).invoke((Object)entity, new Object[0]);
            Object species = pokemon.getClass().getMethod("getSpecies", new Class[0]).invoke(pokemon, new Object[0]);
            return (Identifier)species.getClass().getField("resourceIdentifier").get(species);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static void setType(Entity entity, Identifier resourceLocation) {
        if (!CobblemonHelper.isPokemon(entity)) {
            return;
        }
        try {
            Object pokemon = entity.getClass().getMethod("getPokemon", new Class[0]).invoke((Object)entity, new Object[0]);
            Object species = pokemon.getClass().getMethod("getSpecies", new Class[0]).invoke(pokemon, new Object[0]);
            species.getClass().getField("resourceIdentifier").set(species, resourceLocation);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static EntityModel getPokemonModel(Entity entity) {
        Identifier species = CobblemonHelper.getType(entity);
        EntityModel model = null;
        try {
            Object instance = Class.forName("com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository").getField("INSTANCE").get(null);
            model = (EntityModel)instance.getClass().getMethod("getPoser", Identifier.class, Set.class).invoke(instance, species, new HashSet());
            model.getClass().getMethod("setProfileScale", Float.TYPE).invoke((Object)model, Float.valueOf(1.0f));
            model.getClass().getMethod("setPortraitScale", Float.TYPE).invoke((Object)model, Float.valueOf(1.0f));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return model;
    }

    public static List<String> getTypes() {
        HashSet<String> res = new HashSet<String>();
        try {
            Object instance = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies").getField("INSTANCE").get(null);
            List implementedSpecies = (List)instance.getClass().getMethod("getImplemented", new Class[0]).invoke(instance, new Object[0]);
            for (Object obj : implementedSpecies) {
                res.add(obj.getClass().getMethod("getResourceIdentifier", new Class[0]).invoke(obj, new Object[0]).toString());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return new ArrayList<String>(res);
    }
}

