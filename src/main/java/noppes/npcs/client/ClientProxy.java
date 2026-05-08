/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.ResourceTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.particle.Particle
 */
package noppes.npcs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.random.Random;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.particle.Particle;
import noppes.npcs.CommonProxy;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.gui.GuiBlockBuilder;
import noppes.npcs.client.gui.GuiBlockCopy;
import noppes.npcs.client.gui.GuiBorderBlock;
import noppes.npcs.client.gui.GuiNbtBook;
import noppes.npcs.client.gui.GuiNpcDimension;
import noppes.npcs.client.gui.GuiNpcMobSpawner;
import noppes.npcs.client.gui.GuiNpcMobSpawnerMounter;
import noppes.npcs.client.gui.GuiNpcPather;
import noppes.npcs.client.gui.GuiNpcRedstoneBlock;
import noppes.npcs.client.gui.GuiNpcRemoteEditor;
import noppes.npcs.client.gui.GuiNpcWaypoint;
import noppes.npcs.client.gui.global.GuiNPCManageDialogs;
import noppes.npcs.client.gui.global.GuiNPCManageFactions;
import noppes.npcs.client.gui.global.GuiNPCManageLinkedNpc;
import noppes.npcs.client.gui.global.GuiNPCManageQuest;
import noppes.npcs.client.gui.global.GuiNPCManageTransporters;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.mainmenu.GuiNpcAI;
import noppes.npcs.client.gui.mainmenu.GuiNpcAdvanced;
import noppes.npcs.client.gui.mainmenu.GuiNpcDisplay;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import noppes.npcs.client.gui.player.GuiMailbox;
import noppes.npcs.client.gui.player.GuiTransportSelection;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.roles.GuiNpcBankSetup;
import noppes.npcs.client.gui.roles.GuiNpcTransporter;
import noppes.npcs.client.gui.script.GuiScript;
import noppes.npcs.client.gui.script.GuiScriptBlock;
import noppes.npcs.client.gui.script.GuiScriptDoor;
import noppes.npcs.client.gui.script.GuiScriptGlobal;
import noppes.npcs.client.gui.script.GuiScriptItem;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ArmorLayerMixin;
import noppes.npcs.shared.client.util.TrueTypeFont;
import noppes.npcs.shared.common.util.LogWriter;

