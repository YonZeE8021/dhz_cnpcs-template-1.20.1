/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
 *  net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.entity.projectile.thrown.ThrownEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.GameRules
 *  net.minecraft.world.World
 *  net.minecraft.server.command.CommandOutput
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.block.BlockState
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerType
 *  net.minecraft.text.MutableText
 *  net.minecraft.registry.Registries
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import net.minecraft.world.Heightmap;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.MutableText;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketDialog;
import noppes.npcs.packets.client.PacketDialogDummy;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.packets.client.PacketGuiError;
import noppes.npcs.packets.client.PacketGuiScrollData;
import noppes.npcs.packets.client.PacketNpcEdit;
import noppes.npcs.packets.client.PacketParticle;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.shared.common.CommonUtil;
import noppes.npcs.shared.common.util.LogWriter;

public class NoppesUtilServer {
    private static HashMap<UUID, Quest> editingQuests = new HashMap();
    private static HashMap<UUID, Quest> editingQuestsClient = new HashMap();

    public static void setEditingNpc(PlayerEntity player, EntityNPCInterface npc) {
        PlayerData data = PlayerData.get(player);
        data.editingNpc = npc;
        if (npc != null) {
            Packets.send((ServerPlayerEntity)player, new PacketNpcEdit(npc.getId()));
        }
    }

    public static EntityNPCInterface getEditingNpc(PlayerEntity player) {
        PlayerData data = PlayerData.get(player);
        return data.editingNpc;
    }

    public static void setEditingQuest(PlayerEntity player, Quest quest) {
        if (player.getWorld().isClient) {
            editingQuestsClient.put(player.getUuid(), quest);
        } else {
            editingQuests.put(player.getUuid(), quest);
        }
    }

    public static Quest getEditingQuest(PlayerEntity player) {
        if (player.getWorld().isClient) {
            return editingQuestsClient.get(player.getUuid());
        }
        return editingQuests.get(player.getUuid());
    }

