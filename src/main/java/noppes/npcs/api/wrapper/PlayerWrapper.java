/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.advancement.Advancement
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.advancement.AdvancementProgress
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.GameMode
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.AbstractNbtNumber
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.text.MutableText
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.IPlayerSkin;
import noppes.npcs.api.IPos;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.api.wrapper.ContainerWrapper;
import noppes.npcs.api.wrapper.EntityLivingBaseWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.packets.client.PacketHideAllOverlays;
import noppes.npcs.packets.client.PacketOverlayHide;
import noppes.npcs.packets.client.PacketOverlayShow;
import noppes.npcs.packets.client.PacketPlayMusic;
import noppes.npcs.packets.client.PacketPlaySound;
import noppes.npcs.packets.client.PacketSoundGUIOpen;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.util.ValueUtil;

public class PlayerWrapper<T extends ServerPlayerEntity>
extends EntityLivingBaseWrapper<T>
implements IPlayer {
    private IContainer inventory;
    private Object pixelmonPartyStorage;
    private Object pixelmonPCStorage;
    private final IData storeddata = new IData(){

        @Override
        public void put(String key, Object value) {
            NbtCompound compound = this.getStoredCompound();
            if (value instanceof Number) {
                compound.putDouble(key, ((Number)value).doubleValue());
            } else if (value instanceof String) {
                compound.putString(key, (String)value);
            }
        }

        @Override
        public Object get(String key) {
            NbtCompound compound = this.getStoredCompound();
            if (!compound.contains(key)) {
                return null;
            }
            NbtElement base = compound.get(key);
            if (base instanceof AbstractNbtNumber) {
                return ((AbstractNbtNumber)base).doubleValue();
            }
            return base.asString();
        }

        @Override
        public void remove(String key) {
            NbtCompound compound = this.getStoredCompound();
            compound.remove(key);
        }

        @Override
        public boolean has(String key) {
            return this.getStoredCompound().contains(key);
        }

        @Override
        public void clear() {
            PlayerData data = PlayerData.get((PlayerEntity)PlayerWrapper.this.entity);
            data.scriptStoreddata = new NbtCompound();
        }

        private NbtCompound getStoredCompound() {
            PlayerData data = PlayerData.get((PlayerEntity)PlayerWrapper.this.entity);
            return data.scriptStoreddata;
        }

        @Override
        public String[] getKeys() {
            NbtCompound compound = this.getStoredCompound();
            return compound.getKeys().toArray(new String[compound.getKeys().size()]);
        }
    };
    private PlayerData data;

    public PlayerWrapper(T player) {
        super(player);
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public String getName() {
        return ((ServerPlayerEntity)this.entity).getName().getString();
    }

    @Override
    public String getDisplayName() {
        return ((ServerPlayerEntity)this.entity).getDisplayName().getString();
    }

    @Override
    public int getHunger() {
        return ((ServerPlayerEntity)this.entity).getHungerManager().getFoodLevel();
    }

    @Override
    public void setHunger(int level) {
        ((ServerPlayerEntity)this.entity).getHungerManager().setFoodLevel(level);
    }

    @Override
    public boolean hasFinishedQuest(int id) {
        PlayerQuestData data = this.getData().questData;
        return data.finishedQuests.containsKey(id);
    }

    @Override
    public boolean hasActiveQuest(int id) {
        PlayerQuestData data = this.getData().questData;
        return data.activeQuests.containsKey(id);
    }

    @Override
    public IQuest[] getActiveQuests() {
        PlayerQuestData data = this.getData().questData;
        ArrayList<IQuest> quests = new ArrayList<IQuest>();
        for (int id : data.activeQuests.keySet()) {
            IQuest quest = QuestController.instance.quests.get(id);
            if (quest == null) continue;
            quests.add(quest);
        }
        return quests.toArray(new IQuest[quests.size()]);
    }

    @Override
    public IQuest[] getFinishedQuests() {
        PlayerQuestData data = this.getData().questData;
        ArrayList<IQuest> quests = new ArrayList<IQuest>();
        for (int id : data.finishedQuests.keySet()) {
            IQuest quest = QuestController.instance.quests.get(id);
            if (quest == null) continue;
            quests.add(quest);
        }
        return quests.toArray(new IQuest[quests.size()]);
    }

    @Override
    public void startQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        QuestData questdata = new QuestData(quest);
        PlayerData data = this.getData();
        data.questData.activeQuests.put(id, questdata);
        Packets.send((ServerPlayerEntity)this.entity, new PacketAchievement((Text)Text.translatable((String)"quest.newquest"), (Text)Text.translatable((String)quest.title), 2));
        MutableText text = Text.translatable((String)"quest.newquest").append(":").append((Text)Text.translatable((String)quest.title));
        Packets.send((ServerPlayerEntity)this.entity, new PacketChat((Text)text));
        data.updateClient = true;
    }

    @Override
    public void sendNotification(String title, String msg, int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Wrong type value given " + type, new Object[0]);
        }
        Packets.send((ServerPlayerEntity)this.entity, new PacketAchievement((Text)Text.translatable((String)title), (Text)Text.translatable((String)msg), type));
    }

    @Override
    public void finishQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        PlayerData data = this.getData();
        data.questData.finishedQuests.put(id, System.currentTimeMillis());
        data.updateClient = true;
    }

    @Override
    public void stopQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        PlayerData data = this.getData();
        data.questData.activeQuests.remove(id);
        data.updateClient = true;
    }

    @Override
    public void removeQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        PlayerData data = this.getData();
        data.questData.activeQuests.remove(id);
        data.questData.finishedQuests.remove(id);
        data.updateClient = true;
    }

    @Override
    public boolean hasReadDialog(int id) {
        PlayerDialogData data = this.getData().dialogData;
        return data.dialogsRead.contains(id);
    }

    @Override
    public void showDialog(int id, String name) {
        Dialog dialog = DialogController.instance.dialogs.get(id);
        if (dialog == null) {
            throw new CustomNPCsException("Unknown Dialog id: " + id, new Object[0]);
        }
        if (!dialog.availability.isAvailable((PlayerEntity)this.entity)) {
            return;
        }
        EntityDialogNpc npc = new EntityDialogNpc((World)this.getWorld().getMCLevel());
        npc.display.setName(name);
        EntityUtil.Copy((LivingEntity)this.entity, (LivingEntity)npc);
        DialogOption option = new DialogOption();
        option.dialogId = id;
        option.title = dialog.title;
        npc.dialogs.put(0, option);
        NoppesUtilServer.openDialog((PlayerEntity)this.entity, npc, dialog);
    }

    @Override
    public void addFactionPoints(int faction, int points) {
        PlayerData data = this.getData();
        data.factionData.increasePoints((PlayerEntity)this.entity, faction, points);
        data.updateClient = true;
    }

    @Override
    public int getFactionPoints(int faction) {
        return this.getData().factionData.getFactionPoints((PlayerEntity)this.entity, faction);
    }

    @Override
    public float getRotation() {
        return ((ServerPlayerEntity)this.entity).getYaw();
    }

    @Override
    public void setRotation(float rotation) {
        ((ServerPlayerEntity)this.entity).setYaw(rotation);
    }

    @Override
    public void message(String message) {
        ((ServerPlayerEntity)this.entity).sendMessage((Text)Text.translatable((String)NoppesStringUtils.formatText(message, this.entity)));
    }

    @Override
    public int getGamemode() {
        return ((ServerPlayerEntity)this.entity).interactionManager.getGameMode().getId();
    }

    @Override
    public void setGamemode(int type) {
        ((ServerPlayerEntity)this.entity).changeGameMode(GameMode.byId((int)type));
    }

    @Override
    public int inventoryItemCount(IItemStack item) {
        int count = 0;
        for (int i = 0; i < ((ServerPlayerEntity)this.entity).getInventory().size(); ++i) {
            ItemStack is = ((ServerPlayerEntity)this.entity).getInventory().getStack(i);
            if (is == null || !this.isItemEqual(item.getMCItemStack(), is)) continue;
            count += is.getCount();
        }
        return count;
    }

    private boolean isItemEqual(ItemStack stack, ItemStack other) {
        if (other.isEmpty()) {
            return false;
        }
        return stack.getItem() == other.getItem();
    }

    @Override
    public int inventoryItemCount(String id) {
        Item item = (Item)Registries.ITEM.get(new Identifier(id));
        if (item == null) {
            throw new CustomNPCsException("Unknown item id: " + id, new Object[0]);
        }
        return this.inventoryItemCount(NpcAPI.Instance().getIItemStack(new ItemStack((ItemConvertible)item, 1)));
    }

    @Override
    public IContainer getInventory() {
        if (this.inventory == null) {
            this.inventory = new ContainerWrapper((Inventory)((ServerPlayerEntity)this.entity).getInventory());
        }
        return this.inventory;
    }

    @Override
    public IItemStack getInventoryHeldItem() {
        return NpcAPI.Instance().getIItemStack(((ServerPlayerEntity)this.entity).currentScreenHandler.getCursorStack());
    }

    @Override
    public boolean removeItem(IItemStack item, int amount) {
        int count = this.inventoryItemCount(item);
        if (amount > count) {
            return false;
        }
        if (count == amount) {
            this.removeAllItems(item);
        } else {
            for (int i = 0; i < ((ServerPlayerEntity)this.entity).getInventory().size(); ++i) {
                ItemStack is = ((ServerPlayerEntity)this.entity).getInventory().getStack(i);
                if (is == null || !this.isItemEqual(item.getMCItemStack(), is)) continue;
                if (amount >= is.getCount()) {
                    ((ServerPlayerEntity)this.entity).getInventory().setStack(i, ItemStack.EMPTY);
                    amount -= is.getCount();
                    continue;
                }
                is.split(amount);
                break;
            }
        }
        this.updatePlayerInventory();
        return true;
    }

    @Override
    public boolean removeItem(String id, int amount) {
        Item item = (Item)Registries.ITEM.get(new Identifier(id));
        if (item == null) {
            throw new CustomNPCsException("Unknown item id: " + id, new Object[0]);
        }
        return this.removeItem(NpcAPI.Instance().getIItemStack(new ItemStack((ItemConvertible)item, 1)), amount);
    }

    @Override
    public boolean giveItem(IItemStack item) {
        ItemStack mcItem = item.getMCItemStack();
        if (mcItem.isEmpty()) {
            return false;
        }
        boolean bo = ((ServerPlayerEntity)this.entity).getInventory().insertStack(mcItem.copy());
        if (bo) {
            NoppesUtilServer.playSound((LivingEntity)this.entity, SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, ((((ServerPlayerEntity)this.entity).getRandom().nextFloat() - ((ServerPlayerEntity)this.entity).getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            this.updatePlayerInventory();
        }
        return bo;
    }

    @Override
    public boolean giveItem(String id, int amount) {
        Item item = (Item)Registries.ITEM.get(new Identifier(id));
        if (item == null) {
            return false;
        }
        ItemStack mcStack = new ItemStack((ItemConvertible)item);
        IItemStack itemStack = NpcAPI.Instance().getIItemStack(mcStack);
        itemStack.setStackSize(amount);
        return this.giveItem(itemStack);
    }

    @Override
    public void updatePlayerInventory() {
        ((ServerPlayerEntity)this.entity).playerScreenHandler.sendContentUpdates();
        ((ServerPlayerEntity)this.entity).networkHandler.sendPacket((Packet)new ScreenHandlerSlotUpdateS2CPacket(-2, 0, ((ServerPlayerEntity)this.entity).getInventory().selectedSlot, ((ServerPlayerEntity)this.entity).getInventory().getStack(((ServerPlayerEntity)this.entity).getInventory().selectedSlot)));
        PlayerQuestData playerdata = this.getData().questData;
        playerdata.checkQuestCompletion((PlayerEntity)this.entity, 0);
    }

    @Override
    public IBlock getSpawnPoint() {
        BlockPos pos = ((ServerPlayerEntity)this.entity).getSleepingPosition().orElse(null);
        if (pos == null) {
            return this.getWorld().getSpawnPoint();
        }
        return NpcAPI.Instance().getIBlock(((ServerPlayerEntity)this.entity).getWorld(), pos);
    }

    @Override
    public void setSpawnPoint(IBlock block) {
        this.setSpawnpoint(block.getX(), block.getY(), block.getZ());
    }

    @Override
    public void setSpawnpoint(int x, int y, int z) {
        x = ValueUtil.CorrectInt(x, -30000000, 30000000);
        z = ValueUtil.CorrectInt(z, -30000000, 30000000);
        y = ValueUtil.CorrectInt(y, 0, 256);
        ((ServerPlayerEntity)this.entity).setSpawnPoint(this.getWorld().getMCLevel().getRegistryKey(), new BlockPos(x, y, z), 0.0f, true, false);
    }

    @Override
    public void resetSpawnpoint() {
        ((ServerPlayerEntity)this.entity).setSpawnPoint(this.getWorld().getMCLevel().getRegistryKey(), null, 0.0f, true, false);
    }

    @Override
    public void removeAllItems(IItemStack item) {
        for (int i = 0; i < ((ServerPlayerEntity)this.entity).getInventory().size(); ++i) {
            ItemStack is = ((ServerPlayerEntity)this.entity).getInventory().getStack(i);
            if (is == null || !ItemStack.areItemsEqual((ItemStack)is, (ItemStack)item.getMCItemStack())) continue;
            ((ServerPlayerEntity)this.entity).getInventory().setStack(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean hasAdvancement(String achievement) {
        Advancement advancement = ((ServerPlayerEntity)this.entity).getServer().getAdvancementLoader().get(new Identifier(achievement));
        if (advancement == null) {
            throw new CustomNPCsException("Advancement doesnt exist", new Object[0]);
        }
        AdvancementProgress progress = ((ServerPlayerEntity)this.entity).getServer().getPlayerManager().getAdvancementTracker((ServerPlayerEntity)this.entity).getProgress(advancement);
        return progress.isDone();
    }

    @Override
    public int getExpLevel() {
        return ((ServerPlayerEntity)this.entity).experienceLevel;
    }

    @Override
    public void setExpLevel(int level) {
        ((ServerPlayerEntity)this.entity).addExperienceLevels(level - ((ServerPlayerEntity)this.entity).experienceLevel);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        SPacketDimensionTeleport.teleportPlayer((ServerPlayerEntity)this.entity, x, y, z, (RegistryKey<World>)((ServerPlayerEntity)this.entity).getWorld().getRegistryKey());
    }

    @Override
    public void setPos(IPos pos) {
        SPacketDimensionTeleport.teleportPlayer((ServerPlayerEntity)this.entity, pos.getX(), pos.getY(), pos.getZ(), (RegistryKey<World>)((ServerPlayerEntity)this.entity).getWorld().getRegistryKey());
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean typeOf(int type) {
        return type == 1 ? true : super.typeOf(type);
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public IPixelmonPlayerData getPixelmonData() {
        if (!PixelmonHelper.Enabled) {
            throw new CustomNPCsException("Pixelmon isnt installed", new Object[0]);
        }
        return new IPixelmonPlayerData(){

            @Override
            public Object getParty() {
                if (PlayerWrapper.this.pixelmonPartyStorage == null) {
                    PlayerWrapper.this.pixelmonPartyStorage = PixelmonHelper.getParty((PlayerEntity)PlayerWrapper.this.entity);
                }
                return PlayerWrapper.this.pixelmonPartyStorage;
            }

            @Override
            public Object getPC() {
                if (PlayerWrapper.this.pixelmonPCStorage == null) {
                    PlayerWrapper.this.pixelmonPCStorage = PixelmonHelper.getPc((PlayerEntity)PlayerWrapper.this.entity);
                }
                return PlayerWrapper.this.pixelmonPCStorage;
            }
        };
    }

    private PlayerData getData() {
        if (this.data == null) {
            this.data = PlayerData.get((PlayerEntity)this.entity);
        }
        return this.data;
    }

    @Override
    public ITimers getTimers() {
        return this.getData().timers;
    }

    @Override
    public void removeDialog(int id) {
        PlayerData data = this.getData();
        data.dialogData.dialogsRead.remove(id);
        data.updateClient = true;
    }

    @Override
    public void addDialog(int id) {
        PlayerData data = this.getData();
        data.dialogData.dialogsRead.add(id);
        data.updateClient = true;
    }

    @Override
    public void closeGui() {
        ((ServerPlayerEntity)this.entity).closeHandledScreen();
        Packets.send((ServerPlayerEntity)this.entity, new PacketGuiClose(new NbtCompound()));
    }

    @Override
    public int factionStatus(int factionId) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null) {
            throw new CustomNPCsException("Unknown faction: " + factionId, new Object[0]);
        }
        return faction.playerStatus(this);
    }

    @Override
    public void kick(String message) {
        ((ServerPlayerEntity)this.entity).networkHandler.disconnect((Text)Text.translatable((String)message));
    }

    @Override
    public boolean canQuestBeAccepted(int questId) {
        return PlayerQuestController.canQuestBeAccepted((PlayerEntity)this.entity, questId);
    }

    @Override
    public void showCustomGui(ICustomGui gui) {
        NoppesUtilServer.openContainerGui((ServerPlayerEntity)this.getMCEntity(), EnumGuiType.CustomGui, buf -> buf.writeNbt(((CustomGuiWrapper)gui).toNBT()));
        ((ContainerCustomGui)((ServerPlayerEntity)this.getMCEntity()).currentScreenHandler).setGui((CustomGuiWrapper)gui, (PlayerEntity)this.entity);
    }

    @Override
    public ICustomGui getCustomGui() {
        if (((ServerPlayerEntity)this.entity).currentScreenHandler instanceof ContainerCustomGui) {
            return ((ContainerCustomGui)((ServerPlayerEntity)this.entity).currentScreenHandler).customGui;
        }
        return null;
    }

    @Override
    public void clearData() {
        PlayerData data = this.getData();
        data.setNBT(new NbtCompound());
        data.save(true);
    }

    @Override
    public IContainer getOpenContainer() {
        return NpcAPI.Instance().getIContainer(((ServerPlayerEntity)this.entity).currentScreenHandler);
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        BlockPos pos = ((ServerPlayerEntity)this.entity).getBlockPos();
        Packets.send((ServerPlayerEntity)this.entity, new PacketPlaySound(sound, pos, volume, pitch));
    }

    @Override
    public void playMusic(String sound, boolean background, boolean loops) {
        Packets.send((ServerPlayerEntity)this.entity, new PacketPlayMusic(sound, !background, loops));
    }

    @Override
    public void sendMail(IPlayerMail mail) {
        PlayerData data = this.getData();
        data.mailData.playermail.add(((PlayerMail)mail).copy());
        data.save(false);
    }

    @Override
    public void trigger(int id, Object ... arguments) {
        EventHooks.onScriptTriggerEvent(PlayerData.get((PlayerEntity)((PlayerEntity)this.entity)).scriptData, id, this.getWorld(), this.getPos(), null, arguments);
    }

    @Override
    public void showOverlay(IOverlay overlay) {
        Packets.send((ServerPlayerEntity)this.entity, new PacketOverlayShow(overlay.toNbt()));
    }

    @Override
    public void showSoundSelectionGUI() {
        Packets.send((ServerPlayerEntity)this.entity, new PacketSoundGUIOpen());
    }

    @Override
    public void hideOverlay(int id) {
        Packets.send((ServerPlayerEntity)this.entity, new PacketOverlayHide(id));
    }

    @Override
    public void hideAllOverlays() {
        Packets.send((ServerPlayerEntity)this.entity, new PacketHideAllOverlays(true));
    }

    @Override
    public IPlayerSkin getSkin() {
        return PlayerData.get((PlayerEntity)((PlayerEntity)this.entity)).skinData;
    }
}

