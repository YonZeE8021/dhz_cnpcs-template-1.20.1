/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtInt
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs;

import java.util.List;
import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.ICompatibilty;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;

public class VersionCompatibility {
    public static int ModRev = 18;

    public static void CheckNpcCompatibility(EntityNPCInterface npc, NbtCompound compound) {
        NbtList list;
        if (npc.npcVersion == ModRev) {
            return;
        }
        if (npc.npcVersion < 12) {
            VersionCompatibility.CompatabilityFix(compound, npc.advanced.save(new NbtCompound()));
            VersionCompatibility.CompatabilityFix(compound, npc.ais.save(new NbtCompound()));
            VersionCompatibility.CompatabilityFix(compound, npc.stats.save(new NbtCompound()));
            VersionCompatibility.CompatabilityFix(compound, npc.display.save(new NbtCompound()));
            VersionCompatibility.CompatabilityFix(compound, npc.inventory.save(new NbtCompound()));
        }
        if (npc.npcVersion < 5) {
            String texture = compound.getString("Texture");
            texture = texture.replace("/mob/customnpcs/", "customnpcs:textures/entity/");
            texture = texture.replace("/mob/", "customnpcs:textures/entity/");
            compound.putString("Texture", texture);
        }
        if (npc.npcVersion < 6 && compound.get("NpcInteractLines") instanceof NbtList) {
            List<String> interactLines = NBTTags.getStringList(compound.getList("NpcInteractLines", 10));
            Lines lines = new Lines();
            for (int i = 0; i < interactLines.size(); ++i) {
                Line line = new Line();
                line.setText((String)interactLines.toArray()[i]);
                lines.lines.put(i, line);
            }
            compound.put("NpcInteractLines", (NbtElement)lines.save());
            List<String> worldLines = NBTTags.getStringList(compound.getList("NpcLines", 10));
            lines = new Lines();
            for (int i = 0; i < worldLines.size(); ++i) {
                Line line = new Line();
                line.setText((String)worldLines.toArray()[i]);
                lines.lines.put(i, line);
            }
            compound.put("NpcLines", (NbtElement)lines.save());
            List<String> attackLines = NBTTags.getStringList(compound.getList("NpcAttackLines", 10));
            lines = new Lines();
            for (int i = 0; i < attackLines.size(); ++i) {
                Line line = new Line();
                line.setText((String)attackLines.toArray()[i]);
                lines.lines.put(i, line);
            }
            compound.put("NpcAttackLines", (NbtElement)lines.save());
            List<String> killedLines = NBTTags.getStringList(compound.getList("NpcKilledLines", 10));
            lines = new Lines();
            for (int i = 0; i < killedLines.size(); ++i) {
                Line line = new Line();
                line.setText((String)killedLines.toArray()[i]);
                lines.lines.put(i, line);
            }
            compound.put("NpcKilledLines", (NbtElement)lines.save());
        }
        if (npc.npcVersion == 12 && (list = compound.getList("StartPos", 3)).size() == 3) {
            int z = ((NbtInt)list.remove(2)).intValue();
            int y = ((NbtInt)list.remove(1)).intValue();
            int x = ((NbtInt)list.remove(0)).intValue();
            compound.putIntArray("StartPosNew", new int[]{x, y, z});
        }
        if (npc.npcVersion == 13) {
            boolean bo = compound.getBoolean("HealthRegen");
            compound.putInt("HealthRegen", bo ? 1 : 0);
            NbtCompound comp = compound.getCompound("TransformStats");
            bo = comp.getBoolean("HealthRegen");
            comp.putInt("HealthRegen", bo ? 1 : 0);
            compound.put("TransformStats", (NbtElement)comp);
        }
        if (npc.npcVersion == 15) {
            NbtList list2 = compound.getList("ScriptsContainers", 10);
            if (list2.size() > 0) {
                ScriptContainer script = new ScriptContainer(npc.script);
                for (int i = 0; i < list2.size(); ++i) {
                    NbtCompound scriptOld = list2.getCompound(i);
                    EnumScriptType type = EnumScriptType.values()[scriptOld.getInt("Type")];
                    script.script = script.script + "\nfunction " + type.function + "(event) {\n" + scriptOld.getString("Script") + "\n}";
                    for (String s : NBTTags.getStringList(compound.getList("ScriptList", 10))) {
                        if (script.scripts.contains(s)) continue;
                        script.scripts.add(s);
                    }
                }
            }
            if (compound.getBoolean("CanDespawn")) {
                compound.putInt("SpawnCycle", 4);
            }
            if (compound.getInt("RangeAndMelee") <= 0) {
                compound.putInt("DistanceToMelee", 0);
            }
        }
        if (npc.npcVersion == 16) {
            compound.putString("HitSound", "random.bowhit");
            compound.putString("GroundSound", "random.break");
        }
        if (npc.npcVersion == 17) {
            if (compound.getString("NpcHurtSound").equals("minecraft:game.player.hurt")) {
                compound.putString("NpcHurtSound", "minecraft:entity.player.hurt");
            }
            if (compound.getString("NpcDeathSound").equals("minecraft:game.player.hurt")) {
                compound.putString("NpcDeathSound", "minecraft:entity.player.hurt");
            }
            if (compound.getString("FiringSound").equals("random.bow")) {
                compound.putString("FiringSound", "minecraft:entity.arrow.shoot");
            }
            if (compound.getString("HitSound").equals("random.bowhit")) {
                compound.putString("HitSound", "minecraft:entity.arrow.hit");
            }
            if (compound.getString("GroundSound").equals("random.break")) {
                compound.putString("GroundSound", "minecraft:block.stone.break");
            }
        }
        npc.npcVersion = ModRev;
    }

    public static void CheckAvailabilityCompatibility(ICompatibilty compatibilty, NbtCompound compound) {
        if (compatibilty.getVersion() == ModRev) {
            return;
        }
        VersionCompatibility.CompatabilityFix(compound, compatibilty.save(new NbtCompound()));
        compatibilty.setVersion(ModRev);
    }

    private static void CompatabilityFix(NbtCompound compound, NbtCompound check) {
        Set tags = check.getKeys();
        for (String name : tags) {
            NbtElement nbt = check.get(name);
            if (!compound.contains(name)) {
                compound.put(name, nbt);
                continue;
            }
            if (!(nbt instanceof NbtCompound) || !(compound.get(name) instanceof NbtCompound)) continue;
            VersionCompatibility.CompatabilityFix(compound.getCompound(name), (NbtCompound)nbt);
        }
    }
}