public class ClientProxy
extends CommonProxy {
    public static PlayerData playerData = new PlayerData();
    public static KeyBinding QuestLog;
    public static KeyBinding Scene1;
    public static KeyBinding SceneReset;
    public static KeyBinding Scene2;
    public static KeyBinding Scene3;
    public static FontContainer Font;
    public static ModelData data;
    public static PlayerEntityModel playerModel;
    public static ArmorLayerMixin armorLayer;

    @Override
    public void load() {
    }

    @Override
    public PlayerData getPlayerData(PlayerEntity player) {
        if (player.getUuid() == MinecraftClient.getInstance().player.getUuid()) {
            if (ClientProxy.playerData.player != player) {
                ClientProxy.playerData.player = player;
            }
            return playerData;
        }
        return null;
    }

    @Override
    public void postload() {
    }

    public static void createFolders() {
        File meta;
        File json;
        File check;
        File file = new File(CustomNpcs.Dir, "assets/customnpcs");
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!(check = new File(file, "sounds")).exists()) {
            check.mkdir();
        }
        if (!(json = new File(file, "sounds.json")).exists()) {
            try {
                json.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(json));
                writer.write("{\n\n}");
                writer.close();
            }
            catch (IOException writer) {
                // empty catch block
            }
        }
        if (!(meta = new File(CustomNpcs.Dir, "pack.mcmeta")).exists()) {
            try {
                meta.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(meta));
                writer.write("{\n    \"pack\": {\n        \"description\": \"customnpcs map resource pack\",\n        \"pack_format\": 6\n    }\n}");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!(check = new File(file, "textures")).exists()) {
            check.mkdir();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Screen getGui(EnumGuiType gui, EntityNPCInterface npc, PacketByteBuf buf) {
        try {
            if (gui == EnumGuiType.MainMenuDisplay) {
                if (npc != null) {
                    GuiNpcDisplay guiNpcDisplay = new GuiNpcDisplay(npc);
                    return guiNpcDisplay;
                }
                MinecraftClient.getInstance().player.sendMessage((Text)Text.literal((String)"Unable to find npc"));
            } else {
                if (gui == EnumGuiType.MainMenuStats) {
                    GuiNpcStats guiNpcStats = new GuiNpcStats(npc);
                    return guiNpcStats;
                }
                if (gui == EnumGuiType.MainMenuAdvanced) {
                    GuiNpcAdvanced guiNpcAdvanced = new GuiNpcAdvanced(npc);
                    return guiNpcAdvanced;
                }
                if (gui == EnumGuiType.MovingPath) {
                    GuiNpcPather guiNpcPather = new GuiNpcPather(npc);
                    return guiNpcPather;
                }
                if (gui == EnumGuiType.ManageFactions) {
                    GuiNPCManageFactions guiNPCManageFactions = new GuiNPCManageFactions(npc);
                    return guiNPCManageFactions;
                }
                if (gui == EnumGuiType.ManageLinked) {
                    GuiNPCManageLinkedNpc guiNPCManageLinkedNpc = new GuiNPCManageLinkedNpc(npc);
                    return guiNPCManageLinkedNpc;
                }
                if (gui == EnumGuiType.BuilderBlock) {
                    GuiBlockBuilder guiBlockBuilder = new GuiBlockBuilder(buf.readBlockPos());
                    return guiBlockBuilder;
                }
                if (gui == EnumGuiType.ManageTransport) {
                    GuiNPCManageTransporters guiNPCManageTransporters = new GuiNPCManageTransporters(npc);
                    return guiNPCManageTransporters;
                }
                if (gui == EnumGuiType.ManageDialogs) {
                    GuiNPCManageDialogs guiNPCManageDialogs = new GuiNPCManageDialogs(npc);
                    return guiNPCManageDialogs;
                }
                if (gui == EnumGuiType.ManageQuests) {
                    GuiNPCManageQuest guiNPCManageQuest = new GuiNPCManageQuest(npc);
                    return guiNPCManageQuest;
                }
                if (gui == EnumGuiType.Companion) {
                    GuiNpcCompanionStats guiNpcCompanionStats = new GuiNpcCompanionStats(npc);
                    return guiNpcCompanionStats;
                }
                if (gui == EnumGuiType.CompanionTalent) {
                    GuiNpcCompanionTalents guiNpcCompanionTalents = new GuiNpcCompanionTalents(npc);
                    return guiNpcCompanionTalents;
                }
                if (gui == EnumGuiType.MainMenuGlobal) {
                    GuiNPCGlobalMainMenu guiNPCGlobalMainMenu = new GuiNPCGlobalMainMenu(npc);
                    return guiNPCGlobalMainMenu;
                }
                if (gui == EnumGuiType.MainMenuAI) {
                    GuiNpcAI guiNpcAI = new GuiNpcAI(npc);
                    return guiNpcAI;
                }
                if (gui == EnumGuiType.PlayerTransporter) {
                    GuiTransportSelection guiTransportSelection = new GuiTransportSelection(npc);
                    return guiTransportSelection;
                }
                if (gui == EnumGuiType.Script) {
                    GuiScript guiScript = new GuiScript(npc);
                    return guiScript;
                }
                if (gui == EnumGuiType.ScriptBlock) {
                    GuiScriptBlock guiScriptBlock = new GuiScriptBlock(buf.readBlockPos());
                    return guiScriptBlock;
                }
                if (gui == EnumGuiType.ScriptItem) {
                    GuiScriptItem guiScriptItem = new GuiScriptItem((PlayerEntity)MinecraftClient.getInstance().player);
                    return guiScriptItem;
                }
                if (gui == EnumGuiType.ScriptDoor) {
                    GuiScriptDoor guiScriptDoor = new GuiScriptDoor(buf.readBlockPos());
                    return guiScriptDoor;
                }
                if (gui == EnumGuiType.ScriptPlayers) {
                    GuiScriptGlobal guiScriptGlobal = new GuiScriptGlobal();
                    return guiScriptGlobal;
                }
                if (gui == EnumGuiType.SetupTransporter) {
                    GuiNpcTransporter guiNpcTransporter = new GuiNpcTransporter(npc);
                    return guiNpcTransporter;
                }
                if (gui == EnumGuiType.SetupBank) {
                    GuiNpcBankSetup guiNpcBankSetup = new GuiNpcBankSetup(npc);
                    return guiNpcBankSetup;
                }
                if (gui == EnumGuiType.NpcRemote && MinecraftClient.getInstance().currentScreen == null) {
                    GuiNpcRemoteEditor guiNpcRemoteEditor = new GuiNpcRemoteEditor();
                    return guiNpcRemoteEditor;
                }
                if (gui == EnumGuiType.PlayerMailbox) {
                    GuiMailbox guiMailbox = new GuiMailbox();
                    return guiMailbox;
                }
                if (gui == EnumGuiType.NpcDimensions) {
                    GuiNpcDimension guiNpcDimension = new GuiNpcDimension();
                    return guiNpcDimension;
                }
                if (gui == EnumGuiType.Border) {
                    GuiBorderBlock guiBorderBlock = new GuiBorderBlock(buf.readBlockPos());
                    return guiBorderBlock;
                }
                if (gui == EnumGuiType.RedstoneBlock) {
                    GuiNpcRedstoneBlock guiNpcRedstoneBlock = new GuiNpcRedstoneBlock(buf.readBlockPos());
                    return guiNpcRedstoneBlock;
                }
                if (gui == EnumGuiType.MobSpawner) {
                    GuiNpcMobSpawner guiNpcMobSpawner = new GuiNpcMobSpawner(buf.readBlockPos());
                    return guiNpcMobSpawner;
                }
                if (gui == EnumGuiType.CopyBlock) {
                    GuiBlockCopy guiBlockCopy = new GuiBlockCopy(buf.readBlockPos());
                    return guiBlockCopy;
                }
                if (gui == EnumGuiType.MobSpawnerMounter) {
                    GuiNpcMobSpawnerMounter guiNpcMobSpawnerMounter = new GuiNpcMobSpawnerMounter();
                    return guiNpcMobSpawnerMounter;
                }
                if (gui == EnumGuiType.Waypoint) {
                    GuiNpcWaypoint guiNpcWaypoint = new GuiNpcWaypoint(buf.readBlockPos());
                    return guiNpcWaypoint;
                }
                if (gui == EnumGuiType.NbtBook) {
                    GuiNbtBook guiNbtBook = new GuiNbtBook(buf.readBlockPos());
                    return guiNbtBook;
                }
            }
            Screen class_4372 = null;
            return class_4372;
        }
        finally {
            if (buf != null) {
                buf.release();
            }
        }
    }

    @Override
    public void openGui(PlayerEntity player, EnumGuiType gui) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft.player != player) {
            return;
        }
        Screen screen = ClientProxy.getGui(gui, null, null);
        if (screen != null) {
            minecraft.setScreen(screen);
        }
    }

    @Override
    public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.setScreen(ClientProxy.getGui(gui, npc, null));
    }

    @Override
    public void openGui(PlayerEntity player, Object guiscreen) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (!player.getWorld().isClient || !(guiscreen instanceof Screen)) {
            return;
        }
        if (guiscreen != null) {
            minecraft.setScreen((Screen)guiscreen);
        }
    }

    @Override
    public void spawnParticle(LivingEntity player, String string, Object ... ob) {
        if (string.equals("Block")) {
            BlockPos pos = (BlockPos)ob[0];
            BlockState state = (BlockState)ob[1];
            MinecraftClient.getInstance().particleManager.addBlockBreakParticles(pos, state);
        } else if (string.equals("ModelData")) {
            ModelData data = (ModelData)ob[0];
            ModelPartData particles = (ModelPartData)ob[1];
            EntityCustomNpc npc = (EntityCustomNpc)player;
            MinecraftClient minecraft = MinecraftClient.getInstance();
            double height = npc.getHeightOffset() + (double)data.getBodyY();
            Random class_58192 = npc.getRandom();
        }
    }

    @Override
    public boolean hasClient() {
        return true;
    }

    @Override
    public PlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    public static void bind(Identifier location) {
        try {
            if (location == null) {
                return;
            }
            TextureManager manager = MinecraftClient.getInstance().getTextureManager();
            AbstractTexture ob = manager.getTexture(location);
            if (ob == null) {
                ob = new ResourceTexture(location);
                manager.registerTexture(location, ob);
            }
            RenderSystem.bindTexture((int)ob.getGlId());
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    @Override
    public void spawnParticle(ParticleEffect particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
        double zz;
        double yy;
        MinecraftClient mc = MinecraftClient.getInstance();
        double xx = mc.getCameraEntity().getX() - x;
        if (xx * xx + (yy = mc.getCameraEntity().getY() - y) * yy + (zz = mc.getCameraEntity().getZ() - z) * zz > 256.0) {
            return;
        }
        Particle fx = mc.particleManager.addParticle(particle, x, y, z, motionX, motionY, motionZ);
        if (fx == null) {
            return;
        }
        if (particle == ParticleTypes.FLAME) {
            fx.scale(1.0E-5f);
        } else if (particle == ParticleTypes.SMOKE) {
            fx.scale(1.0E-5f);
        }
    }

    public static class FontContainer {
        private TrueTypeFont textFont = null;
        public boolean useCustomFont = true;

        private FontContainer() {
        }

        public FontContainer(String fontType, int fontSize) {
            try {
                this.textFont = new TrueTypeFont(new Font(fontType, 0, fontSize), 1.0f);
                boolean bl = this.useCustomFont = !fontType.equalsIgnoreCase("minecraft");
                if (!this.useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default")) {
                    this.textFont = new TrueTypeFont(new Identifier("customnpcs", "opensans.ttf"), fontSize, 1.0f);
                }
            }
            catch (Throwable e) {
                LogWriter.except(e);
                this.useCustomFont = false;
            }
        }

        public int height(String text) {
            if (this.useCustomFont) {
                return this.textFont.height(text);
            }
            Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
            return 9;
        }

        public int width(String text) {
            if (this.useCustomFont) {
                return this.textFont.width(text);
            }
            return MinecraftClient.getInstance().textRenderer.getWidth(text);
        }

        public FontContainer copy() {
            FontContainer font = new FontContainer();
            font.textFont = this.textFont;
            font.useCustomFont = this.useCustomFont;
            return font;
        }

        public void draw(DrawContext graphics, String text, int x, int y, int color) {
            if (this.useCustomFont) {
                this.textFont.draw(graphics.getMatrices(), text, x, y, color);
            } else {
                graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, x, y, color);
            }
        }

        public String getName() {
            if (!this.useCustomFont) {
                return "Minecraft";
            }
            return this.textFont.getFontName();
        }

        public void clear() {
            if (this.textFont != null) {
                this.textFont.dispose();
            }
        }
    }
}

