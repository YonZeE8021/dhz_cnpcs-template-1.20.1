/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.context.CommandContext
 *  net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents$AfterDeath
 *  net.fabricmc.fabric.api.event.player.UseEntityCallback
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.command.argument.EntityArgumentType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.registry.Registries
 *  org.jetbrains.annotations.Nullable
 */
package noppes.npcs;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemSoulstoneEmpty;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiCloneOpen;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.quests.QuestKill;
import org.jetbrains.annotations.Nullable;

public class ServerEventsHandler
implements UseEntityCallback,
ServerLivingEntityEvents.AfterDeath {
    public static VillagerEntity Merchant;

    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        ItemStack item = player.getMainHandStack();
        boolean isCancelled = false;
        if (item.isEmpty() || hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        boolean isClientSide = player.getWorld().isClient;
        boolean npcInteracted = entity instanceof EntityNPCInterface;
        if (!isClientSide && CustomNpcs.OpsOnly && !player.getServer().getPlayerManager().isOperator(player.getGameProfile())) {
            return ActionResult.PASS;
        }
        if (!isClientSide && item.getItem() == CustomItems.soulstoneEmpty && entity instanceof LivingEntity) {
            ((ItemSoulstoneEmpty)item.getItem()).store((LivingEntity)entity, item, player);
        }
        if (item.getItem() == CustomItems.wand && npcInteracted && !isClientSide) {
            if (!CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.NPC_GUI)) {
                return ActionResult.PASS;
            }
            isCancelled = true;
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MainMenuDisplay, (EntityNPCInterface)entity);
        } else if (item.getItem() == CustomItems.cloner && !isClientSide && !(entity instanceof PlayerEntity)) {
            NbtCompound compound = new NbtCompound();
            if (!entity.saveSelfNbt(compound)) {
                return ActionResult.PASS;
            }
            PlayerData data = PlayerData.get(player);
            ServerCloneController.Instance.cleanTags(compound);
            Packets.send((ServerPlayerEntity)player, new PacketGuiCloneOpen(compound));
            data.cloned = compound;
            isCancelled = true;
        } else if (item.getItem() == CustomItems.scripter && !isClientSide && npcInteracted) {
            if (!CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.NPC_GUI)) {
                return ActionResult.PASS;
            }
            NoppesUtilServer.setEditingNpc(player, (EntityNPCInterface)entity);
            isCancelled = true;
            Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(EnumGuiType.Script, BlockPos.ORIGIN));
        } else if (item.getItem() == CustomItems.mount && !isClientSide) {
            if (!CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.TOOL_MOUNTER)) {
                return ActionResult.PASS;
            }
            PlayerData data = PlayerData.get(player);
            isCancelled = true;
            data.mounted = entity;
            Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(EnumGuiType.MobSpawnerMounter, BlockPos.ORIGIN));
        }
        return isCancelled ? ActionResult.FAIL : ActionResult.PASS;
    }

    public void afterDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity.getWorld().isClient) {
            return;
        }
        Entity source = NoppesUtilServer.GetDamageSourcee(damageSource);
        if (source != null) {
            if (source instanceof EntityNPCInterface && entity != null) {
                EntityNPCInterface npc = (EntityNPCInterface)source;
                Line line = npc.advanced.getKillLine();
                if (line != null) {
                    npc.saySurrounding(Line.formatTarget(line, entity));
                }
                EventHooks.onNPCKills(npc, entity);
            }
            PlayerEntity player = null;
            if (source instanceof PlayerEntity) {
                player = (PlayerEntity)source;
            } else if (source instanceof EntityNPCInterface && ((EntityNPCInterface)source).getOwner() instanceof PlayerEntity) {
                player = (PlayerEntity)((EntityNPCInterface)source).getOwner();
            }
            if (player != null) {
                this.doQuest(player, entity, true);
                if (entity instanceof EntityNPCInterface) {
                    this.doFactionPoints(player, (EntityNPCInterface)entity);
                }
            }
        }
        if (entity instanceof PlayerEntity) {
            PlayerData data = PlayerData.get((PlayerEntity)entity);
            data.save(false);
        }
    }

    private void doFactionPoints(PlayerEntity player, EntityNPCInterface npc) {
        npc.advanced.factions.addPoints(player);
    }

    private void doQuest(PlayerEntity player, LivingEntity entity, boolean all) {
        PlayerData pdata = PlayerData.get(player);
        PlayerQuestData playerdata = pdata.questData;
        String entityName = Registries.ENTITY_TYPE.getId(entity.getType()).toString();
        if (entity instanceof PlayerEntity) {
            entityName = "Player";
        }
        for (QuestData data : playerdata.activeQuests.values()) {
            if (data.quest.type != 2 && data.quest.type != 4) continue;
            if (data.quest.type == 4 && all) {
                List<PlayerEntity> list = player.getWorld().getNonSpectatingEntities(PlayerEntity.class, entity.getBoundingBox().expand(10.0, 10.0, 10.0));
                for (PlayerEntity pl : list) {
                    if (pl == player) continue;
                    this.doQuest(pl, entity, false);
                }
            }
            String name = entityName;
            QuestKill quest = (QuestKill)data.quest.questInterface;
            if (quest.targets.containsKey(entity.getName().getString())) {
                name = entity.getName().getString();
            } else if (!quest.targets.containsKey(name)) continue;
            HashMap<String, Integer> killed = quest.getKilled(data);
            if (killed.containsKey(name) && killed.get(name) >= quest.targets.get(name)) continue;
            int amount = 0;
            if (killed.containsKey(name)) {
                amount = killed.get(name);
            }
            killed.put(name, amount + 1);
            quest.setKilled(data, killed);
            pdata.updateClient = true;
        }
        playerdata.checkQuestCompletion(player, 2);
        playerdata.checkQuestCompletion(player, 4);
    }

    public static boolean allowCommandMessage(ParseResults<ServerCommandSource> parseRes) {
        String command = parseRes.getReader().getString();
        if (command.startsWith("give ")) {
            try {
                CommandContext context = parseRes.getContext().build(parseRes.getReader().getString());
                Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"targets");
                for (ServerPlayerEntity player : players) {
                    player.getServer().execute((Runnable)ListenableFutureTask.create(Executors.callable(() -> {
                        PlayerQuestData playerdata = PlayerData.get((PlayerEntity)player).questData;
                        playerdata.checkQuestCompletion((PlayerEntity)player, 0);
                    })));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (command.startsWith("time ")) {
            try {
                CustomNpcs.Server.submit(() -> {
                    List<ServerPlayerEntity> players = CustomNpcs.Server.getPlayerManager().getPlayerList();
                    for (ServerPlayerEntity playerMP : players) {
                        VisibilityController.instance.onUpdate(playerMP);
                    }
                });
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return true;
    }
}

