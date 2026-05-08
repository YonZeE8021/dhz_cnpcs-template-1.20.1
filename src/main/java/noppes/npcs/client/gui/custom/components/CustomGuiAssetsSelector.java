/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 */
package noppes.npcs.client.gui.custom.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiLabelWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiLabel;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiButton;
import noppes.npcs.packets.server.SPacketCustomGuiTextUpdate;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.util.AssetsFinder;

public class CustomGuiAssetsSelector
extends ClickableWidget
implements IGuiComponent {
    private String up = "..<" + I18n.translate((String)"gui.up", (Object[])new Object[0]) + ">..";
    private GuiCustom parent;
    private CustomGuiAssetsSelectorWrapper component;
    private GuiCustomScrollNop folders;
    private GuiCustomScrollNop items;
    private CustomGuiLabel label;
    public int id;
    private static final HashMap<String, List<Identifier>> domains = new HashMap();
    private static final HashMap<String, Identifier> textures = new HashMap();
    private String location = "";
    private String path = "";
    private String selectedDomain;
    public Identifier prevResource = null;
    public Identifier selectedResource = null;

    public CustomGuiAssetsSelector(GuiCustom parent, final CustomGuiAssetsSelectorWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.empty());
        this.parent = parent;
        this.component = component;
        this.folders = new GuiCustomScrollNop((Screen)parent, 101);
        this.items = new GuiCustomScrollNop((Screen)parent, 102);
        this.label = new CustomGuiLabel(parent, (CustomGuiLabelWrapper)new CustomGuiLabelWrapper().setCentered(true));
        this.init();
        if (!component.getSelected().isEmpty()) {
            this.selectedResource = this.prevResource = new Identifier(component.getSelected());
        }
        List<Identifier> resources = AssetsFinder.find(component.getRoot(), "." + component.getFileType());
        for (Identifier loc : resources) {
            domains.computeIfAbsent(loc.getNamespace(), k -> new ArrayList()).add(loc);
        }
        if (this.selectedResource != null && !this.selectedResource.getPath().isEmpty()) {
            this.selectedDomain = this.selectedResource.getNamespace();
            if (!domains.containsKey(this.selectedDomain)) {
                this.selectedDomain = null;
            }
            int i = this.selectedResource.getPath().lastIndexOf(47);
            this.location = this.path = this.selectedResource.getPath().substring(0, i + 1);
            i = this.path.lastIndexOf(47, this.path.length() - 2);
            if (i > 0) {
                this.location = this.path.substring(0, i + 1);
            }
            this.label.setText(this.selectedDomain + ":" + this.location);
        }
        this.setFolders();
        this.setItems();
        this.folders.listener = new ICustomScrollListener(){

            @Override
            public void scrollClicked(double x, double y, int k, GuiCustomScrollNop scroll) {
                if (scroll.getSelected().equals(CustomGuiAssetsSelector.this.up)) {
                    return;
                }
                CustomGuiAssetsSelector.this.path = CustomGuiAssetsSelector.this.location + scroll.getSelected() + "/";
                CustomGuiAssetsSelector.this.setItems();
            }

            @Override
            public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
                if (CustomGuiAssetsSelector.this.selectedDomain == null) {
                    CustomGuiAssetsSelector.this.selectedDomain = scroll.getSelected();
                    if (!component.getRoot().isEmpty()) {
                        CustomGuiAssetsSelector.this.path = CustomGuiAssetsSelector.this.location = component.getRoot() + "/";
                    }
                } else if (scroll.getSelected().equals(CustomGuiAssetsSelector.this.up)) {
                    int i = CustomGuiAssetsSelector.this.location.lastIndexOf(47, CustomGuiAssetsSelector.this.location.length() - 2);
                    if (i > 0) {
                        CustomGuiAssetsSelector.this.path = CustomGuiAssetsSelector.this.location;
                        CustomGuiAssetsSelector.this.location = CustomGuiAssetsSelector.this.location.substring(0, i + 1);
                    } else {
                        CustomGuiAssetsSelector.this.location = "";
                        CustomGuiAssetsSelector.this.path = "";
                    }
                    if (CustomGuiAssetsSelector.this.location.isEmpty()) {
                        CustomGuiAssetsSelector.this.selectedDomain = null;
                    }
                } else {
                    CustomGuiAssetsSelector.this.path = CustomGuiAssetsSelector.this.location = CustomGuiAssetsSelector.this.location + scroll.getSelected() + "/";
                }
                CustomGuiAssetsSelector.this.setFolders();
                CustomGuiAssetsSelector.this.setItems();
                CustomGuiAssetsSelector.this.label.setText(CustomGuiAssetsSelector.this.selectedDomain + ":" + CustomGuiAssetsSelector.this.location);
            }
        };
        this.items.listener = new ICustomScrollListener(){

            @Override
            public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
                CustomGuiAssetsSelector.this.selectedResource = textures.get(scroll.getSelected());
                component.setSelected(textures.get(scroll.getSelected()).toString());
                if (!component.disablePackets) {
                    Packets.sendServer(new SPacketCustomGuiTextUpdate(component.getUniqueID(), component.getSelected()));
                } else {
                    component.onChange(null);
                }
            }

            @Override
            public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
                if (!component.disablePackets) {
                    Packets.sendServer(new SPacketCustomGuiButton(component.getUniqueID()));
                } else {
                    component.onPress(null);
                }
            }
        };
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.folders.guiTop = this.items.guiTop = this.getY() + 10;
        this.setWidth(this.component.getWidth());
        this.height = this.component.getHeight();
        this.folders.setSize(this.component.getWidth() / 2 - 1, this.component.getHeight() - 10);
        this.items.setSize(this.component.getWidth() / 2 - 1, this.component.getHeight() - 10);
        this.folders.guiLeft = this.getX();
        this.items.guiLeft = this.getX() + this.component.getWidth() / 2 + 1;
        this.label.setWidth(this.component.getWidth());
        this.label.setX(this.getX());
        this.label.setY(this.getY());
        this.label.setHeight(10);
        if (!this.component.getSelected().isEmpty()) {
            this.selectedResource = new Identifier(this.component.getSelected());
        }
    }

    private void setFolders() {
        if (this.selectedDomain == null) {
            this.folders.setList(Lists.newArrayList(domains.keySet()));
            if (this.selectedResource != null) {
                this.selectedDomain = this.selectedResource.getNamespace();
                this.folders.setSelected(this.selectedDomain);
            }
            return;
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(this.up);
        for (Identifier td : domains.get(this.selectedDomain)) {
            String path;
            int i;
            String fullPath = td.getPath();
            if (fullPath.indexOf(47) >= 0) {
                fullPath = fullPath.substring(0, fullPath.lastIndexOf(47) + 1);
            }
            if (!this.location.isEmpty() && (!fullPath.startsWith(this.location) || fullPath.equals(this.location)) || (i = (path = fullPath.substring(this.location.length())).indexOf(47)) < 0 || (path = path.substring(0, i)).isEmpty() || list.contains(path)) continue;
            list.add(path);
        }
        this.folders.clearSelection();
        this.folders.setList(list);
        if (this.selectedResource != null && this.selectedResource.getPath().startsWith(this.location) && !this.location.equals(this.path)) {
            this.folders.setSelected(this.path.substring(this.location.length(), this.path.length() - 1));
            this.folders.scrollTo(this.folders.getSelected());
        }
    }

    private void setItems() {
        if (this.selectedDomain == null) {
            return;
        }
        textures.clear();
        List<Identifier> data = domains.get(this.selectedDomain);
        ArrayList<String> list = new ArrayList<String>();
        for (Identifier td : data) {
            String name = td.getPath();
            String path = td.getPath();
            if (name.indexOf(47) >= 0) {
                name = name.substring(name.lastIndexOf(47) + 1);
                path = path.substring(0, path.lastIndexOf(47) + 1);
            }
            if (!path.equals(this.path) || list.contains(name)) continue;
            list.add(name);
            textures.put(name, td);
        }
        this.items.clearSelection();
        this.items.setList(list);
        if (this.selectedResource != null) {
            int i = this.selectedResource.getPath().lastIndexOf(47);
            String name = this.selectedResource.getPath().substring(i + 1);
            String path = this.selectedResource.getPath().substring(0, i + 1);
            if (path.equals(this.path)) {
                this.items.setSelected(name);
            }
        }
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public void onRenderPost(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        boolean hovered;
        if (!this.visible) {
            return;
        }
        this.label.onRender(graphics, mouseX, mouseY, partialTicks);
        this.folders.render(graphics, mouseX, mouseY, partialTicks);
        this.items.render(graphics, mouseX, mouseY, partialTicks);
        boolean bl = hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
        return this.folders.mouseScrolled(mouseX, mouseY, mouseScrolled) || this.items.mouseScrolled(mouseX, mouseY, mouseScrolled);
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.folders.mouseClicked(mouseX, mouseY, button) || this.items.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return this.folders.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) || this.items.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        return this.folders.charTyped(p_231042_1_, p_231042_2_) || this.items.charTyped(p_231042_1_, p_231042_2_);
    }

    public void appendClickableNarrations(NarrationMessageBuilder p_169152_) {
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }
}