    public static void openDialog(PlayerEntity player, EntityNPCInterface npc, Dialog dia) {
        Dialog dialog = dia.copy(player);
        PlayerData playerdata = PlayerData.get(player);
        if (EventHooks.onNPCDialog(npc, player, dialog)) {
            playerdata.dialogId = -1;
            return;
        }
        playerdata.dialogId = dialog.id;
        if (npc instanceof EntityDialogNpc || dia.id < 0) {
            dialog.hideNPC = true;
            Packets.send((ServerPlayerEntity)player, new PacketDialogDummy(npc.getName().getString(), dialog.save(new NbtCompound())));
        } else {
            Packets.send((ServerPlayerEntity)player, new PacketDialog(npc.getId(), dialog.id));
        }
        dia.factionOptions.addPoints(player);
        if (dialog.hasQuest()) {
            PlayerQuestController.addActiveQuest(dialog.getQuest(), player);
        }
        if (!dialog.command.isEmpty()) {
            NoppesUtilServer.runCommand((Entity)npc, npc.getName().getString(), dialog.command, player);
        }
        if (dialog.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName().getString(), dialog.mail);
        }
        PlayerDialogData data = playerdata.dialogData;
        if (!data.dialogsRead.contains(dialog.id) && dialog.id >= 0) {
            data.dialogsRead.add(dialog.id);
            playerdata.updateClient = true;
        }
        NoppesUtilServer.setEditingNpc(player, npc);
        playerdata.questData.checkQuestCompletion(player, 1);
    }

    public static String runCommand(Entity executer, String name, String command, PlayerEntity player) {
        return NoppesUtilServer.runCommand(executer.getEntityWorld(), executer.getBlockPos(), name, command, player, executer);
    }

    public static String runCommand(final World level, BlockPos pos, String name, String command, PlayerEntity player, Entity executer) {
        if (!level.getServer().areCommandBlocksEnabled()) {
            CommonUtil.NotifyOPs(level.getServer(), "Cant run commands if CommandBlocks are disabled", new Object[0]);
            LogWriter.warn("Cant run commands if CommandBlocks are disabled");
            return "Cant run commands if CommandBlocks are disabled";
        }
        if (player != null) {
            command = command.replace("@dp", player.getName().getString());
        }
        command = command.replace("@npc", name);
        MutableText output = Text.literal((String)"");
        CommandOutput icommandsender = new CommandOutput(){
            final /* synthetic */ Text val$output;
            final /* synthetic */ World val$level;
            {
                this.val$output = class_25612;
                this.val$level = class_19372;
            }

            public void sendMessage(Text component) {
                ((MutableText)this.val$output).append(component);
            }

            public boolean shouldReceiveFeedback() {
                return true;
            }

            public boolean shouldBroadcastConsoleToOps() {
                return this.val$level.getGameRules().getBoolean(GameRules.field_19394);
            }

            public boolean shouldTrackOutput() {
                return true;
            }
        };
        int permLvl = CustomNpcs.NpcUseOpCommands ? 4 : 2;
        Vec3d point = new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
        ServerCommandSource commandSource = new ServerCommandSource(icommandsender, point, Vec2f.ZERO, (ServerWorld)level, permLvl, "@CustomNPCs-" + name, (Text)Text.literal((String)("@CustomNPCs-" + name)), level.getServer(), executer){

            public void sendError(Text text) {
                super.sendError(text);
                CommonUtil.NotifyOPs(level.getServer(), text);
            }
        };
        CommandManager icommandmanager = level.getServer().getCommandManager();
        icommandmanager.executeWithPrefix(commandSource, command);
        if (output.getString().isEmpty()) {
            return null;
        }
        return output.getString();
    }

    public static void sendOpenGui(PlayerEntity player, EnumGuiType gui, EntityNPCInterface npc) {
        SPacketGuiOpen.sendOpenGui(player, gui, npc, BlockPos.ORIGIN);
    }

    private static ScreenHandlerType getType(EnumGuiType gui) {
        if (gui == EnumGuiType.PlayerAnvil) {
            return CustomContainer.container_carpentrybench;
        }
        if (gui == EnumGuiType.CustomGui) {
            return CustomContainer.container_customgui;
        }
        if (gui == EnumGuiType.PlayerBankUnlock) {
            return CustomContainer.container_bankunlock;
        }
        if (gui == EnumGuiType.PlayerBankLarge) {
            return CustomContainer.container_banklarge;
        }
        if (gui == EnumGuiType.PlayerBankUprade) {
            return CustomContainer.container_bankupgrade;
        }
        if (gui == EnumGuiType.PlayerBankSmall) {
            return CustomContainer.container_banksmall;
        }
        if (gui == EnumGuiType.PlayerMailman) {
            return CustomContainer.container_mail;
        }
        if (gui == EnumGuiType.MainMenuInv) {
            return CustomContainer.container_inv;
        }
        if (gui == EnumGuiType.QuestItem) {
            return CustomContainer.container_questtypeitem;
        }
        if (gui == EnumGuiType.QuestReward) {
            return CustomContainer.container_questreward;
        }
        if (gui == EnumGuiType.CompanionInv) {
            return CustomContainer.container_companion;
        }
        if (gui == EnumGuiType.PlayerTrader) {
            return CustomContainer.container_trader;
        }
        if (gui == EnumGuiType.PlayerFollower) {
            return CustomContainer.container_follower;
        }
        if (gui == EnumGuiType.PlayerFollowerHire) {
            return CustomContainer.container_followerhire;
        }
        if (gui == EnumGuiType.SetupTrader) {
            return CustomContainer.container_tradersetup;
        }
        if (gui == EnumGuiType.SetupFollower) {
            return CustomContainer.container_followersetup;
        }
        if (gui == EnumGuiType.SetupItemGiver) {
            return CustomContainer.container_itemgiver;
        }
        if (gui == EnumGuiType.ManageBanks) {
            return CustomContainer.container_managebanks;
        }
        if (gui == EnumGuiType.ManageRecipes) {
            return CustomContainer.container_managerecipes;
        }
        return null;
    }

    public static void openContainerGui(ServerPlayerEntity player, final EnumGuiType gui, Consumer<PacketByteBuf> extraDataWriter) {
        final PacketByteBuf outerbuf = new PacketByteBuf(Unpooled.buffer());
        extraDataWriter.accept(outerbuf);
        final ByteBuf copy = outerbuf.copy();
        player.openHandledScreen((NamedScreenHandlerFactory)new ExtendedScreenHandlerFactory(){

            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBytes(copy);
            }

            public ScreenHandler createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                return ((ExtendedScreenHandlerType)NoppesUtilServer.getType(gui)).create(p_createMenu_1_, p_createMenu_2_, outerbuf);
            }

            public Text getDisplayName() {
                return Text.literal((String)gui.name());
            }
        });
    }

    public static void spawnParticle(Entity entity, String particle, int dimension) {
        Packets.sendNearby(entity, new PacketParticle(entity.getX(), entity.getY(), entity.getZ(), entity.getHeight(), entity.getWidth(), particle));
    }

    public static void sendScrollData(ServerPlayerEntity player, Map<String, Integer> map) {
        Packets.send(player, new PacketGuiScrollData(map));
    }

    public static void sendGuiError(PlayerEntity player, int i) {
        Packets.send((ServerPlayerEntity)player, new PacketGuiError(i, new NbtCompound()));
    }

    public static void sendGuiClose(ServerPlayerEntity player, int i, NbtCompound comp) {
        Packets.send(player, new PacketGuiClose(comp));
    }

    public static void GivePlayerItem(Entity entity, PlayerEntity player, ItemStack item) {
        if (entity.getWorld().isClient || item == null || item.isEmpty()) {
            return;
        }
        item = item.copy();
        float f = 0.7f;
        double d = (double)(entity.getWorld().random.nextFloat() * f) + (double)(1.0f - f);
        double d1 = (double)(entity.getWorld().random.nextFloat() * f) + (double)(1.0f - f);
        double d2 = (double)(entity.getWorld().random.nextFloat() * f) + (double)(1.0f - f);
        ItemEntity entityitem = new ItemEntity(entity.getWorld(), entity.getX() + d, entity.getY() + d1, entity.getZ() + d2, item);
        entityitem.setPickupDelay(2);
        entity.getWorld().spawnEntity((Entity)entityitem);
        if (player.getInventory().insertStack(item)) {
            entity.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.field_15197, SoundCategory.field_15248, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.sendPickup((Entity)entityitem, item.getCount());
            PlayerQuestData playerdata = PlayerData.get((PlayerEntity)player).questData;
            playerdata.checkQuestCompletion(player, 0);
            if (item.getCount() <= 0) {
                entityitem.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    public static BlockPos GetClosePos(BlockPos origin, World level) {
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                for (int y = 2; y >= -2; --y) {
                    BlockPos pos = origin.add(x, y, z);
                    BlockState state = level.getBlockState(pos.up());
                    if (!state.isSolidBlock((BlockView)level, pos) || !level.isAir(pos.up()) || !level.isAir(pos.up(2))) continue;
                    return pos.up();
                }
            }
        }
        return level.getTopPosition(Heightmap.Type.field_13203, origin);
    }

    public static void playSound(LivingEntity entity, SoundEvent sound, float volume, float pitch) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.field_15254, volume, pitch);
    }

    public static void playSound(World level, BlockPos pos, SoundEvent sound, SoundCategory cat, float volume, float pitch) {
        level.playSound(null, pos, sound, cat, volume, pitch);
    }

    public static PlayerEntity getPlayer(MinecraftServer minecraftserver, UUID id) {
        List list = minecraftserver.getPlayerManager().getPlayerList();
        for (PlayerEntity player : list) {
            if (!id.equals(player.getUuid())) continue;
            return player;
        }
        return null;
    }

    public static Entity GetDamageSourcee(DamageSource damagesource) {
        Entity entity = damagesource.getAttacker();
        if (entity == null) {
            entity = damagesource.getSource();
        }
        if (entity instanceof EntityProjectile && ((EntityProjectile)entity).getOwner() instanceof LivingEntity) {
            entity = ((PersistentProjectileEntity)entity).getOwner();
        } else if (entity instanceof ThrownEntity) {
            entity = ((ThrownEntity)entity).getOwner();
        }
        return entity;
    }

    public static boolean IsItemStackNull(ItemStack is) {
        return is == null || is.isEmpty() || is == ItemStack.EMPTY || is.getItem() == null;
    }

    public static ItemStack ChangeItemStack(ItemStack is, Item item) {
        NbtCompound comp = is.writeNbt(new NbtCompound());
        Identifier resourcelocation = Registries.ITEM.getId((Object)item);
        comp.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        return ItemStack.fromNbt((NbtCompound)comp);
    }
}

