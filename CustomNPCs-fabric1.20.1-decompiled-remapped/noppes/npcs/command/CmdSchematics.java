/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.world.World
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.command.CommandSource
 *  net.minecraft.command.argument.BlockPosArgumentType
 *  net.minecraft.command.suggestion.SuggestionProviders
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.schematics.SchematicWrapper;

public class CmdSchematics {
    public static final List<String> names = new ArrayList<String>();
    public static final SuggestionProvider<ServerCommandSource> SCHEMAS = SuggestionProviders.register((Identifier)new Identifier("schemas"), (context, builder) -> CommandSource.suggestMatching(names.stream(), (SuggestionsBuilder)builder));
    public static final SuggestionProvider<ServerCommandSource> ROTATION = SuggestionProviders.register((Identifier)new Identifier("rotation"), (context, builder) -> CommandSource.suggestMatching((String[])new String[]{"0", "90", "180", "270"}, (SuggestionsBuilder)builder));

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"schema").requires(source -> source.hasPermissionLevel(4))).then(CommandManager.literal((String)"build").then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.word()).suggests(SCHEMAS).then(CommandManager.argument((String)"pos", (ArgumentType)BlockPosArgumentType.blockPos()).then(CommandManager.argument((String)"rotation", (ArgumentType)StringArgumentType.word()).suggests(ROTATION).executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"name");
            BlockPos pos = BlockPosArgumentType.getLoadedBlockPos((CommandContext)context, (String)"pos");
            int rotation = Integer.parseInt(StringArgumentType.getString((CommandContext)context, (String)"rotation"));
            SchematicWrapper schem = SchematicController.Instance.load(name);
            schem.init(pos, (World)((ServerCommandSource)context.getSource()).getWorld(), rotation);
            SchematicController.Instance.build(schem, (ServerCommandSource)context.getSource());
            return 1;
        })))))).then(CommandManager.literal((String)"stop").executes(context -> {
            SchematicController.Instance.stop((ServerCommandSource)context.getSource());
            return 1;
        }))).then(CommandManager.literal((String)"info").executes(context -> {
            SchematicController.Instance.info((ServerCommandSource)context.getSource());
            return 1;
        }))).then(CommandManager.literal((String)"list").executes(context -> {
            List<String> list = SchematicController.Instance.list();
            if (list.isEmpty()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.translatable((String)"No schemas available"), false);
                return 1;
            }
            Object s = "";
            for (String file : list) {
                s = (String)s + file + ", ";
            }
            String finalS = s;
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.translatable((String)finalS), false);
            return 1;
        }));
        return command;
    }
}

