/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuClose;
import noppes.npcs.packets.server.SPacketNpcDelete;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiNpcMenu {
    private IGuiInterface parent;
    private GuiMenuTopButton[] topButtons = new GuiMenuTopButton[0];
    private int activeMenu;
    private EntityNPCInterface npc;

    public GuiNpcMenu(IGuiInterface parent, int activeMenu, EntityNPCInterface npc) {
        this.parent = parent;
        this.activeMenu = activeMenu;
        this.npc = npc;
    }

    public void initGui(int guiLeft, int guiTop, int width) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        GuiMenuTopButton display = new GuiMenuTopButton(this.parent, 1, guiLeft + 4, guiTop - 17, "menu.display"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.save();
                GuiNpcMenu.this.activeMenu = 1;
                CustomNpcs.proxy.openGui(GuiNpcMenu.this.npc, EnumGuiType.MainMenuDisplay);
            }
        };
        GuiMenuTopButton stats = new GuiMenuTopButton(this.parent, 2, display.getX() + display.getWidth(), guiTop - 17, "menu.stats"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.save();
                GuiNpcMenu.this.activeMenu = 2;
                CustomNpcs.proxy.openGui(GuiNpcMenu.this.npc, EnumGuiType.MainMenuStats);
            }
        };
        GuiMenuTopButton ai = new GuiMenuTopButton(this.parent, 3, stats.getX() + stats.getWidth(), guiTop - 17, "menu.ai"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.save();
                GuiNpcMenu.this.activeMenu = 3;
                CustomNpcs.proxy.openGui(GuiNpcMenu.this.npc, EnumGuiType.MainMenuAI);
            }
        };
        GuiMenuTopButton inv = new GuiMenuTopButton(this.parent, 4, ai.getX() + ai.getWidth(), guiTop - 17, "menu.inventory"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.save();
                GuiNpcMenu.this.activeMenu = 4;
                NoppesUtil.requestOpenGUI(EnumGuiType.MainMenuInv);
            }
        };
        GuiMenuTopButton advanced = new GuiMenuTopButton(this.parent, 5, inv.getX() + inv.getWidth(), guiTop - 17, "menu.advanced"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.save();
                GuiNpcMenu.this.activeMenu = 5;
                CustomNpcs.proxy.openGui(GuiNpcMenu.this.npc, EnumGuiType.MainMenuAdvanced);
            }
        };
        GuiMenuTopButton global = new GuiMenuTopButton(this.parent, 6, advanced.getX() + advanced.getWidth(), guiTop - 17, "menu.global"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.save();
                GuiNpcMenu.this.activeMenu = 6;
                CustomNpcs.proxy.openGui(GuiNpcMenu.this.npc, EnumGuiType.MainMenuGlobal);
            }
        };
        GuiMenuTopButton close = new GuiMenuTopButton(this.parent, 0, guiLeft + width - 22, guiTop - 17, "X"){

            @Override
            public void onClick(double x, double y) {
                GuiNpcMenu.this.close();
            }
        };
        GuiMenuTopButton delete = new GuiMenuTopButton(this.parent, 66, guiLeft + width - 72, guiTop - 17, "selectServer.delete"){

            @Override
            public void onClick(double x, double y) {
                ConfirmScreen guiyesno = new ConfirmScreen(GuiNpcMenu.this::accept, (Text)Text.translatable((String)""), (Text)Text.translatable((String)"gui.deleteMessage", (Object[])new Object[]{GuiNpcMenu.this.npc.getDisplayName().getString()}));
                mc.setScreen((Screen)guiyesno);
            }
        };
        delete.setX(close.getX() - delete.getWidth());
        for (GuiMenuTopButton button : this.topButtons = new GuiMenuTopButton[]{display, stats, ai, inv, advanced, global, close, delete}) {
            button.active = button.id == this.activeMenu;
        }
    }

    private void save() {
        GuiTextFieldNop.unfocus();
        this.parent.save();
    }

    private void close() {
        ((Screen)this.parent).close();
        if (this.npc != null) {
            this.npc.reset();
            Packets.sendServer(new SPacketMenuClose());
        }
    }

    public boolean mouseClicked(double i, double j, int k) {
        if (k == 0) {
            MinecraftClient mc = MinecraftClient.getInstance();
            for (GuiMenuTopButton button : this.topButtons) {
                if (!button.mouseClicked(i, j, k)) continue;
                return true;
            }
        }
        return false;
    }

    public void drawElements(DrawContext graphics, TextRenderer font, int i, int j, MinecraftClient mc, float f) {
        for (GuiMenuTopButton button : this.topButtons) {
            button.render(graphics, i, j, f);
        }
    }

    public void accept(boolean flag) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (flag) {
            Packets.sendServer(new SPacketNpcDelete());
            mc.setScreen(null);
            mc.mouse.lockCursor();
        } else {
            NoppesUtil.openGUI((PlayerEntity)mc.player, this.parent);
        }
    }
}

