/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtHelper
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.BlockState
 *  net.minecraft.registry.RegistryEntryLookup
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.schematics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.Registries;
import noppes.npcs.schematics.Blueprint;

public class BlueprintUtil {
    public static NbtCompound writeBlueprintToNBT(Blueprint schem) {
        NbtCompound compound = new NbtCompound();
        compound.putByte("version", (byte)1);
        compound.putShort("size_x", schem.getSizeX());
        compound.putShort("size_y", schem.getSizeY());
        compound.putShort("size_z", schem.getSizeZ());
        BlockState[] palette = schem.getPallete();
        NbtList paletteTag = new NbtList();
        for (short i = 0; i < schem.getPalleteSize(); i = (short)(i + 1)) {
            paletteTag.add(NbtHelper.fromBlockState((BlockState)palette[i]));
        }
        compound.put("palette", (NbtElement)paletteTag);
        int[] blockInt = BlueprintUtil.convertBlocksToSaveData(schem.getStructure(), schem.getSizeX(), schem.getSizeY(), schem.getSizeZ());
        compound.putIntArray("blocks", blockInt);
        NbtList finishedTes = new NbtList();
        NbtCompound[] tes = schem.getTileEntities();
        for (int i = 0; i < tes.length; ++i) {
            finishedTes.add(tes[i]);
        }
        compound.put("tile_entities", (NbtElement)finishedTes);
        List<String> requiredMods = schem.getRequiredMods();
        NbtList modsList = new NbtList();
        for (int i = 0; i < requiredMods.size(); ++i) {
            modsList.add(NbtString.of((String)requiredMods.get(i)));
        }
        compound.put("required_mods", (NbtElement)modsList);
        String name = schem.getName();
        String[] architects = schem.getArchitects();
        if (name != null) {
            compound.putString("name", name);
        }
        if (architects != null) {
            NbtList architectsTag = new NbtList();
            for (String architect : architects) {
                architectsTag.add(NbtString.of((String)architect));
            }
            compound.put("architects", (NbtElement)architectsTag);
        }
        return compound;
    }

    public static Blueprint readBlueprintFromNBT(NbtCompound tag) {
        byte version = tag.getByte("version");
        if (version == 1) {
            short sizeX = tag.getShort("size_x");
            short sizeY = tag.getShort("size_y");
            short sizeZ = tag.getShort("size_z");
            ArrayList<String> requiredMods = new ArrayList<String>();
            NbtList modsList = tag.getList("required_mods", 8);
            int modListSize = modsList.size();
            for (int i = 0; i < modListSize; ++i) {
                requiredMods.add(((NbtString)modsList.get(i)).asString());
                if (FabricLoader.getInstance().isModLoaded((String)requiredMods.get(i))) continue;
                Logger.getGlobal().log(Level.WARNING, "Couldn't load Blueprint, the following mod is missing: " + (String)requiredMods.get(i));
                return null;
            }
            NbtList paletteTag = tag.getList("palette", 10);
            short paletteSize = (short)paletteTag.size();
            BlockState[] palette = new BlockState[paletteSize];
            for (int i = 0; i < palette.length; i = (int)((short)(i + 1))) {
                palette[i] = NbtHelper.toBlockState((RegistryEntryLookup)Registries.BLOCK.getReadOnlyWrapper(), (NbtCompound)paletteTag.getCompound(i));
            }
            short[][][] blocks = BlueprintUtil.convertSaveDataToBlocks(tag.getIntArray("blocks"), sizeX, sizeY, sizeZ);
            NbtList teTag = tag.getList("tile_entities", 10);
            NbtCompound[] tileEntities = new NbtCompound[teTag.size()];
            for (int i = 0; i < tileEntities.length; i = (int)((short)(i + 1))) {
                tileEntities[i] = teTag.getCompound(i);
            }
            Blueprint schem = new Blueprint(sizeX, sizeY, sizeZ, paletteSize, palette, blocks, tileEntities, requiredMods);
            if (tag.contains("name")) {
                schem.setName(tag.getString("name"));
            }
            if (tag.contains("architects")) {
                NbtList architectsTag = tag.getList("architects", 8);
                String[] architects = new String[architectsTag.size()];
                for (int i = 0; i < architectsTag.size(); ++i) {
                    architects[i] = architectsTag.getString(i);
                }
                schem.setArchitects(architects);
            }
            return schem;
        }
        return null;
    }

    private static int[] convertBlocksToSaveData(short[][][] multDimArray, short sizeX, short sizeY, short sizeZ) {
        short[] oneDimArray = new short[sizeX * sizeY * sizeZ];
        int j = 0;
        for (short y = 0; y < sizeY; y = (short)(y + 1)) {
            for (short z = 0; z < sizeZ; z = (short)(z + 1)) {
                for (short x = 0; x < sizeX; x = (short)(x + 1)) {
                    oneDimArray[j++] = multDimArray[y][z][x];
                }
            }
        }
        int[] ints = new int[(int)Math.ceil((float)oneDimArray.length / 2.0f)];
        int currentInt = 0;
        for (int i = 1; i < oneDimArray.length; i += 2) {
            currentInt = oneDimArray[i - 1];
            ints[(int)Math.ceil((double)((double)((float)i / 2.0f))) - 1] = currentInt = currentInt << 16 | oneDimArray[i];
            currentInt = 0;
        }
        if (oneDimArray.length % 2 == 1) {
            ints[ints.length - 1] = currentInt = oneDimArray[oneDimArray.length - 1] << 16;
        }
        return ints;
    }

    public static short[][][] convertSaveDataToBlocks(int[] ints, short sizeX, short sizeY, short sizeZ) {
        short[] oneDimArray = new short[ints.length * 2];
        for (int i = 0; i < ints.length; ++i) {
            oneDimArray[i * 2] = (short)(ints[i] >> 16);
            oneDimArray[i * 2 + 1] = (short)ints[i];
        }
        short[][][] multDimArray = new short[sizeY][sizeZ][sizeX];
        int i = 0;
        for (short y = 0; y < sizeY; y = (short)(y + 1)) {
            for (short z = 0; z < sizeZ; z = (short)(z + 1)) {
                for (short x = 0; x < sizeX; x = (short)(x + 1)) {
                    multDimArray[y][z][x] = oneDimArray[i++];
                }
            }
        }
        return multDimArray;
    }
}

