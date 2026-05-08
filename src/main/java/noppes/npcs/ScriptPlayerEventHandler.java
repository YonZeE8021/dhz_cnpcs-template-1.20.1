/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents$AllowDamage
 *  net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents$AllowDeath
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$StartTick
 *  net.fabricmc.fabric.api.event.player.AttackBlockCallback
 *  net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents$Before
 *  net.fabricmc.fabric.api.event.player.UseBlockCallback
 *  net.fabricmc.fabric.api.event.player.UseEntityCallback
 *  net.fabricmc.fabric.api.event.player.UseItemCallback
 *  net.fabricmc.fabric.api.message.v1.ServerMessageEvents$AllowChatMessage
 *  net.fabricmc.fabric.api.networking.v1.PacketSender
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Disconnect
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Join
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.TypedActionResult
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.network.message.MessageType$Parameters
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.network.ServerPlayNetworkHandler
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.network.message.SignedMessage
 *  net.minecraft.server.MinecraftServer
 *  org.jetbrains.annotations.Nullable
 */
package noppes.npcs;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.network.message.MessageType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemNbtBook;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketItemUpdate;
import org.jetbrains.annotations.Nullable;

public class ScriptPlayerEventHandler
implements ServerTickEvents.StartTick,
AttackBlockCallback,
UseBlockCallback,
UseEntityCallback,
UseItemCallback,
PlayerBlockBreakEvents.Before,
ServerMessageEvents.AllowChatMessage,
ServerLivingEntityEvents.AllowDamage,
ServerLivingEntityEvents.AllowDeath,
ServerPlayConnectionEvents.Join,
ServerPlayConnectionEvents.Disconnect {
    public void onStartTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerData data = PlayerData.get(player);
            if (player.age % 10 == 0) {
                EventHooks.onPlayerTick(data.scriptData);
                for (int i = 0; i < player.getInventory().size(); ++i) {
                    ItemStack item = player.getInventory().getStack(i);
                    if (item.isEmpty() || item.getItem() != CustomItems.scripted_item) continue;
                    ItemScriptedWrapper isw = (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(item);
                    EventHooks.onScriptItemUpdate(isw, player);
                    if (!isw.updateClient) continue;
                    isw.updateClient = false;
                    Packets.send((ServerPlayerEntity)player, new PacketItemUpdate(i, isw.getMCNbt()));
                }
            }
            if (data.playerLevel != player.experienceLevel) {
                EventHooks.onPlayerLevelUp(data.scriptData, data.playerLevel - player.experienceLevel);
                data.playerLevel = player.experienceLevel;
            }
            data.timers.update();
        }
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (world.isClient || hand != Hand.MAIN_HAND || !(world instanceof ServerWorld)) {
            return ActionResult.PASS;
        }
        if (player.getStackInHand(hand).getItem() == CustomItems.teleporter) {
            return ActionResult.FAIL;
        }
        boolean isCancelled = false;
        PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
        PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(world, pos));
        isCancelled = EventHooks.onPlayerAttack(handler, ev);
        if (player.getStackInHand(hand).getItem() == CustomItems.scripted_item && !isCancelled) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(player.getStackInHand(hand));
            ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent((IItemScripted)isw, handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(world, pos));
            eve.setCanceled(isCancelled);
            isCancelled = EventHooks.onScriptItemAttack(isw, eve);
        }
        return isCancelled ? ActionResult.FAIL : ActionResult.PASS;
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (player.getWorld().isClient || hand != Hand.MAIN_HAND || !(world instanceof ServerWorld)) {
            return ActionResult.PASS;
        }
        if (player.getStackInHand(hand).getItem() == CustomItems.nbt_book) {
            ((ItemNbtBook)player.getStackInHand(hand).getItem()).blockEvent(player, world, hand, hitResult);
            return ActionResult.FAIL;
        }
        if (player.getStackInHand(hand).getItem() == CustomItems.teleporter) {
            return ActionResult.FAIL;
        }
        boolean isCancelled = false;
        PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
        handler.hadInteract = true;
        PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(world, hitResult.getBlockPos()));
        isCancelled = EventHooks.onPlayerInteract(handler, ev);
        if (player.getStackInHand(hand).getItem() == CustomItems.scripted_item && !isCancelled) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(player.getStackInHand(hand));
            ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(world, hitResult.getBlockPos()));
            isCancelled = EventHooks.onScriptItemInteract(isw, eve);
        }
        return isCancelled ? ActionResult.FAIL : ActionResult.PASS;
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world.isClient || hand != Hand.MAIN_HAND || !(world instanceof ServerWorld)) {
            return ActionResult.PASS;
        }
        if (player.getStackInHand(hand).getItem() == CustomItems.nbt_book) {
            ((ItemNbtBook)player.getStackInHand(hand).getItem()).entityEvent(player, world, hand, entity, hitResult);
            return ActionResult.FAIL;
        }
        PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
        PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 1, NpcAPI.Instance().getIEntity(entity));
        boolean isCancelled = false;
        isCancelled = EventHooks.onPlayerInteract(handler, ev);
        if (player.getStackInHand(hand).getItem() == CustomItems.scripted_item && !isCancelled) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(player.getStackInHand(hand));
            ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 1, NpcAPI.Instance().getIEntity(entity));
            isCancelled = EventHooks.onScriptItemInteract(isw, eve);
        }
        return isCancelled ? ActionResult.FAIL : ActionResult.PASS;
    }

    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        if (world.isClient || hand != Hand.MAIN_HAND || !(world instanceof ServerWorld)) {
            return TypedActionResult.pass(player.getStackInHand(hand));
        }
        if (player.isCreative() && player.isInSneakingPose() && player.getStackInHand(hand).getItem() == CustomItems.scripted_item) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.ScriptItem, null);
            return TypedActionResult.pass(player.getStackInHand(hand));
        }
        PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
        if (handler.hadInteract) {
            handler.hadInteract = false;
            return TypedActionResult.pass(player.getStackInHand(hand));
        }
        PlayerEvent.InteractEvent ev = new PlayerEvent.InteractEvent(handler.getPlayer(), 0, null);
        boolean isCancelled = EventHooks.onPlayerInteract(handler, ev);
        if (player.getStackInHand(hand).getItem() == CustomItems.scripted_item && !isCancelled) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(player.getStackInHand(hand));
            ItemEvent.InteractEvent eve = new ItemEvent.InteractEvent(isw, handler.getPlayer(), 0, null);
            isCancelled = EventHooks.onScriptItemInteract(isw, eve);
        }
        return isCancelled ? TypedActionResult.fail(player.getStackInHand(hand)) : TypedActionResult.pass(player.getStackInHand(hand));
    }

    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (player.getWorld().isClient || !(world instanceof ServerWorld)) {
            return true;
        }
        PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
        PlayerEvent.BreakEvent ev = new PlayerEvent.BreakEvent(handler.getPlayer(), NpcAPI.Instance().getIBlock((World)((ServerWorld)world), pos));
        return !EventHooks.onPlayerBreak(handler, ev);
    }

    public boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        PlayerScriptData handler;
        if (!(entity.getWorld() instanceof ServerWorld)) {
            return true;
        }
        Entity sourceEnt = NoppesUtilServer.GetDamageSourcee(damageSource);
        if (entity instanceof PlayerEntity) {
            handler = PlayerData.get((PlayerEntity)((PlayerEntity)entity)).scriptData;
            EventHooks.onPlayerDeath(handler, damageSource, sourceEnt);
        }
        if (sourceEnt instanceof PlayerEntity) {
            handler = PlayerData.get((PlayerEntity)((PlayerEntity)sourceEnt)).scriptData;
            EventHooks.onPlayerKills(handler, entity);
        }
        return true;
    }

    public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        PlayerEvent pevent;
        PlayerScriptData handler;
        if (!(entity.getWorld() instanceof ServerWorld)) {
            return true;
        }
        Entity sourceEnt = NoppesUtilServer.GetDamageSourcee(source);
        boolean isCanceled = false;
        if (entity instanceof PlayerEntity) {
            handler = PlayerData.get((PlayerEntity)((PlayerEntity)entity)).scriptData;
            pevent = new PlayerEvent.DamagedEvent(handler.getPlayer(), sourceEnt, amount, source);
            isCanceled = EventHooks.onPlayerDamaged(handler, (PlayerEvent.DamagedEvent)pevent);
        }
        if (sourceEnt instanceof PlayerEntity) {
            handler = PlayerData.get((PlayerEntity)((PlayerEntity)sourceEnt)).scriptData;
            pevent = new PlayerEvent.DamagedEntityEvent(handler.getPlayer(), (Entity)entity, amount, source);
            isCanceled = EventHooks.onPlayerDamagedEntity(handler, (PlayerEvent.DamagedEntityEvent)pevent);
        }
        return !isCanceled;
    }

    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if (!(handler.player.getWorld() instanceof ServerWorld)) {
            return;
        }
        PlayerScriptData scriptData = PlayerData.get((PlayerEntity)handler.player).scriptData;
        EventHooks.onPlayerLogin(scriptData);
    }

    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if (!(handler.player.getWorld() instanceof ServerWorld)) {
            return;
        }
        PlayerScriptData scriptData = PlayerData.get((PlayerEntity)handler.player).scriptData;
        EventHooks.onPlayerLogout(scriptData);
    }

    public boolean allowChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        if (!(sender.getWorld() instanceof ServerWorld) || sender == EntityNPCInterface.ChatEventPlayer) {
            return true;
        }
        PlayerScriptData handler = PlayerData.get((PlayerEntity)sender).scriptData;
        String messageStr = message.getContent().getString();
        PlayerEvent.ChatEvent ev = new PlayerEvent.ChatEvent(handler.getPlayer(), message.getContent().getString());
        EventHooks.onPlayerChat(handler, ev);
        boolean isCanceled = ev.isCanceled();
        return !isCanceled;
    }
}

