/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.command.CommandSource
 *  net.minecraft.command.argument.EntityArgumentType
 *  net.minecraft.command.argument.BlockPosArgumentType
 *  net.minecraft.command.suggestion.SuggestionProviders
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.command.CmdNoppes;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

public class CmdNPC {
    public static final SuggestionProvider<ServerCommandSource> VISIBLE = SuggestionProviders.register((Identifier)new Identifier("visible"), (context, builder) -> CommandSource.suggestMatching((String[])new String[]{"true", "false", "semi"}, (SuggestionsBuilder)builder));

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"npc").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandOpOnly ? CustomNpcs.NoppesAdminPermissionLevel : CustomNpcs.NoppesCommandPermissionLevel))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument((String)"npc", (ArgumentType)StringArgumentType.string()).then(CommandManager.literal((String)"home").then(CommandManager.argument((String)"pos", (ArgumentType)BlockPosArgumentType.blockPos()).executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
            if (!npcs.isEmpty()) {
                npcs.get((int)0).ais.setStartPos(BlockPosArgumentType.getLoadedBlockPos((CommandContext)context, (String)"pos"));
            }
            return 1;
        })))).then(CommandManager.literal((String)"visible").then(CommandManager.argument((String)"visibility", (ArgumentType)StringArgumentType.word()).suggests(VISIBLE).executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
            String val = StringArgumentType.getString((CommandContext)context, (String)"visibility");
            int vis = 0;
            if (val.equalsIgnoreCase("false")) {
                vis = 1;
            } else if (val.equalsIgnoreCase("semi")) {
                vis = 2;
            }
            for (EntityNPCInterface npc : npcs) {
                npc.display.setVisible(vis);
            }
            return 1;
        })))).then(CommandManager.literal((String)"delete").executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
            for (EntityNPCInterface npc : npcs) {
                npc.delete();
            }
            return 1;
        }))).then(((LiteralArgumentBuilder)CommandManager.literal((String)"owner").executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
            for (EntityNPCInterface npc : npcs) {
                LivingEntity owner = npc.getOwner();
                if (owner == null) {
                    ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"No owner"), false);
                    continue;
                }
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Owner is: " + String.valueOf(owner.getName()))), false);
            }
            return 1;
        })).then(CommandManager.argument((String)"player", (ArgumentType)EntityArgumentType.player()).executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
            ServerPlayerEntity player = EntityArgumentType.getPlayer((CommandContext)context, (String)"player");
            for (EntityNPCInterface npc : npcs) {
                if (npc.role instanceof RoleFollower) {
                    ((RoleFollower)npc.role).setOwner((PlayerEntity)player);
                }
                if (!(npc.role instanceof RoleCompanion)) continue;
                ((RoleCompanion)npc.role).setOwner((PlayerEntity)player);
            }
            return 1;
        })))).then(CommandManager.literal((String)"delete").then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(context -> {
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), StringArgumentType.getString((CommandContext)context, (String)"npc"));
            String name = StringArgumentType.getString((CommandContext)context, (String)"name");
            for (EntityNPCInterface npc : npcs) {
                npc.display.setName(name);
                npc.updateClient = true;
            }
            return 1;
        })))).then(CommandManager.literal((String)"reset").executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            List<EntityNPCInterface> npcs = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
            for (EntityNPCInterface npc : npcs) {
                npc.reset();
            }
            return 1;
        }))).then(CommandManager.literal((String)"create").executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"npc");
            ServerWorld pw = ((ServerCommandSource)context.getSource()).getWorld();
            EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, (World)pw);
            npc.display.setName(name);
            Vec3d pos = ((ServerCommandSource)context.getSource()).getPosition();
            npc.updatePositionAndAngles(pos.x, pos.y, pos.z, 0.0f, 0.0f);
            npc.ais.setStartPos(new BlockPos((int)pos.x, (int)pos.y, (int)pos.z));
            pw.spawnEntity((Entity)npc);
            npc.setHealth(npc.getMaxHealth());
            return 1;
        })));
        return command;
    }
}

