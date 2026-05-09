/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnGroup
 *  net.minecraft.entity.passive.TameableEntity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.passive.HorseEntity
 *  net.minecraft.entity.boss.dragon.EnderDragonEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.mob.GhastEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.util.math.Box
 *  net.minecraft.text.Text
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.Box;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class CmdSlay {
    static Map<String, Class<?>> slayMap = new LinkedHashMap();

    private static Map<String, Class<?>> getSlay(World level) {
        if (!slayMap.isEmpty()) {
            return slayMap;
        }
        slayMap.put("all", LivingEntity.class);
        slayMap.put("mobs", HostileEntity.class);
        slayMap.put("animals", AnimalEntity.class);
        slayMap.put("items", ItemEntity.class);
        slayMap.put("xporbs", ExperienceOrbEntity.class);
        slayMap.put("npcs", EntityNPCInterface.class);
        for (EntityType ent : Registries.ENTITY_TYPE) {
            if (ent.getSpawnGroup() == SpawnGroup.MISC) continue;
            String name = ent.getTranslationKey();
            try {
                Entity e = ent.create(level);
                e.remove(Entity.RemovalReason.DISCARDED);
                Class<?> cls = e.getClass();
                if (EntityNPCInterface.class.isAssignableFrom(cls) || !LivingEntity.class.isAssignableFrom(cls)) continue;
                slayMap.put(name.toLowerCase(), cls);
            }
            catch (Throwable throwable) {}
        }
        slayMap.remove("monster");
        slayMap.remove("mob");
        return slayMap;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"slay").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesAdminPermissionLevel))).then(CommandManager.argument((String)"type", (ArgumentType)StringArgumentType.word()).then(CommandManager.argument((String)"range", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(context -> {
            ArrayList toDelete = new ArrayList();
            boolean deleteNPCs = false;
            String delete = StringArgumentType.getString((CommandContext)context, (String)"type");
            Class<?> cls = CmdSlay.getSlay((World)((ServerCommandSource)context.getSource()).getWorld()).get(delete);
            if (cls != null) {
                toDelete.add(cls);
            }
            if (delete.equals("mobs")) {
                toDelete.add(GhastEntity.class);
                toDelete.add(EnderDragonEntity.class);
            }
            if (delete.equals("npcs")) {
                deleteNPCs = true;
            }
            int count = 0;
            int range = IntegerArgumentType.getInteger((CommandContext)context, (String)"range");
            Box box = new Box(((ServerCommandSource)context.getSource()).getPosition(), ((ServerCommandSource)context.getSource()).getPosition().add(1.0, 1.0, 1.0)).expand((double)range, (double)range, (double)range);
            List<LivingEntity> list = ((ServerCommandSource)context.getSource()).getWorld().getNonSpectatingEntities(LivingEntity.class, box);
            for (Entity entity : list) {
                if (entity instanceof PlayerEntity || entity instanceof TameableEntity && ((TameableEntity)entity).isTamed() || entity instanceof EntityNPCInterface && !deleteNPCs || !CmdSlay.delete(entity, toDelete)) continue;
                ++count;
            }
            if (toDelete.contains(ExperienceOrbEntity.class)) {
                List<ExperienceOrbEntity> orbList = ((ServerCommandSource)context.getSource()).getWorld().getNonSpectatingEntities(ExperienceOrbEntity.class, box);
                for (Entity entity : orbList) {
                    entity.setRemoved(Entity.RemovalReason.DISCARDED);
                    ++count;
                }
            }
            if (toDelete.contains(ItemEntity.class)) {
                List<ItemEntity> itemList = ((ServerCommandSource)context.getSource()).getWorld().getNonSpectatingEntities(ItemEntity.class, box);
                for (Entity entity : itemList) {
                    entity.setRemoved(Entity.RemovalReason.DISCARDED);
                    ++count;
                }
            }
            int finalCount = count;
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.translatable((String)(finalCount + " entities deleted")), false);
            return 1;
        })));
        return command;
    }

    private static boolean delete(Entity entity, ArrayList<Class<?>> toDelete) {
        for (Class<?> delete : toDelete) {
            if (delete == AnimalEntity.class && entity instanceof HorseEntity || !delete.isAssignableFrom(entity.getClass())) continue;
            entity.setRemoved(Entity.RemovalReason.DISCARDED);
            return true;
        }
        return false;
    }
}

