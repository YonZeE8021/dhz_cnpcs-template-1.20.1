/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.server.world.ServerChunkManager
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker
 *  net.minecraft.server.world.EntityTrackingListener
 *  net.minecraft.server.MinecraftServer
 *  org.apache.logging.log4j.util.TriConsumer
 */
package noppes.npcs.packets;

import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.packets.client.PacketChatBubble;
import noppes.npcs.packets.client.PacketConfigFont;
import noppes.npcs.packets.client.PacketDialog;
import noppes.npcs.packets.client.PacketDialogDummy;
import noppes.npcs.packets.client.PacketEyeBlink;
import noppes.npcs.packets.client.PacketGuiCloneOpen;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.packets.client.PacketGuiComponentUpdate;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiError;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.client.PacketGuiParts;
import noppes.npcs.packets.client.PacketGuiScrollData;
import noppes.npcs.packets.client.PacketGuiScrollList;
import noppes.npcs.packets.client.PacketGuiScrollSelected;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.packets.client.PacketHideAllOverlays;
import noppes.npcs.packets.client.PacketItemUpdate;
import noppes.npcs.packets.client.PacketMarkData;
import noppes.npcs.packets.client.PacketNpcDelete;
import noppes.npcs.packets.client.PacketNpcEdit;
import noppes.npcs.packets.client.PacketNpcRole;
import noppes.npcs.packets.client.PacketNpcRotationUpdate;
import noppes.npcs.packets.client.PacketNpcUpdate;
import noppes.npcs.packets.client.PacketNpcVisibleFalse;
import noppes.npcs.packets.client.PacketNpcVisibleTrue;
import noppes.npcs.packets.client.PacketOverlayHide;
import noppes.npcs.packets.client.PacketOverlayShow;
import noppes.npcs.packets.client.PacketParticle;
import noppes.npcs.packets.client.PacketPlayMusic;
import noppes.npcs.packets.client.PacketPlaySound;
import noppes.npcs.packets.client.PacketQuestCompletion;
import noppes.npcs.packets.client.PacketSoundGUIOpen;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.packets.client.PacketSyncRecipeRemove;
import noppes.npcs.packets.client.PacketSyncRecipeUpdate;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncSkin;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.packets.client.PacketTraderLimitSync;
import noppes.npcs.packets.client.PacketUpdatePhysics;
import noppes.npcs.packets.server.SPacketBankGet;
import noppes.npcs.packets.server.SPacketBankRemove;
import noppes.npcs.packets.server.SPacketBankSave;
import noppes.npcs.packets.server.SPacketBankUnlock;
import noppes.npcs.packets.server.SPacketBankUpgrade;
import noppes.npcs.packets.server.SPacketBanksGet;
import noppes.npcs.packets.server.SPacketBanksSlotOpen;
import noppes.npcs.packets.server.SPacketCloneList;
import noppes.npcs.packets.server.SPacketCloneNameCheck;
import noppes.npcs.packets.server.SPacketCloneRemove;
import noppes.npcs.packets.server.SPacketCloneSave;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketCompanionTalentExp;
import noppes.npcs.packets.server.SPacketCustomGuiButton;
import noppes.npcs.packets.server.SPacketCustomGuiButtonList;
import noppes.npcs.packets.server.SPacketCustomGuiFocusUpdate;
import noppes.npcs.packets.server.SPacketCustomGuiParts;
import noppes.npcs.packets.server.SPacketCustomGuiScrollClick;
import noppes.npcs.packets.server.SPacketCustomGuiSliderUpdate;
import noppes.npcs.packets.server.SPacketCustomGuiSubGuiClosed;
import noppes.npcs.packets.server.SPacketCustomGuiTextUpdate;
import noppes.npcs.packets.server.SPacketDialogCategoryRemove;
import noppes.npcs.packets.server.SPacketDialogCategorySave;
import noppes.npcs.packets.server.SPacketDialogRemove;
import noppes.npcs.packets.server.SPacketDialogSave;
import noppes.npcs.packets.server.SPacketDialogSelected;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.packets.server.SPacketDimensionsGet;
import noppes.npcs.packets.server.SPacketFactionGet;
import noppes.npcs.packets.server.SPacketFactionRemove;
import noppes.npcs.packets.server.SPacketFactionSave;
import noppes.npcs.packets.server.SPacketFactionsGet;
import noppes.npcs.packets.server.SPacketFollowerExtend;
import noppes.npcs.packets.server.SPacketFollowerHire;
import noppes.npcs.packets.server.SPacketFollowerState;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.packets.server.SPacketLinkedAdd;
import noppes.npcs.packets.server.SPacketLinkedGet;
import noppes.npcs.packets.server.SPacketLinkedRemove;
import noppes.npcs.packets.server.SPacketLinkedSet;
import noppes.npcs.packets.server.SPacketMailSetup;
import noppes.npcs.packets.server.SPacketMenuClose;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.packets.server.SPacketNaturalSpawnGet;
import noppes.npcs.packets.server.SPacketNaturalSpawnGetAll;
import noppes.npcs.packets.server.SPacketNaturalSpawnRemove;
import noppes.npcs.packets.server.SPacketNaturalSpawnSave;
import noppes.npcs.packets.server.SPacketNbtBookBlockSave;
import noppes.npcs.packets.server.SPacketNbtBookEntitySave;
import noppes.npcs.packets.server.SPacketNpRandomNameSet;
import noppes.npcs.packets.server.SPacketNpcDelete;
import noppes.npcs.packets.server.SPacketNpcDialogRemove;
import noppes.npcs.packets.server.SPacketNpcDialogSet;
import noppes.npcs.packets.server.SPacketNpcDialogsGet;
import noppes.npcs.packets.server.SPacketNpcFactionSet;
import noppes.npcs.packets.server.SPacketNpcJobGet;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.packets.server.SPacketNpcJobSpawnerSet;
import noppes.npcs.packets.server.SPacketNpcMarketSet;
import noppes.npcs.packets.server.SPacketNpcRoleCompanionUpdate;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.packets.server.SPacketNpcTransform;
import noppes.npcs.packets.server.SPacketNpcTransportGet;
import noppes.npcs.packets.server.SPacketOpenParts;
import noppes.npcs.packets.server.SPacketPlayerCloseContainer;
import noppes.npcs.packets.server.SPacketPlayerDataGet;
import noppes.npcs.packets.server.SPacketPlayerDataRemove;
import noppes.npcs.packets.server.SPacketPlayerKeyPressed;
import noppes.npcs.packets.server.SPacketPlayerLeftClicked;
import noppes.npcs.packets.server.SPacketPlayerMailDelete;
import noppes.npcs.packets.server.SPacketPlayerMailGet;
import noppes.npcs.packets.server.SPacketPlayerMailOpen;
import noppes.npcs.packets.server.SPacketPlayerMailRead;
import noppes.npcs.packets.server.SPacketPlayerMailSend;
import noppes.npcs.packets.server.SPacketPlayerSoundPlays;
import noppes.npcs.packets.server.SPacketTraderLimitRefresh;
import noppes.npcs.packets.server.SPacketPlayerTransport;
import noppes.npcs.packets.server.SPacketQuestCategoryRemove;
import noppes.npcs.packets.server.SPacketQuestCategorySave;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;
import noppes.npcs.packets.server.SPacketQuestCompletionCheckAll;
import noppes.npcs.packets.server.SPacketQuestDialogTitles;
import noppes.npcs.packets.server.SPacketQuestOpen;
import noppes.npcs.packets.server.SPacketQuestRemove;
import noppes.npcs.packets.server.SPacketQuestSave;
import noppes.npcs.packets.server.SPacketRecipeGet;
import noppes.npcs.packets.server.SPacketRecipeRemove;
import noppes.npcs.packets.server.SPacketRecipeSave;
import noppes.npcs.packets.server.SPacketRecipesGet;
import noppes.npcs.packets.server.SPacketRemoteFreeze;
import noppes.npcs.packets.server.SPacketRemoteMenuOpen;
import noppes.npcs.packets.server.SPacketRemoteNpcDelete;
import noppes.npcs.packets.server.SPacketRemoteNpcReset;
import noppes.npcs.packets.server.SPacketRemoteNpcTp;
import noppes.npcs.packets.server.SPacketRemoteNpcsGet;
import noppes.npcs.packets.server.SPacketSceneReset;
import noppes.npcs.packets.server.SPacketSceneStart;
import noppes.npcs.packets.server.SPacketSchematicsStore;
import noppes.npcs.packets.server.SPacketSchematicsTileBuild;
import noppes.npcs.packets.server.SPacketSchematicsTileGet;
import noppes.npcs.packets.server.SPacketSchematicsTileSave;
import noppes.npcs.packets.server.SPacketSchematicsTileSet;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import noppes.npcs.packets.server.SPacketToolMounter;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportCategoryRemove;
import noppes.npcs.packets.server.SPacketTransportCategorySave;
import noppes.npcs.packets.server.SPacketTransportGet;
import noppes.npcs.packets.server.SPacketTransportRemove;
import noppes.npcs.packets.server.SPacketTransportSave;
import noppes.npcs.shared.common.PacketBasic;
import org.apache.logging.log4j.util.TriConsumer;

