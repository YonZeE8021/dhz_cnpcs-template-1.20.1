/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
 *  net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType$ExtendedFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.registry.Registry
 *  net.minecraft.screen.ScreenHandlerType
 *  net.minecraft.registry.Registries
 */
package noppes.npcs;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.registry.Registries;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.containers.ContainerMerchantAdd;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.containers.ContainerNPCBankLarge;
import noppes.npcs.containers.ContainerNPCBankSmall;
import noppes.npcs.containers.ContainerNPCBankUnlock;
import noppes.npcs.containers.ContainerNPCBankUpgrade;
import noppes.npcs.containers.ContainerNPCCompanion;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.containers.ContainerNPCFollowerSetup;
import noppes.npcs.containers.ContainerNPCInv;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.containers.ContainerNPCTraderSetup;
import noppes.npcs.containers.ContainerNpcItemGiver;
import noppes.npcs.containers.ContainerNpcQuestReward;
import noppes.npcs.containers.ContainerNpcQuestTypeItem;

public class CustomContainer {
    public static ScreenHandlerType<ContainerCarpentryBench> container_carpentrybench;
    public static ScreenHandlerType<ContainerCustomGui> container_customgui;
    public static ScreenHandlerType<ContainerMail> container_mail;
    public static ScreenHandlerType<ContainerManageBanks> container_managebanks;
    public static ScreenHandlerType<ContainerManageRecipes> container_managerecipes;
    public static ScreenHandlerType<ContainerMerchantAdd> container_merchantadd;
    public static ScreenHandlerType<ContainerNPCBankInterface> container_banklarge;
    public static ScreenHandlerType<ContainerNPCBankInterface> container_banksmall;
    public static ScreenHandlerType<ContainerNPCBankInterface> container_bankunlock;
    public static ScreenHandlerType<ContainerNPCBankInterface> container_bankupgrade;
    public static ScreenHandlerType<ContainerNPCCompanion> container_companion;
    public static ScreenHandlerType<ContainerNPCFollower> container_follower;
    public static ScreenHandlerType<ContainerNPCFollowerHire> container_followerhire;
    public static ScreenHandlerType<ContainerNPCFollowerSetup> container_followersetup;
    public static ScreenHandlerType<ContainerNPCInv> container_inv;
    public static ScreenHandlerType<ContainerNpcItemGiver> container_itemgiver;
    public static ScreenHandlerType<ContainerNpcQuestReward> container_questreward;
    public static ScreenHandlerType<ContainerNpcQuestTypeItem> container_questtypeitem;
    public static ScreenHandlerType<ContainerNPCTrader> container_trader;
    public static ScreenHandlerType<ContainerNPCTraderSetup> container_tradersetup;

    public static void registerContainers() {
        container_carpentrybench = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_carpentrybench", CustomContainer.createContainer((containerId, inv, data) -> new ContainerCarpentryBench(containerId, inv, data.readBlockPos())));
        container_customgui = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_customgui", CustomContainer.createContainer((containerId, inv, data) -> new ContainerCustomGui(containerId, data.readNbt())));
        container_mail = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_mail", CustomContainer.createContainer((containerId, inv, data) -> new ContainerMail(containerId, inv, data.readBoolean(), data.readBoolean())));
        container_managebanks = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_managebanks", CustomContainer.createContainer((containerId, inv, data) -> new ContainerManageBanks(containerId, inv)));
        container_managerecipes = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_managerecipes", CustomContainer.createContainer((containerId, inv, data) -> {
            data.readInt();
            return new ContainerManageRecipes(containerId, inv, data.readBlockPos().getX());
        }));
        container_merchantadd = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_merchantadd", CustomContainer.createContainer((containerId, inv, data) -> new ContainerMerchantAdd(containerId, inv)));
        container_banklarge = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_banklarge", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCBankLarge(containerId, inv, data.readInt(), data.readInt())));
        container_banksmall = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_banksmall", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCBankSmall(containerId, inv, data.readInt(), data.readInt())));
        container_bankunlock = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_bankunlock", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCBankUnlock(containerId, inv, data.readInt(), data.readInt())));
        container_bankupgrade = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_bankupgrade", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCBankUpgrade(containerId, inv, data.readInt(), data.readInt())));
        container_companion = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_companion", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCCompanion(containerId, inv, data.readInt())));
        container_follower = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_follower", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCFollower(containerId, inv, data.readInt())));
        container_followerhire = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_followerhire", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCFollowerHire(containerId, inv, data.readInt())));
        container_followersetup = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_followersetup", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCFollowerSetup(containerId, inv, data.readInt())));
        container_inv = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_inv", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCInv(containerId, inv, data.readInt())));
        container_itemgiver = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_itemgiver", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNpcItemGiver(containerId, inv, data.readInt())));
        container_questreward = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_questreward", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNpcQuestReward(containerId, inv)));
        container_questtypeitem = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_questtypeitem", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNpcQuestTypeItem(containerId, inv)));
        container_trader = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_trader", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCTrader(containerId, inv, data.readInt())));
        container_tradersetup = (ScreenHandlerType)Registry.register((Registry)Registries.SCREEN_HANDLER, (String)"customnpcs:container_tradersetup", CustomContainer.createContainer((containerId, inv, data) -> new ContainerNPCTraderSetup(containerId, inv, data.readInt())));
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> createContainer(ExtendedScreenHandlerType.ExtendedFactory<T> factoryIn) {
        return new ExtendedScreenHandlerType(factoryIn);
    }
}

