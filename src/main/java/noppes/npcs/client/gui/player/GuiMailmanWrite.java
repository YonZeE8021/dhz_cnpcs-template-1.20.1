/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Formatting
 *  net.minecraft.SharedConstants
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.SelectionManager
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerMailDelete;
import noppes.npcs.packets.server.SPacketPlayerMailSend;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;
import noppes.npcs.shared.client.gui.listeners.IGuiError;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

@Environment(value=EnvType.CLIENT)
public class GuiMailmanWrite
extends GuiContainerNPCInterface<ContainerMail>
implements ITextfieldListener,
IGuiError,
IGuiClose {
    private static final Identifier bookGuiTextures = new Identifier("textures/gui/book.png");
    private static final Identifier bookWidgets = new Identifier("textures/gui/widgets.png");
    private static final Identifier bookInventory = new Identifier("textures/gui/container/inventory.png");
    private final SelectionManager pageEdit = new SelectionManager(this::getText, this::setText, this::getClipboard, this::setClipboard, p_238774_1_ -> p_238774_1_.length() < 1024 && this.textRenderer.getWrappedLinesHeight(p_238774_1_, 114) <= 128);
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int bookTotalPages = 1;
    private int currPage;
    private NbtList bookPages;
    private GuiButtonNextPage buttonNextPage;
    private GuiButtonNextPage buttonPreviousPage;
    private final boolean canEdit;
    private final boolean canSend;
    private boolean hasSend = false;
    public static Screen parent;
    public static PlayerMail mail;
    private MinecraftClient mc = MinecraftClient.getInstance();
    private String username = "";
    private GuiLabel error;

    public GuiMailmanWrite(ContainerMail container, PlayerInventory inv, Text titleIn) {
        super(null, container, inv, titleIn);
        this.title = "";
        this.canEdit = container.canEdit;
        this.canSend = container.canSend;
        if (GuiMailmanWrite.mail.message.contains("pages")) {
            this.bookPages = GuiMailmanWrite.mail.message.getList("pages", 8);
        }
        if (this.bookPages != null) {
            this.bookPages = this.bookPages.copy();
            this.bookTotalPages = this.bookPages.size();
            if (this.bookTotalPages < 1) {
                this.bookTotalPages = 1;
            }
        } else {
            this.bookPages = new NbtList();
            this.bookPages.add(NbtString.of((String)""));
            this.bookTotalPages = 1;
        }
        this.backgroundWidth = 360;
        this.backgroundHeight = 260;
        this.drawDefaultBackground = false;
    }

    @Override
    public void handledScreenTick() {
        ++this.updateCount;
    }

    @Override
    public void init() {
        super.init();
        this.drawables.clear();
        if (this.canEdit && !this.canSend) {
            this.addLabel(new GuiLabel(0, "mailbox.sender", this.guiLeft + 170, this.guiTop + 32, 0));
        } else {
            this.addLabel(new GuiLabel(0, "mailbox.username", this.guiLeft + 170, this.guiTop + 32, 0));
        }
        if (this.canEdit && !this.canSend) {
            this.addTextField(new GuiTextFieldNop(2, (Screen)this, this.guiLeft + 170, this.guiTop + 42, 114, 20, GuiMailmanWrite.mail.sender));
        } else if (this.canEdit) {
            this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 170, this.guiTop + 42, 114, 20, this.username));
        } else {
            this.addLabel(new GuiLabel(10, GuiMailmanWrite.mail.sender, this.guiLeft + 170, this.guiTop + 42, 0));
        }
        this.addLabel(new GuiLabel(1, "mailbox.subject", this.guiLeft + 170, this.guiTop + 72, 0));
        if (this.canEdit) {
            this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 170, this.guiTop + 82, 114, 20, GuiMailmanWrite.mail.subject));
        } else {
            this.addLabel(new GuiLabel(11, GuiMailmanWrite.mail.subject, this.guiLeft + 170, this.guiTop + 82, 0));
        }
        this.error = new GuiLabel(2, "", this.guiLeft + 170, this.guiTop + 114, 0xFF0000);
        this.addLabel(this.error);
        if (this.canEdit && !this.canSend) {
            this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 200, this.guiTop + 171, 60, 20, "gui.done"));
        } else if (this.canEdit) {
            this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 200, this.guiTop + 171, 60, 20, "mailbox.send"));
        }
        if (!this.canEdit && !this.canSend) {
            this.addButton(new GuiButtonNop(this, 4, this.guiLeft + 200, this.guiTop + 171, 60, 20, "selectServer.delete"));
        }
        if (!this.canEdit || this.canSend) {
            this.addButton(new GuiButtonNop(this, 3, this.guiLeft + 200, this.guiTop + 194, 60, 20, "gui.cancel"));
        }
        this.buttonNextPage = new GuiButtonNextPage((IGuiInterface)this, 1, this.guiLeft + 120, this.guiTop + 156, true, b -> {
            if (this.currPage < this.bookTotalPages - 1) {
                ++this.currPage;
            } else if (this.canEdit) {
                this.addNewPage();
                if (this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                }
            }
            this.updateButtons();
        });
        this.addButton(this.buttonNextPage);
        this.buttonPreviousPage = new GuiButtonNextPage((IGuiInterface)this, 2, this.guiLeft + 38, this.guiTop + 156, false, b -> {
            if (this.currPage > 0) {
                --this.currPage;
            }
            this.updateButtons();
        });
        this.addButton(this.buttonPreviousPage);
        this.updateButtons();
    }

    private void updateButtons() {
        this.buttonNextPage.visible = this.currPage < this.bookTotalPages - 1 || this.canEdit;
        this.buttonPreviousPage.visible = this.currPage > 0;
    }

    @Override
    public void buttonEvent(GuiButtonNop par1GuiButton) {
        if (par1GuiButton.active) {
            int id = par1GuiButton.id;
            if (id == 0) {
                GuiMailmanWrite.mail.message.put("pages", (NbtElement)this.bookPages);
                if (this.canSend) {
                    if (!this.hasSend) {
                        this.hasSend = true;
                        Packets.sendServer(new SPacketPlayerMailSend(this.username, mail.writeNBT()));
                    }
                } else {
                    this.close();
                }
            }
            if (id == 3) {
                this.close();
            }
            if (id == 4) {
                ConfirmScreen guiyesno = new ConfirmScreen(flag -> {
                    if (flag) {
                        Packets.sendServer(new SPacketPlayerMailDelete(GuiMailmanWrite.mail.time, GuiMailmanWrite.mail.sender));
                        this.close();
                    } else {
                        NoppesUtil.openGUI((PlayerEntity)this.player, this);
                    }
                }, (Text)Text.literal((String)""), (Text)Text.translatable((String)I18n.translate((String)"gui.deleteMessage", (Object[])new Object[0])));
                this.setScreen((Screen)guiyesno);
            }
            this.updateButtons();
        }
    }

    private void addNewPage() {
        if (this.bookPages != null && this.bookPages.size() < 50) {
            this.bookPages.add(NbtString.of((String)""));
            ++this.bookTotalPages;
        }
    }

    @Override
    public boolean charTyped(char par1, int limbSwingAmount) {
        if (!GuiTextFieldNop.isAnyActive() && this.canEdit) {
            if (SharedConstants.isValidChar((char)par1)) {
                this.pageEdit.insert(Character.toString(par1));
                return true;
            }
        } else {
            super.charTyped(par1, limbSwingAmount);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
            return true;
        }
        boolean flag = this.bookKeyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
        return flag;
    }

    private boolean bookKeyPressed(int p_214230_1_, int p_214230_2_, int p_214230_3_) {
        if (Screen.isSelectAll((int)p_214230_1_)) {
            this.pageEdit.selectAll();
            return true;
        }
        if (Screen.isCopy((int)p_214230_1_)) {
            this.pageEdit.copy();
            return true;
        }
        if (Screen.isPaste((int)p_214230_1_)) {
            this.pageEdit.paste();
            return true;
        }
        if (Screen.isCut((int)p_214230_1_)) {
            this.pageEdit.cut();
            return true;
        }
        switch (p_214230_1_) {
            case 257: 
            case 335: {
                this.pageEdit.insert("\n");
                return true;
            }
            case 259: {
                this.pageEdit.delete(-1);
                return true;
            }
            case 261: {
                this.pageEdit.delete(1);
                return true;
            }
            case 262: {
                this.pageEdit.moveCursor(1, Screen.hasShiftDown());
                return true;
            }
            case 263: {
                this.pageEdit.moveCursor(-1, Screen.hasShiftDown());
                return true;
            }
            case 266: {
                this.buttonPreviousPage.onPress();
                return true;
            }
            case 267: {
                this.buttonNextPage.onPress();
                return true;
            }
        }
        return false;
    }

    private String getText() {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
            return this.bookPages.getString(this.currPage);
        }
        return "";
    }

    private void setText(String par1Str) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
            this.bookPages.set(this.currPage, (NbtElement)NbtString.of((String)par1Str));
        }
    }

    private void setClipboard(String p_238760_1_) {
        if (this.client != null) {
            SelectionManager.setClipboard((MinecraftClient)this.client, (String)p_238760_1_);
        }
    }

    private String getClipboard() {
        return this.client != null ? SelectionManager.getClipboard((MinecraftClient)this.client) : "";
    }

    private void append(String par1Str) {
        String s1 = this.getText();
        String s2 = s1 + par1Str;
        int i = this.mc.textRenderer.getWrappedLinesHeight(s2 + String.valueOf(Formatting.BLACK) + "_", 118);
        if (i <= 118 && s2.length() < 256) {
            this.setText(s2);
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float par3) {
        super.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)bookGuiTextures);
        graphics.drawTexture(bookGuiTextures, this.guiLeft + 130, this.guiTop + 22, 0, 0, this.bookImageWidth, this.bookImageHeight / 3);
        graphics.drawTexture(bookGuiTextures, this.guiLeft + 130, this.guiTop + 22 + this.bookImageHeight / 3, 0, this.bookImageHeight / 2, this.bookImageWidth, this.bookImageHeight / 2);
        graphics.drawTexture(bookGuiTextures, this.guiLeft, this.guiTop + 2, 0, 0, this.bookImageWidth, this.bookImageHeight);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)bookInventory);
        graphics.drawTexture(bookInventory, this.guiLeft + 20, this.guiTop + 173, 0, 82, 180, 55);
        graphics.drawTexture(bookInventory, this.guiLeft + 20, this.guiTop + 228, 0, 140, 180, 28);
        String s = I18n.translate((String)"book.pageIndicator", (Object[])new Object[]{this.currPage + 1, this.bookTotalPages});
        Object s1 = "";
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
            s1 = this.bookPages.getString(this.currPage);
        }
        if (this.canEdit) {
            s1 = this.updateCount / 6 % 2 == 0 ? (String)s1 + "_" : (this.updateCount / 6 % 2 == 0 ? (String)s1 + String.valueOf(Formatting.BLACK) + "_" : (String)s1 + String.valueOf(Formatting.GRAY) + "_");
        }
        int l = this.mc.textRenderer.getWidth(s);
        graphics.drawTextWithShadow(this.mc.textRenderer, s, this.guiLeft - l + this.bookImageWidth - 44, this.guiTop + 18, 0);
        graphics.drawTextWrapped(this.mc.textRenderer, (StringVisitable)Text.translatable((String)s1), this.guiLeft + 36, this.guiTop + 18 + 16, 116, 0);
        graphics.fillGradient(this.guiLeft + 175, this.guiTop + 136, this.guiLeft + 269, this.guiTop + 154, -1072689136, -804253680);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)bookWidgets);
        for (int i = 0; i < 4; ++i) {
            graphics.drawTexture(bookWidgets, this.guiLeft + 175 + i * 24, this.guiTop + 134, 0, 22, 24, 24);
        }
        super.render(graphics, mouseX, mouseY, par3);
    }

    @Override
    public void close() {
        this.mc.setScreen(parent);
        parent = null;
        mail = new PlayerMail();
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id == 0) {
            this.username = textfield.getText();
        }
        if (textfield.id == 1) {
            GuiMailmanWrite.mail.subject = textfield.getText();
        }
        if (textfield.id == 2) {
            GuiMailmanWrite.mail.sender = textfield.getText();
        }
    }

    @Override
    public void setError(int i, NbtCompound data) {
        if (i == 0) {
            this.error.setMessage((Text)Text.translatable((String)"mailbox.errorUsername"));
        }
        if (i == 1) {
            this.error.setMessage((Text)Text.translatable((String)"mailbox.errorSubject"));
        }
        this.hasSend = false;
    }

    @Override
    public void setClose(NbtCompound data) {
        this.player.sendMessage((Text)Text.translatable((String)"mailbox.succes", (Object[])new Object[]{data.getString("username")}));
    }

    @Override
    public void save() {
    }

    static {
        mail = new PlayerMail();
    }
}