public class Packets {
    public static int index = 0;
    public static HashMap<Class, Integer> indexes = new HashMap();
    public static HashMap<Class, BiConsumer> encoders = new HashMap();

    public static void register() {
        index = 0;
        Packets.registerPacket(index++, PacketAchievement.class, PacketAchievement::encode, PacketAchievement::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketChat.class, PacketChat::encode, PacketChat::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketChatBubble.class, PacketChatBubble::encode, PacketChatBubble::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketConfigFont.class, PacketConfigFont::encode, PacketConfigFont::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketDialog.class, PacketDialog::encode, PacketDialog::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketDialogDummy.class, PacketDialogDummy::encode, PacketDialogDummy::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketEyeBlink.class, PacketEyeBlink::encode, PacketEyeBlink::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiCloneOpen.class, PacketGuiCloneOpen::encode, PacketGuiCloneOpen::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiClose.class, PacketGuiClose::encode, PacketGuiClose::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiData.class, PacketGuiData::encode, PacketGuiData::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiComponentUpdate.class, PacketGuiComponentUpdate::encode, PacketGuiComponentUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiError.class, PacketGuiError::encode, PacketGuiError::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiOpen.class, PacketGuiOpen::encode, PacketGuiOpen::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiScrollData.class, PacketGuiScrollData::encode, PacketGuiScrollData::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiScrollList.class, PacketGuiScrollList::encode, PacketGuiScrollList::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiScrollSelected.class, PacketGuiScrollSelected::encode, PacketGuiScrollSelected::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiUpdate.class, PacketGuiUpdate::encode, PacketGuiUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketItemUpdate.class, PacketItemUpdate::encode, PacketItemUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketMarkData.class, PacketMarkData::encode, PacketMarkData::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcDelete.class, PacketNpcDelete::encode, PacketNpcDelete::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcEdit.class, PacketNpcEdit::encode, PacketNpcEdit::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcRole.class, PacketNpcRole::encode, PacketNpcRole::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcUpdate.class, PacketNpcUpdate::encode, PacketNpcUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketParticle.class, PacketParticle::encode, PacketParticle::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketPlayMusic.class, PacketPlayMusic::encode, PacketPlayMusic::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketPlaySound.class, PacketPlaySound::encode, PacketPlaySound::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketQuestCompletion.class, PacketQuestCompletion::encode, PacketQuestCompletion::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSync.class, PacketSync::encode, PacketSync::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSyncRecipeUpdate.class, PacketSyncRecipeUpdate::encode, PacketSyncRecipeUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSyncRecipeRemove.class, PacketSyncRecipeRemove::encode, PacketSyncRecipeRemove::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSyncRemove.class, PacketSyncRemove::encode, PacketSyncRemove::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSyncUpdate.class, PacketSyncUpdate::encode, PacketSyncUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcVisibleFalse.class, PacketNpcVisibleFalse::encode, PacketNpcVisibleFalse::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcVisibleTrue.class, PacketNpcVisibleTrue::encode, PacketNpcVisibleTrue::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketUpdatePhysics.class, PacketUpdatePhysics::encode, PacketUpdatePhysics::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketGuiParts.class, PacketGuiParts::encode, PacketGuiParts::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketOverlayShow.class, PacketOverlayShow::encode, PacketOverlayShow::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketOverlayHide.class, PacketOverlayHide::encode, PacketOverlayHide::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketHideAllOverlays.class, PacketHideAllOverlays::encode, PacketHideAllOverlays::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSoundGUIOpen.class, PacketSoundGUIOpen::encode, PacketSoundGUIOpen::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketNpcRotationUpdate.class, PacketNpcRotationUpdate::encode, PacketNpcRotationUpdate::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketSyncSkin.class, PacketSyncSkin::encode, PacketSyncSkin::decode, PacketBasic::handle);
        Packets.registerPacket(index++, PacketTraderLimitSync.class, PacketTraderLimitSync::encode, PacketTraderLimitSync::decode, PacketBasic::handle);
        Packets.registerServerPacket(index++, SPacketBankGet.class, SPacketBankGet::encode, SPacketBankGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketBankRemove.class, SPacketBankRemove::encode, SPacketBankRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketBankSave.class, SPacketBankSave::encode, SPacketBankSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketBanksGet.class, SPacketBanksGet::encode, SPacketBanksGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketBanksSlotOpen.class, SPacketBanksSlotOpen::encode, SPacketBanksSlotOpen::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketBankUnlock.class, SPacketBankUnlock::encode, SPacketBankUnlock::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketBankUpgrade.class, SPacketBankUpgrade::encode, SPacketBankUpgrade::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCloneList.class, SPacketCloneList::encode, SPacketCloneList::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCloneNameCheck.class, SPacketCloneNameCheck::encode, SPacketCloneNameCheck::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCloneRemove.class, SPacketCloneRemove::encode, SPacketCloneRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCloneSave.class, SPacketCloneSave::encode, SPacketCloneSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCompanionOpenInv.class, SPacketCompanionOpenInv::encode, SPacketCompanionOpenInv::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCompanionTalentExp.class, SPacketCompanionTalentExp::encode, SPacketCompanionTalentExp::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDialogCategoryRemove.class, SPacketDialogCategoryRemove::encode, SPacketDialogCategoryRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDialogRemove.class, SPacketDialogRemove::encode, SPacketDialogRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDialogSelected.class, SPacketDialogSelected::encode, SPacketDialogSelected::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDimensionsGet.class, SPacketDimensionsGet::encode, SPacketDimensionsGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDimensionTeleport.class, SPacketDimensionTeleport::encode, SPacketDimensionTeleport::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFactionGet.class, SPacketFactionGet::encode, SPacketFactionGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFactionRemove.class, SPacketFactionRemove::encode, SPacketFactionRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFactionSave.class, SPacketFactionSave::encode, SPacketFactionSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFactionsGet.class, SPacketFactionsGet::encode, SPacketFactionsGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFollowerExtend.class, SPacketFollowerExtend::encode, SPacketFollowerExtend::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFollowerHire.class, SPacketFollowerHire::encode, SPacketFollowerHire::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketFollowerState.class, SPacketFollowerState::encode, SPacketFollowerState::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketGuiOpen.class, SPacketGuiOpen::encode, SPacketGuiOpen::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketLinkedAdd.class, SPacketLinkedAdd::encode, SPacketLinkedAdd::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketLinkedGet.class, SPacketLinkedGet::encode, SPacketLinkedGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketLinkedRemove.class, SPacketLinkedRemove::encode, SPacketLinkedRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketLinkedSet.class, SPacketLinkedSet::encode, SPacketLinkedSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketMailSetup.class, SPacketMailSetup::encode, SPacketMailSetup::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketMenuClose.class, SPacketMenuClose::encode, SPacketMenuClose::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketMenuGet.class, SPacketMenuGet::encode, SPacketMenuGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketMenuSave.class, SPacketMenuSave::encode, SPacketMenuSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNaturalSpawnGet.class, SPacketNaturalSpawnGet::encode, SPacketNaturalSpawnGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNaturalSpawnGetAll.class, SPacketNaturalSpawnGetAll::encode, SPacketNaturalSpawnGetAll::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNaturalSpawnRemove.class, SPacketNaturalSpawnRemove::encode, SPacketNaturalSpawnRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNaturalSpawnSave.class, SPacketNaturalSpawnSave::encode, SPacketNaturalSpawnSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNbtBookBlockSave.class, SPacketNbtBookBlockSave::encode, SPacketNbtBookBlockSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNbtBookEntitySave.class, SPacketNbtBookEntitySave::encode, SPacketNbtBookEntitySave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcDelete.class, SPacketNpcDelete::encode, SPacketNpcDelete::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcDialogRemove.class, SPacketNpcDialogRemove::encode, SPacketNpcDialogRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcDialogSet.class, SPacketNpcDialogSet::encode, SPacketNpcDialogSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcDialogsGet.class, SPacketNpcDialogsGet::encode, SPacketNpcDialogsGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcFactionSet.class, SPacketNpcFactionSet::encode, SPacketNpcFactionSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcJobGet.class, SPacketNpcJobGet::encode, SPacketNpcJobGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcJobSave.class, SPacketNpcJobSave::encode, SPacketNpcJobSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcJobSpawnerSet.class, SPacketNpcJobSpawnerSet::encode, SPacketNpcJobSpawnerSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcMarketSet.class, SPacketNpcMarketSet::encode, SPacketNpcMarketSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcRoleCompanionUpdate.class, SPacketNpcRoleCompanionUpdate::encode, SPacketNpcRoleCompanionUpdate::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcRoleGet.class, SPacketNpcRoleGet::encode, SPacketNpcRoleGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcRoleSave.class, SPacketNpcRoleSave::encode, SPacketNpcRoleSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcTransform.class, SPacketNpcTransform::encode, SPacketNpcTransform::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpcTransportGet.class, SPacketNpcTransportGet::encode, SPacketNpcTransportGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerCloseContainer.class, SPacketPlayerCloseContainer::encode, SPacketPlayerCloseContainer::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerDataGet.class, SPacketPlayerDataGet::encode, SPacketPlayerDataGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerDataRemove.class, SPacketPlayerDataRemove::encode, SPacketPlayerDataRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerKeyPressed.class, SPacketPlayerKeyPressed::encode, SPacketPlayerKeyPressed::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerLeftClicked.class, SPacketPlayerLeftClicked::encode, SPacketPlayerLeftClicked::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerMailDelete.class, SPacketPlayerMailDelete::encode, SPacketPlayerMailDelete::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerMailGet.class, SPacketPlayerMailGet::encode, SPacketPlayerMailGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerMailOpen.class, SPacketPlayerMailOpen::encode, SPacketPlayerMailOpen::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerMailRead.class, SPacketPlayerMailRead::encode, SPacketPlayerMailRead::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerMailSend.class, SPacketPlayerMailSend::encode, SPacketPlayerMailSend::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerTransport.class, SPacketPlayerTransport::encode, SPacketPlayerTransport::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestCategoryRemove.class, SPacketQuestCategoryRemove::encode, SPacketQuestCategoryRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestCompletionCheck.class, SPacketQuestCompletionCheck::encode, SPacketQuestCompletionCheck::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestCompletionCheckAll.class, SPacketQuestCompletionCheckAll::encode, SPacketQuestCompletionCheckAll::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestDialogTitles.class, SPacketQuestDialogTitles::encode, SPacketQuestDialogTitles::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestOpen.class, SPacketQuestOpen::encode, SPacketQuestOpen::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestRemove.class, SPacketQuestRemove::encode, SPacketQuestRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRecipeGet.class, SPacketRecipeGet::encode, SPacketRecipeGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRecipeRemove.class, SPacketRecipeRemove::encode, SPacketRecipeRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRecipeSave.class, SPacketRecipeSave::encode, SPacketRecipeSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRecipesGet.class, SPacketRecipesGet::encode, SPacketRecipesGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRemoteFreeze.class, SPacketRemoteFreeze::encode, SPacketRemoteFreeze::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRemoteMenuOpen.class, SPacketRemoteMenuOpen::encode, SPacketRemoteMenuOpen::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRemoteNpcDelete.class, SPacketRemoteNpcDelete::encode, SPacketRemoteNpcDelete::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRemoteNpcReset.class, SPacketRemoteNpcReset::encode, SPacketRemoteNpcReset::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRemoteNpcsGet.class, SPacketRemoteNpcsGet::encode, SPacketRemoteNpcsGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketRemoteNpcTp.class, SPacketRemoteNpcTp::encode, SPacketRemoteNpcTp::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSceneReset.class, SPacketSceneReset::encode, SPacketSceneReset::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSceneStart.class, SPacketSceneStart::encode, SPacketSceneStart::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSchematicsStore.class, SPacketSchematicsStore::encode, SPacketSchematicsStore::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSchematicsTileBuild.class, SPacketSchematicsTileBuild::encode, SPacketSchematicsTileBuild::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSchematicsTileGet.class, SPacketSchematicsTileGet::encode, SPacketSchematicsTileGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSchematicsTileSave.class, SPacketSchematicsTileSave::encode, SPacketSchematicsTileSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketSchematicsTileSet.class, SPacketSchematicsTileSet::encode, SPacketSchematicsTileSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketScriptGet.class, SPacketScriptGet::encode, SPacketScriptGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTileEntityGet.class, SPacketTileEntityGet::encode, SPacketTileEntityGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTileEntitySave.class, SPacketTileEntitySave::encode, SPacketTileEntitySave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketToolMounter.class, SPacketToolMounter::encode, SPacketToolMounter::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTransportCategoriesGet.class, SPacketTransportCategoriesGet::encode, SPacketTransportCategoriesGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTransportCategoryRemove.class, SPacketTransportCategoryRemove::encode, SPacketTransportCategoryRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTransportCategorySave.class, SPacketTransportCategorySave::encode, SPacketTransportCategorySave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTransportGet.class, SPacketTransportGet::encode, SPacketTransportGet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTransportRemove.class, SPacketTransportRemove::encode, SPacketTransportRemove::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTransportSave.class, SPacketTransportSave::encode, SPacketTransportSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiButton.class, SPacketCustomGuiButton::encode, SPacketCustomGuiButton::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiButtonList.class, SPacketCustomGuiButtonList::encode, SPacketCustomGuiButtonList::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiTextUpdate.class, SPacketCustomGuiTextUpdate::encode, SPacketCustomGuiTextUpdate::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiSliderUpdate.class, SPacketCustomGuiSliderUpdate::encode, SPacketCustomGuiSliderUpdate::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiFocusUpdate.class, SPacketCustomGuiFocusUpdate::encode, SPacketCustomGuiFocusUpdate::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiScrollClick.class, SPacketCustomGuiScrollClick::encode, SPacketCustomGuiScrollClick::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiSubGuiClosed.class, SPacketCustomGuiSubGuiClosed::encode, SPacketCustomGuiSubGuiClosed::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketCustomGuiParts.class, SPacketCustomGuiParts::encode, SPacketCustomGuiParts::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketNpRandomNameSet.class, SPacketNpRandomNameSet::encode, SPacketNpRandomNameSet::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketPlayerSoundPlays.class, SPacketPlayerSoundPlays::encode, SPacketPlayerSoundPlays::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketTraderLimitRefresh.class, SPacketTraderLimitRefresh::encode, SPacketTraderLimitRefresh::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketScriptSave.class, SPacketScriptSave::encode, SPacketScriptSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketToolMobSpawner.class, SPacketToolMobSpawner::encode, SPacketToolMobSpawner::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestSave.class, SPacketQuestSave::encode, SPacketQuestSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketQuestCategorySave.class, SPacketQuestCategorySave::encode, SPacketQuestCategorySave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDialogSave.class, SPacketDialogSave::encode, SPacketDialogSave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketDialogCategorySave.class, SPacketDialogCategorySave::encode, SPacketDialogCategorySave::decode, PacketServerBasic::handle);
        Packets.registerServerPacket(index++, SPacketOpenParts.class, SPacketOpenParts::encode, SPacketOpenParts::decode, PacketServerBasic::handle);
    }

