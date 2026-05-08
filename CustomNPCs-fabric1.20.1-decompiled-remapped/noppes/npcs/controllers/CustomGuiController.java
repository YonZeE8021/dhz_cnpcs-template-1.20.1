/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package noppes.npcs.controllers;

import net.minecraft.entity.player.PlayerEntity;
import noppes.npcs.api.event.CustomGuiEvent;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.containers.ContainerCustomGui;

public class CustomGuiController {
    static boolean checkGui(CustomGuiEvent event) {
        Object player = event.player.getMCEntity();
        if (!(((PlayerEntity)player).currentScreenHandler instanceof ContainerCustomGui)) {
            return false;
        }
        return ((ContainerCustomGui)((PlayerEntity)player).currentScreenHandler).customGui.getID() == event.gui.getID();
    }

    public static void onButton(CustomGuiEvent.ButtonEvent event) {
        Object player = event.player.getMCEntity();
        if (CustomGuiController.checkGui(event) && CustomGuiController.getOpenGui(player).getScriptHandler() != null) {
            ((CustomGuiWrapper)event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_BUTTON, event);
        }
    }

    public static void onQuickCraft(CustomGuiEvent.SlotEvent event) {
        Object player = event.player.getMCEntity();
        if (CustomGuiController.checkGui(event) && CustomGuiController.getOpenGui(player).getScriptHandler() != null) {
            ((CustomGuiWrapper)event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SLOT, event);
        }
    }

    public static void onScrollClick(CustomGuiEvent.ScrollEvent event) {
        Object player = event.player.getMCEntity();
        if (CustomGuiController.checkGui(event) && CustomGuiController.getOpenGui(player).getScriptHandler() != null) {
            ((CustomGuiWrapper)event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SCROLL, event);
        }
    }

    public static boolean onSlotClick(CustomGuiEvent.SlotClickEvent event) {
        Object player = event.player.getMCEntity();
        if (CustomGuiController.checkGui(event) && CustomGuiController.getOpenGui(player).getScriptHandler() != null) {
            ((CustomGuiWrapper)event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SLOT_CLICKED, event);
        }
        return true;
    }

    public static void onClose(CustomGuiEvent.CloseEvent event) {
        Object player = event.player.getMCEntity();
        if (CustomGuiController.checkGui(event) && CustomGuiController.getOpenGui(player).getScriptHandler() != null) {
            ((CustomGuiWrapper)event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_CLOSED, event);
        }
    }

    public static CustomGuiWrapper getOpenGui(PlayerEntity player) {
        if (player.currentScreenHandler instanceof ContainerCustomGui) {
            return ((ContainerCustomGui)player.currentScreenHandler).customGui;
        }
        return null;
    }
}

