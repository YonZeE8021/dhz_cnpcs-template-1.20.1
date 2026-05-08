/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.HandledScreens
 */
package noppes.npcs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.client.CustomKeybinds;
import noppes.npcs.client.CustomRenderers;
import noppes.npcs.client.OverlayEventHandler;
import noppes.npcs.client.VersionChecker;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.gui.GuiMerchantAdd;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.global.GuiNPCManageBanks;
import noppes.npcs.client.gui.global.GuiNpcManageRecipes;
import noppes.npcs.client.gui.global.GuiNpcQuestReward;
import noppes.npcs.client.gui.mainmenu.GuiNPCInv;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.client.gui.player.GuiNPCTrader;
import noppes.npcs.client.gui.player.GuiNpcCarpentryBench;
import noppes.npcs.client.gui.player.GuiNpcFollower;
import noppes.npcs.client.gui.player.GuiNpcFollowerHire;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem;
import noppes.npcs.client.gui.roles.GuiNpcFollowerSetup;
import noppes.npcs.client.gui.roles.GuiNpcItemGiver;
import noppes.npcs.client.gui.roles.GuiNpcTraderSetup;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.PixelmonHelper;

public class CustomNpcsClient
implements ClientModInitializer {
    public void onInitializeClient() {
        ClientProxy.createFolders();
        CustomRenderers.registerEntityRenderer();
        CustomKeybinds.registerKeys();
        HandledScreens.register(CustomContainer.container_carpentrybench, GuiNpcCarpentryBench::new);
        HandledScreens.register(CustomContainer.container_customgui, (ContainerCustomGui container, PlayerInventory inv, Text title) -> {
            GuiCustom gui = new GuiCustom(container, inv, title);
            gui.setGuiData(container.data);
            return gui;
        });
        HandledScreens.register(CustomContainer.container_mail, GuiMailmanWrite::new);
        HandledScreens.register(CustomContainer.container_managebanks, GuiNPCManageBanks::new);
        HandledScreens.register(CustomContainer.container_managerecipes, GuiNpcManageRecipes::new);
        HandledScreens.register(CustomContainer.container_merchantadd, GuiMerchantAdd::new);
        HandledScreens.register(CustomContainer.container_banklarge, GuiNPCBankChest::new);
        HandledScreens.register(CustomContainer.container_banksmall, GuiNPCBankChest::new);
        HandledScreens.register(CustomContainer.container_bankunlock, GuiNPCBankChest::new);
        HandledScreens.register(CustomContainer.container_bankupgrade, GuiNPCBankChest::new);
        HandledScreens.register(CustomContainer.container_companion, GuiNpcCompanionInv::new);
        HandledScreens.register(CustomContainer.container_follower, GuiNpcFollower::new);
        HandledScreens.register(CustomContainer.container_followerhire, GuiNpcFollowerHire::new);
        HandledScreens.register(CustomContainer.container_followersetup, GuiNpcFollowerSetup::new);
        HandledScreens.register(CustomContainer.container_inv, GuiNPCInv::new);
        HandledScreens.register(CustomContainer.container_itemgiver, GuiNpcItemGiver::new);
        HandledScreens.register(CustomContainer.container_questreward, GuiNpcQuestReward::new);
        HandledScreens.register(CustomContainer.container_questtypeitem, GuiNpcQuestTypeItem::new);
        HandledScreens.register(CustomContainer.container_trader, GuiNPCTrader::new);
        HandledScreens.register(CustomContainer.container_tradersetup, GuiNpcTraderSetup::new);
        new MusicController();
        HudRenderCallback.EVENT.register(new OverlayEventHandler());
        ClientTickEvents.START_CLIENT_TICK.register(new ClientTickHandler());
        ScreenEvents.AFTER_INIT.register(new ClientEventHandler());
        MinecraftClient mc = MinecraftClient.getInstance();
        new PresetController(CustomNpcs.Dir);
        if (CustomNpcs.EnableUpdateChecker) {
            VersionChecker checker = new VersionChecker();
            checker.start();
        }
        PixelmonHelper.loadClient();
    }
}

