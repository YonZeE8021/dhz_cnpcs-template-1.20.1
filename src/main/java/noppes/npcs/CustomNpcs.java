/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarting
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
 *  net.fabricmc.fabric.api.event.player.AttackBlockCallback
 *  net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
 *  net.fabricmc.fabric.api.event.player.UseBlockCallback
 *  net.fabricmc.fabric.api.event.player.UseEntityCallback
 *  net.fabricmc.fabric.api.event.player.UseItemCallback
 *  net.fabricmc.fabric.api.message.v1.ServerMessageEvents
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.entity.attribute.ClampedEntityAttribute
 *  net.minecraft.world.World
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager$RegistrationEnvironment
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.ScoreboardPlayerScore
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket
 *  net.minecraft.scoreboard.ServerScoreboard
 *  net.minecraft.scoreboard.ServerScoreboard$UpdateMode
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.util.WorldSavePath
 *  net.minecraft.command.CommandRegistryAccess
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.WorldSavePath;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import nikedemos.markovnames.generators.MarkovGenerator;
import noppes.npcs.CommonProxy;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomTabs;
import noppes.npcs.ScriptPlayerEventHandler;
import noppes.npcs.ServerEventsHandler;
import noppes.npcs.ServerTickHandler;
import noppes.npcs.SkinEventHandler;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.command.CmdNoppes;
import noppes.npcs.command.CmdSchematics;
import noppes.npcs.config.ConfigLoader;
import noppes.npcs.config.ConfigProp;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ScoreBoardMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.shared.common.util.LogWriter;

