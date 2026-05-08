/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.mixin.MouseHelperMixin;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

public class GuiCustomScrollNop
extends Screen {
    public static final Identifier resource = new Identifier("customnpcs", "textures/gui/misc.png");
    protected List<String> list;
    private int listSize = 0;
    public int id;
    public int guiLeft = 0;
    public int guiTop = 0;
    private int selected = -1;
    private List<Integer> selectedList;
    private int hover;
    private int listHeight;
    private int scrollY;
    private int maxScrollY;
    private int scrollHeight;
    private boolean isScrolling;
    public boolean multipleSelection = false;
    public ICustomScrollListener listener;
    private boolean isSorted = true;
    public boolean visible = true;
    private boolean selectable = true;
    private boolean mouseInList = false;
    private int lastClickedItem = -1;
    private long lastClickedTime = 0L;
    private GuiTextFieldNop textField;
    protected boolean hasSearch = true;
    private String searchStr = "";
    private String[] searchWords = new String[0];

    public GuiCustomScrollNop(Screen parent, int id) {
        super((Text)Text.empty());
        this.width = 176;
        this.height = 159;
        this.hover = -1;
        this.selectedList = new ArrayList<Integer>();
        this.listHeight = 0;
        this.scrollY = 0;
        this.scrollHeight = 0;
        this.isScrolling = false;
        if (parent instanceof ICustomScrollListener) {
            this.listener = (ICustomScrollListener)parent;
        }
        this.list = new ArrayList<String>();
        this.id = id;
        this.textField = new GuiTextFieldNop(0, null, 0, 0, 176, 20, "");
        this.client = MinecraftClient.getInstance();
        this.textRenderer = this.client.textRenderer;
    }

    public GuiCustomScrollNop(Screen parent, int id, boolean multipleSelection) {
        this(parent, id);
        this.multipleSelection = multipleSelection;
    }

    public void setSize(int x, int y) {
        this.textField.setWidth(x);
        this.height = y - this.textFieldHeight();
        this.width = x;
        this.listHeight = 14 * this.listSize;
        this.scrollHeight = this.listHeight > 0 ? (int)((double)(this.height - 8) / (double)this.listHeight * (double)(this.height - 8)) : Integer.MAX_VALUE;
        this.maxScrollY = this.listHeight - (this.height - 8) - 1;
        if (this.maxScrollY > 0 && this.scrollY > this.maxScrollY || this.maxScrollY <= 0 && this.scrollY > this.scrollHeight) {
            this.scrollY = 0;
        }
    }

    public void disabledSearch() {
        this.hasSearch = false;
    }

    private int textFieldHeight() {
        return this.hasSearch ? 22 : 0;
    }

    private void reset() {
        this.listSize = this.searchWords.length == 0 ? this.list.size() : (int)this.list.stream().filter(this::isSearched).count();
        this.scrollY = 0;
        this.setSize(this.width, this.height + this.textFieldHeight());
    }

    private boolean isSearched(String s) {
        s = I18n.translate((String)s, (Object[])new Object[0]);
        for (String k : this.searchWords) {
            if (s.toLowerCase().contains(k.toLowerCase())) continue;
            return false;
        }
        return true;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height + this.textFieldHeight();
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        MatrixStack matrixStack = graphics.getMatrices();
        if (this.hasSearch) {
            this.textField.setX(this.guiLeft);
            this.textField.setY(this.guiTop);
            this.textField.render(graphics, mouseX, mouseY, partialTicks);
        }
        this.guiTop += this.textFieldHeight();
        this.mouseInList = this.isMouseOver(mouseX, mouseY);
        graphics.fillGradient(this.guiLeft, this.guiTop, this.width + this.guiLeft, this.height + this.guiTop, -1072689136, -804253680);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        if (this.scrollHeight < this.height - 8) {
            this.drawScrollBar(graphics);
        }
        matrixStack.push();
        matrixStack.translate((float)this.guiLeft, (float)this.guiTop, 0.0f);
        if (this.selectable) {
            this.hover = this.getMouseOver(mouseX, mouseY);
        }
        this.drawItems(graphics);
        matrixStack.pop();
        if (this.scrollHeight < this.height - 8) {
            mouseX -= this.guiLeft;
            mouseY -= this.guiTop;
            if (((MouseHelperMixin)this.client.mouse).getActiveButton() == 0) {
                if (mouseX >= this.width - 10 && mouseX < this.width - 2 && mouseY >= 4 && mouseY < this.height) {
                    this.isScrolling = true;
                }
            } else {
                this.isScrolling = false;
            }
            if (this.isScrolling) {
                this.scrollY = (mouseY - 8) * this.listHeight / (this.height - 8) - this.scrollHeight;
                if (this.scrollY < 0) {
                    this.scrollY = 0;
                }
                if (this.scrollY > this.maxScrollY) {
                    this.scrollY = this.maxScrollY;
                }
            }
        }
        this.guiTop -= this.textFieldHeight();
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
        if (mouseScrolled != 0.0 && this.mouseInList) {
            this.scrollY += mouseScrolled > 0.0 ? -14 : 14;
            if (this.scrollY > this.maxScrollY) {
                this.scrollY = this.maxScrollY;
            }
            if (this.scrollY < 0) {
                this.scrollY = 0;
            }
            return true;
        }
        return false;
    }

    public boolean mouseInOption(int i, int j, int k) {
        int xOffset = this.scrollHeight < this.height - 8 ? 10 : 0;
        int l = 4;
        int i1 = 14 * k + 4 - this.scrollY;
        return i >= l - 1 && i < this.width - 2 - xOffset && j >= i1 - 1 && j < i1 + 8;
    }

    protected void drawItems(DrawContext graphics) {
        int displayIndex = 0;
        for (int i = 0; i < this.list.size(); ++i) {
            if (!this.isSearched(this.list.get(i))) continue;
            int j = 4;
            int k = 14 * displayIndex + 4 - this.scrollY;
            if (k >= 4 && k + 12 < this.height) {
                int xOffset = this.scrollHeight < this.height - 8 ? 0 : 10;
                String displayString = I18n.translate((String)this.list.get(i), (Object[])new Object[0]);
                Object text = "";
                float maxWidth = (float)(this.width + xOffset - 8) * 0.8f;
                if ((float)this.textRenderer.getWidth(displayString) > maxWidth) {
                    char c;
                    for (int h = 0; h < displayString.length() && !((float)this.textRenderer.getWidth((String)(text = (String)text + (c = displayString.charAt(h)))) > maxWidth); ++h) {
                    }
                    if (displayString.length() > ((String)text).length()) {
                        text = (String)text + "...";
                    }
                } else {
                    text = displayString;
                }
                if (this.multipleSelection && this.selectedList.contains(i) || !this.multipleSelection && this.selected == i) {
                    graphics.drawVerticalLine(j - 2, k - 4, k + 10, -1);
                    graphics.drawVerticalLine(j + this.width - 18 + xOffset, k - 4, k + 10, -1);
                    graphics.drawHorizontalLine(j - 2, j + this.width - 18 + xOffset, k - 3, -1);
                    graphics.drawHorizontalLine(j - 2, j + this.width - 18 + xOffset, k + 10, -1);
                    graphics.drawTextWithShadow(this.textRenderer, (String)text, j, k, 0xFFFFFF);
                } else if (i == this.hover) {
                    graphics.drawTextWithShadow(this.textRenderer, (String)text, j, k, 65280);
                } else {
                    graphics.drawTextWithShadow(this.textRenderer, (String)text, j, k, 0xFFFFFF);
                }
            }
            ++displayIndex;
        }
    }

    public String getSelected() {
        if (this.selected < 0 || this.selected >= this.list.size()) {
            return null;
        }
        return this.list.get(this.selected);
    }

    private int getMouseOver(int i, int j) {
        if ((i -= this.guiLeft) >= 4 && i < this.width - 4 && (j -= this.guiTop) >= 4 && j < this.height) {
            int displayIndex = 0;
            for (int j1 = 0; j1 < this.list.size(); ++j1) {
                if (!this.isSearched(this.list.get(j1))) continue;
                if (!this.mouseInOption(i, j, displayIndex)) {
                    ++displayIndex;
                    continue;
                }
                return j1;
            }
        }
        return -1;
    }

    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (this.hasSearch) {
            boolean bo = this.textField.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
            if (!this.searchStr.equals(this.textField.getText())) {
                this.searchStr = this.textField.getText().trim();
                this.searchWords = this.searchStr.split(" ");
                this.reset();
            }
            return bo;
        }
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        if (this.hasSearch) {
            boolean bo = this.textField.charTyped(p_231042_1_, p_231042_2_);
            if (!this.searchStr.equals(this.textField.getText())) {
                this.searchStr = this.textField.getText().trim();
                this.searchWords = this.searchStr.split(" ");
                this.reset();
            }
            return bo;
        }
        return super.charTyped(p_231042_1_, p_231042_2_);
    }

    public boolean mouseClicked(double i, double j, int k) {
        if (this.hasSearch) {
            this.textField.mouseClicked(i, j, k);
        }
        if (k != 0 || this.hover < 0) {
            return false;
        }
        if (this.multipleSelection) {
            if (this.selectedList.contains(this.hover)) {
                this.selectedList.remove((Object)this.hover);
            } else {
                this.selectedList.add(this.hover);
            }
        } else {
            this.selected = this.hover;
            this.hover = -1;
        }
        if (this.listener != null) {
            long time = System.currentTimeMillis();
            this.listener.scrollClicked(i, j, k, this);
            if (this.selected >= 0 && this.selected == this.lastClickedItem && time - this.lastClickedTime < 500L) {
                this.listener.scrollDoubleClicked(this.getSelected(), this);
            }
            this.lastClickedTime = time;
            this.lastClickedItem = this.selected;
        }
        return true;
    }

    private void drawScrollBar(DrawContext graphics) {
        int j;
        int i = this.guiLeft + this.width - 9;
        int k = j = this.guiTop + (int)((double)this.scrollY / (double)this.listHeight * (double)(this.height - 8)) + 4;
        graphics.drawTexture(resource, i, k, this.width, 9, 5, 1);
        ++k;
        while (k < j + this.scrollHeight - 1) {
            graphics.drawTexture(resource, i, k, this.width, 10, 5, 1);
            ++k;
        }
        graphics.drawTexture(resource, i, k, this.width, 11, 5, 1);
    }

    public boolean hasSelected() {
        return this.selected >= 0;
    }

    public void setList(List<String> list) {
        if (this.isSameList(list)) {
            return;
        }
        this.isSorted = true;
        this.scrollY = 0;
        Collections.sort(list, new NaturalOrderComparator());
        this.list = list;
        this.reset();
    }

    public void setUnsortedList(List<String> list) {
        if (this.isSameList(list)) {
            return;
        }
        this.isSorted = false;
        this.scrollY = 0;
        this.list = list;
        this.reset();
    }

    private boolean isSameList(List<String> list) {
        if (this.list.size() != list.size()) {
            return false;
        }
        for (String s : this.list) {
            if (list.contains(s)) continue;
            return false;
        }
        return true;
    }

    public void replace(String old, String name) {
        String select = this.getSelected();
        this.list.remove(old);
        this.list.add(name);
        if (this.isSorted) {
            Collections.sort(this.list, new NaturalOrderComparator());
        }
        if (old.equals(select)) {
            select = name;
        }
        this.setSelected(select);
        this.reset();
    }

    public void setSelected(String name) {
        this.selected = this.list.indexOf(name);
    }

    public void clear() {
        this.list = new ArrayList<String>();
        this.selected = -1;
        this.scrollY = 0;
        this.searchStr = "";
        this.searchWords = new String[0];
        this.textField.setText("");
        this.reset();
    }

    public void clearSelection() {
        this.list = new ArrayList<String>();
        this.selected = -1;
    }

    public List<String> getList() {
        return this.list;
    }

    public List<String> getSelectedList() {
        return IntStream.range(0, this.list.size()).filter(i -> this.selectedList.contains(i)).mapToObj(i -> this.list.get(i)).collect(Collectors.toList());
    }

    public void setSelectedList(Collection<String> selectedList) {
        this.selectedList = selectedList.stream().map(t -> this.list.indexOf(t)).collect(Collectors.toList());
    }

    public GuiCustomScrollNop setUnselectable() {
        this.selectable = false;
        return this;
    }

    public void scrollTo(String name) {
        int i = this.list.indexOf(name);
        if (i < 0 || this.scrollHeight >= this.height - 8) {
            return;
        }
        int pos = (int)(1.0f * (float)i / (float)this.list.size() * (float)this.listHeight);
        if (pos > this.maxScrollY) {
            pos = this.maxScrollY;
        }
        this.scrollY = pos;
    }

    public boolean isMouseOver(int x, int y) {
        return x >= this.guiLeft && x <= this.guiLeft + this.width && y >= this.guiTop && y <= this.guiTop + this.height;
    }

    public int getSelectedIndex() {
        return this.selected;
    }

    public void setSelectedIndex(int i) {
        this.selected = i < 0 ? -1 : i;
    }
}