    public static <MSG> void registerServerPacket(int index, Class<MSG> messageType, BiConsumer<MSG, PacketByteBuf> encoder, Function<PacketByteBuf, MSG> decoder, TriConsumer<MSG, MinecraftServer, ServerPlayerEntity> handle) {
        indexes.put(messageType, index);
        encoders.put(messageType, encoder);
        ServerPlayNetworking.registerGlobalReceiver((Identifier)new Identifier("customnpcs", "" + index), (server, player, _handler, buf, _responseSender) -> handle.accept(decoder.apply(buf), server, player));
    }

    public static <MSG> void registerPacket(int index, Class<MSG> messageType, BiConsumer<MSG, PacketByteBuf> encoder, Function<PacketByteBuf, MSG> decoder, Consumer<MSG> handle) {
        indexes.put(messageType, index);
        encoders.put(messageType, encoder);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver((Identifier)new Identifier("customnpcs", "" + index), (client, _handler, buf, _responseSender) -> handle.accept(decoder.apply(buf)));
        }
    }

    public static <MSG> void send(ServerPlayerEntity player, MSG msg) {
        PacketByteBuf ret = new PacketByteBuf(Unpooled.buffer());
        encoders.get(msg.getClass()).accept(msg, ret);
        ServerPlayNetworking.send((ServerPlayerEntity)player, (Identifier)new Identifier("customnpcs", String.valueOf(indexes.get(msg.getClass()))), (PacketByteBuf)ret);
    }

    public static <MSG> void sendNearby(World level, BlockPos pos, int range, MSG msg) {
        for (ServerPlayerEntity player : level.getServer().getPlayerManager().getPlayerList()) {
            if (!(player.squaredDistanceTo((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) <= (double)range) || !player.getWorld().getRegistryKey().equals(level.getRegistryKey())) continue;
            Packets.send(player, msg);
        }
    }

    public static <MSG> void sendNearby(Entity entity, MSG msg) {
        @SuppressWarnings("unchecked")
        Set<EntityTrackingListener> connections = (Set<EntityTrackingListener>)(Set<?>)((ThreadedAnvilChunkStorage.EntityTracker)((ServerChunkManager)entity.getEntityWorld().getChunkManager()).threadedAnvilChunkStorage.entityTrackers.get((int)entity.getId())).listeners;
        for (EntityTrackingListener conn : connections) {
            ServerPlayerEntity player = conn.getPlayer();
            Packets.send(player, msg);
        }
    }

    public static <MSG> void sendAll(MSG msg) {
        for (ServerPlayerEntity player : CustomNpcs.Server.getPlayerManager().getPlayerList()) {
            Packets.send(player, msg);
        }
    }

    public static <MSG> void sendServer(MSG msg) {
        if (msg instanceof Packet) {
            MinecraftClient.getInstance().getNetworkHandler().getConnection().send((Packet)msg);
        } else {
            PacketByteBuf ret = new PacketByteBuf(Unpooled.buffer());
            encoders.get(msg.getClass()).accept(msg, ret);
            ClientPlayNetworking.send((Identifier)new Identifier("customnpcs", String.valueOf(indexes.get(msg.getClass()))), (PacketByteBuf)ret);
        }
    }
}