public class CustomNpcs
implements ModInitializer,
CommandRegistrationCallback,
ServerLifecycleEvents.ServerStarting,
ServerLifecycleEvents.ServerStarted {
    public static final String MODID = "customnpcs";
    public static final String VERSION = "1.20.1";
    @ConfigProp(info="Whether scripting is enabled or not")
    public static boolean EnableScripting = true;
    @ConfigProp(info="Arguments given to the Nashorn scripting library")
    public static String NashorArguments = "-strict";
    @ConfigProp(info="Disable Chat Bubbles")
    public static boolean EnableChatBubbles = true;
    @ConfigProp(info="Navigation search range for NPCs. Not recommended to increase if you have a slow pc or on a server")
    public static int NpcNavRange = 32;
    @ConfigProp(info="Limit too how many npcs can be in one chunk for natural spawning")
    public static int NpcNaturalSpawningChunkLimit = 4;
    @ConfigProp(info="Set to true if you want the dialog command option to be able to use op commands like tp etc")
    public static boolean NpcUseOpCommands = false;
    @ConfigProp(info="If set to true only opped people can use the /noppes command")
    public static boolean NoppesCommandOpOnly = false;
    @ConfigProp(info="Minimum Minecraft permission level (0-4) required for NPC management (GUI, wand, clone add/remove, etc.)")
    public static int NpcManagePermissionLevel = 3;
    @ConfigProp(info="Minimum Minecraft permission level (0-4) for general /noppes subcommands (root, faction, quest player ops, clone list/spawn, dialog read/show, config font, etc.)")
    public static int NoppesCommandPermissionLevel = 2;
    @ConfigProp(info="Minimum Minecraft permission level (0-4) for administrative /noppes actions (config toggles, slay, dialog/quest reload, schematics, and when NoppesCommandOpOnly applies to npc/script)")
    public static int NoppesAdminPermissionLevel = 4;
    @ConfigProp
    public static boolean InventoryGuiEnabled = true;
    public static boolean FixUpdateFromPre_1_12 = false;
    public static boolean DisablePermissions = false;
    @ConfigProp
    public static boolean SceneButtonsEnabled = true;
    public static long ticks;
    @ConfigProp(info="Enables CustomNpcs startup update message")
    public static boolean EnableUpdateChecker;
    public static CustomNpcs instance;
    public static boolean FreezeNPCs;
    public static boolean OpsOnly;
    @ConfigProp(info="Default interact line. Leave empty to not have one")
    public static String DefaultInteractLine;
    @ConfigProp(info="Number of chunk loading npcs that can be active at the same time")
    public static int ChuckLoaders;
    public static File Dir;
    @ConfigProp(info="Enables leaves decay")
    public static boolean LeavesDecayEnabled;
    @ConfigProp(info="Enables Vine Growth")
    public static boolean VineGrowthEnabled;
    @ConfigProp(info="Enables Ice Melting")
    public static boolean IceMeltsEnabled;
    @ConfigProp(info="Normal players can use soulstone on animals")
    public static boolean SoulStoneAnimals;
    @ConfigProp(info="Normal players can use soulstone on all npcs")
    public static boolean SoulStoneNPCs;
    @ConfigProp(info="Type 0 = Normal, Type 1 = Solid")
    public static int HeadWearType;
    @ConfigProp(info="When set to Minecraft it will use minecrafts font, when Default it will use OpenSans. Can only use fonts installed on your PC")
    public static String FontType;
    @ConfigProp(info="Font size for custom fonts (doesn't work with minecrafts font)")
    public static int FontSize;
    @ConfigProp(info="On some servers or with certain plugins, it doesnt work, so you can disable it here")
    public static boolean EnableInvisibleNpcs;
    @ConfigProp
    public static boolean NpcSpeachTriggersChatEvent;
    public static ConfigLoader Config;
    public static boolean VerboseDebug;
    public static MinecraftServer Server;
    public static CommonProxy proxy;

    public static int clampMcPermissionLevel(int value) {
        return Math.max(0, Math.min(4, value));
    }

    public static File getLevelSaveDirectory() {
        return CustomNpcs.getLevelSaveDirectory(null);
    }

    public static File getLevelSaveDirectory(String s) {
        try {
            File dir = new File(".");
            if (Server != null) {
                if (!Server.isDedicated()) {
                    dir = new File(MinecraftClient.getInstance().runDirectory, "saves");
                }
                dir = Server.getSavePath(new WorldSavePath(MODID)).toFile();
            }
            if (s != null) {
                dir = new File(dir, s);
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        }
        catch (Exception e) {
            LogWriter.error("Error getting worldsave", e);
            return null;
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
        try {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (IllegalAccessException e) {
            LogWriter.error("setPrivateValue error", e);
        }
    }

    public void onInitialize() {
        instance = this;
        File dir = new File(Paths.get("config", new String[0]).toFile(), "..");
        Config = new ConfigLoader(this.getClass(), new File(dir, "config"), "CustomNpcs");
        Config.loadConfig();
        if (NpcNavRange < 16) {
            NpcNavRange = 16;
        }
        NpcManagePermissionLevel = CustomNpcs.clampMcPermissionLevel(NpcManagePermissionLevel);
        NoppesCommandPermissionLevel = CustomNpcs.clampMcPermissionLevel(NoppesCommandPermissionLevel);
        NoppesAdminPermissionLevel = CustomNpcs.clampMcPermissionLevel(NoppesAdminPermissionLevel);
        CustomBlocks.registerBlocks();
        CustomItems.registerItems();
        CustomTabs.registerCreativeTab();
        CustomEntities.registerEntities();
        CustomEntities.attribute();
        CustomContainer.registerContainers();
        Packets.register();
        ServerLifecycleEvents.SERVER_STARTING.register(this);
        ServerLifecycleEvents.SERVER_STARTED.register(this);
        ServerTickEvents.END_SERVER_TICK.register(new SkinEventHandler());
        ServerPlayConnectionEvents.JOIN.register(new SkinEventHandler());
        UseEntityCallback.EVENT.register(new ServerEventsHandler());
        ServerLivingEntityEvents.AFTER_DEATH.register(new ServerEventsHandler());
        ServerTickEvents.START_SERVER_TICK.register(new ServerTickHandler());
        ServerPlayConnectionEvents.JOIN.register(new ServerTickHandler());
        CommandRegistrationCallback.EVENT.register(this);
        proxy.load();
        PixelmonHelper.load();
        ScriptController controller = new ScriptController();
        if (EnableScripting && controller.languages.size() > 0) {
            ServerTickEvents.START_SERVER_TICK.register(new ScriptPlayerEventHandler());
            AttackBlockCallback.EVENT.register(new ScriptPlayerEventHandler());
            UseBlockCallback.EVENT.register(new ScriptPlayerEventHandler());
            UseEntityCallback.EVENT.register(new ScriptPlayerEventHandler());
            UseItemCallback.EVENT.register(new ScriptPlayerEventHandler());
            PlayerBlockBreakEvents.BEFORE.register(new ScriptPlayerEventHandler());
            ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(new ScriptPlayerEventHandler());
            ServerLivingEntityEvents.ALLOW_DAMAGE.register(new ScriptPlayerEventHandler());
            ServerLivingEntityEvents.ALLOW_DEATH.register(new ScriptPlayerEventHandler());
            ServerPlayConnectionEvents.JOIN.register(new ScriptPlayerEventHandler());
            ServerPlayConnectionEvents.DISCONNECT.register(new ScriptPlayerEventHandler());
        }
        CustomNpcs.setPrivateValue(ClampedEntityAttribute.class, (ClampedEntityAttribute)EntityAttributes.GENERIC_MAX_HEALTH, Double.MAX_VALUE, 1);
        new RecipeController();
        proxy.postload();
        CustomItems.registerDispenser();
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        CmdNoppes.register(dispatcher);
    }

    public void onServerStarted(MinecraftServer server) {
        EntityNPCInterface.ChatEventPlayer = FakePlayer.get((ServerWorld)server.getWorld(World.OVERWORLD), (GameProfile)EntityNPCInterface.ChatEventProfile);
        EntityNPCInterface.CommandPlayer = FakePlayer.get((ServerWorld)server.getWorld(World.OVERWORLD), (GameProfile)EntityNPCInterface.CommandProfile);
        EntityNPCInterface.GenericPlayer = FakePlayer.get((ServerWorld)server.getWorld(World.OVERWORLD), (GameProfile)EntityNPCInterface.GenericProfile);
        for (ServerWorld level : Server.getWorlds()) {
            ServerScoreboard board = level.getScoreboard();
            board.addUpdateListener(() -> {
                for (String objective : Availability.scores) {
                    ScoreboardObjective so = board.getNullableObjective(objective);
                    if (so == null) continue;
                    for (ServerPlayerEntity player : Server.getPlayerManager().getPlayerList()) {
                        if (!board.playerHasObjective(player.getEntityName(), so) && board.getSlot(so) == 0) {
                            player.networkHandler.sendPacket((Packet)new ScoreboardObjectiveUpdateS2CPacket(so, 0));
                        }
                        ScoreBoardMixin mixin = (ScoreBoardMixin)board;
                        @SuppressWarnings("unchecked")
                        Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)mixin.getScores().computeIfAbsent(player.getEntityName(), p_197898_0_ -> Maps.newHashMap());
                        ScoreboardPlayerScore sco = map.computeIfAbsent(so, ob -> new ScoreboardPlayerScore((Scoreboard)board, ob, player.getEntityName()));
                        player.networkHandler.sendPacket((Packet)new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, so.getName(), sco.getPlayerName(), sco.getScore()));
                    }
                }
            });
            board.addUpdateListener(() -> {
                List<ServerPlayerEntity> players = Server.getPlayerManager().getPlayerList();
                for (ServerPlayerEntity playerMP : players) {
                    VisibilityController.instance.onUpdate(playerMP);
                }
            });
        }
        RecipeController.instance.load();
        new BankController();
        ServerCloneController.Instance = new ServerCloneController();
        DialogController.instance.load();
        QuestController.instance.load();
        ScriptController.HasStart = true;
    }

    public void onServerStarting(MinecraftServer server) {
        Availability.scores.clear();
        Server = server;
        MarkovGenerator.load();
        ChunkController.instance.clear();
        FactionController.instance.load();
        new PlayerDataController();
        new TransportController();
        new GlobalDataController();
        new SpawnController();
        new LinkedNpcController();
        new MassBlockController();
        VisibilityController.instance = new VisibilityController();
        ScriptController.Instance.loadCategories();
        ScriptController.Instance.loadStoredData();
        ScriptController.Instance.loadPlayerScripts();
        ScriptController.Instance.loadForgeScripts();
        ScriptController.HasStart = false;
        WrapperNpcAPI.clearCache();
        CmdSchematics.names.clear();
        CmdSchematics.names.addAll(SchematicController.Instance.list());
    }

    static {
        EnableUpdateChecker = true;
        FreezeNPCs = false;
        OpsOnly = true;
        DefaultInteractLine = "Hello @p";
        ChuckLoaders = 20;
        LeavesDecayEnabled = true;
        VineGrowthEnabled = true;
        IceMeltsEnabled = true;
        SoulStoneAnimals = true;
        SoulStoneNPCs = false;
        HeadWearType = 1;
        FontType = "Default";
        FontSize = 18;
        EnableInvisibleNpcs = true;
        NpcSpeachTriggersChatEvent = false;
        VerboseDebug = false;
        proxy = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? new ClientProxy() : new CommonProxy();
        File dir = new File(Paths.get("config", new String[0]).toFile(), "..");
        Dir = new File(dir, MODID);
        if (!Dir.exists()) {
            Dir.mkdir();
        }
    }
}

