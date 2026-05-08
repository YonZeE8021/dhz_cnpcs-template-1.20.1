/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.text.MutableText
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.entity.EntityRenderDispatcher
 */
package noppes.npcs.client.gui.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.text.MutableText;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import noppes.npcs.ModelData;
import noppes.npcs.ModelEyeData;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.gui.subgui.AssetsGui;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonListWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiEntityDisplayWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.api.wrapper.gui.GuiComponentsScrollableWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiEntityDisplay;
import noppes.npcs.client.gui.custom.components.CustomGuiScroll;
import noppes.npcs.client.gui.custom.components.CustomGuiSlider;
import noppes.npcs.client.gui.custom.components.CustomGuiTexturedRect;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.client.gui.model.GuiModelColor;
import noppes.npcs.client.layer.LayerParts;
import noppes.npcs.client.parts.ModelPartWrapper;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartAbstractClient;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.client.parts.MpmPartEyes;
import noppes.npcs.client.parts.MpmPartReader;
import noppes.npcs.client.parts.PartBehaviorType;
import noppes.npcs.client.parts.PartRenderType;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiParts;
import noppes.npcs.shared.client.gui.components.GuiColorButton;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.common.util.ColorUtil;
import noppes.npcs.shared.common.util.NaturalOrderComparator;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;

public class GuiCreationNewParts
extends ClickableWidget
implements IGuiComponent {
    private CustomGuiScroll scroll;
    private CustomGuiSlider slider;
    private CustomGuiEntityDisplay entity;
    private ModelData data;
    private ModelData renderData = new ModelData(null);
    private static String active = "";
    private static PlayerEntityModel biped;
    private GuiCustom parent;
    private EntityCustomNpc npc;
    private MinecraftClient minecraft;
    private List<GuiMpmPart> guiParts = new ArrayList<GuiMpmPart>();
    public static final Identifier buttonsResource;
    private static final Identifier colorWheel;

    public GuiCreationNewParts(final GuiCustom parent, EntityCustomNpc npc) {
        super(0, 0, 420, 200, (Text)Text.empty());
        this.npc = npc;
        this.parent = parent;
        this.data = npc.modelData;
        this.minecraft = MinecraftClient.getInstance();
        String[] menus = (String[])MpmPartReader.PARTS.values().stream().map(p -> p.menu).sorted(new NaturalOrderComparator()).distinct().toArray(String[]::new);
        if (active.isEmpty()) {
            active = menus[0];
        }
        this.scroll = new CustomGuiScroll(parent, new CustomGuiScrollWrapper(-4, 4, 24, 100, 210, menus));
        this.scroll.disabledSearch();
        this.scroll.listener = new ICustomScrollListener(){

            @Override
            public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
                if (scroll.getSelectedIndex() >= 0) {
                    active = scroll.getSelected();
                    for (GuiMpmPart part : GuiCreationNewParts.this.guiParts) {
                        parent.scrollingPanel.comps.removeComponent(part.getID());
                    }
                    GuiCreationNewParts.this.guiParts.clear();
                    parent.init();
                }
            }

            @Override
            public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
            }
        };
        CustomGuiEntityDisplayWrapper wrapper = new CustomGuiEntityDisplayWrapper(-2, npc.wrappedNPC, 106, 90);
        wrapper.setSize(68, 90);
        this.entity = new CustomGuiEntityDisplay(parent, wrapper);
        this.slider = new CustomGuiSlider(parent, (CustomGuiSliderWrapper)new CustomGuiSliderWrapper(-3, "", 106, 186, 68, 20).setMax(360.0f).setDecimals(0).setValue(180.0f).setOnChange((gui, slider) -> {
            this.entity.component.setRotation((int)slider.getValue() - 180);
            this.entity.init();
        })).disablePackets();
        biped = new PlayerEntityModel(this.minecraft.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER), true);
    }

    public void openSubgui(GuiCustom parent, GuiCustom subgui) {
        subgui.init(this.minecraft, parent.width, parent.height);
        subgui.parent = parent;
        parent.subgui = subgui;
        if (subgui.guiWrapper != null) {
            subgui.background = new CustomGuiTexturedRect(subgui, (CustomGuiTexturedRectWrapper)subgui.guiWrapper.getBackgroundRect());
        }
        if (subgui.scrollingPanel.comps == null) {
            subgui.scrollingPanel.comps = new GuiComponentsScrollableWrapper(subgui.guiWrapper, null);
        }
    }

    @Override
    public int getID() {
        return -10;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.entity.visible = this.parent.subgui == null;
        this.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        this.parent.add(this.scroll);
        this.parent.add(this.entity);
        this.parent.add(this.slider);
        ArrayList list = new ArrayList(MpmPartReader.PARTS.values().stream().sorted(Comparator.comparing(t -> t.id)).filter(t -> t.menu.equals(active) && t.parentId == null).collect(Collectors.toList()));
        this.scroll.setSelected(active);
        this.entity.setEntity((Entity)this.npc);
        if (this.guiParts.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                int column = i % 3;
                MpmPart part = (MpmPart)list.get(i);
                GuiMpmPart gui = new GuiMpmPart(this.parent, 80 + i, column * 70 + column, i / 3 * 70, part);
                this.guiParts.add(gui);
                this.parent.addPanel(gui);
                this.parent.scrollingPanel.comps.addComponent(new PartsWrapper(gui));
            }
        } else {
            for (int i = 0; i < this.guiParts.size(); ++i) {
                int column = i % 3;
                GuiMpmPart gui = this.guiParts.get(i);
                gui.setX(column * 70 + column);
                gui.setY(i / 3 * 70);
                this.parent.addPanel(gui);
            }
        }
        this.parent.scrollingPanel.setMaxSize(this.guiParts.stream().mapToInt(v -> v.getY() + v.getHeight()).max().orElse(0));
    }

    @Override
    public ICustomGuiComponent component() {
        return null;
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    public void render(DrawContext graphics, int i, int j, float f) {
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }

    public void save() {
        Packets.sendServer(new SPacketCustomGuiParts(this.data.save()));
    }

    public void openTextureSubgui(GuiCustom parent, MpmPartData data, MpmPart part) {
        TexturePart screen = new TexturePart(data, part);
        CustomGuiWrapper gui = screen.guiWrapper;
        gui.setBackgroundTexture("customnpcs:textures/gui/components.png");
        gui.setSize(310, 200);
        gui.getBackgroundRect().setTextureOffset(0, 0);
        gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
        ((CustomGuiComponentWrapper)(gui.addButton(66, "x", 276, 4, 20, 20).setOnPress((gui2, button) -> screen.close()))).setDisablePackets();
        if (!part.disableCustomTextures) {
            gui.addLabel(21, "gui.playerskin", 4, 110, 10, 100);
            gui.addButtonList(22, 76, 105, 50, 20).setValues("gui.no", "gui.yes").setSelected(data.usePlayerSkin ? 1 : 0).setOnPress((gui2, button) -> {
                data.usePlayerSkin = ((CustomGuiButtonListWrapper)button).getSelected() == 1;
                gui2.getComponent(23).setVisible(!data.usePlayerSkin);
                gui2.getComponent(24).setVisible(!data.usePlayerSkin);
                gui2.getComponent(25).setVisible(!data.usePlayerSkin);
                gui2.getComponent(26).setVisible(!data.usePlayerSkin);
                gui2.getComponent(27).setVisible(!data.usePlayerSkin);
                this.data.refreshParts();
                this.save();
                screen.init();
            }).setDisablePackets();
            gui.addLabel(23, "gui.texture", 4, 130, 10, 100).setVisible(!data.usePlayerSkin);
            Identifier loc = data.getDefaultTexture();
            CustomGuiTextFieldWrapper tf = (CustomGuiTextFieldWrapper)((CustomGuiComponentWrapper)(gui.addTextField(24, 4, 140, 220, 20).setText(loc == null ? "" : loc.toString()).setOnFocusLost((gui2, text) -> data.setTexture(text.getText())))).setVisible(!data.usePlayerSkin).setDisablePackets();
            ((CustomGuiComponentWrapper)(gui.addButton(25, "gui.select", 226, 140, 80, 20).setOnPress((gui2, button) -> this.openSubgui(screen, this.openTextureBasic(data.getDefaultTexture() == null ? "" : data.getDefaultTexture().toString(), resource -> {
                data.setTexture(resource);
                tf.setText(resource);
                this.data.refreshParts();
                this.save();
                screen.init();
            }))))).setVisible(!data.usePlayerSkin).setDisablePackets();
            gui.addLabel(26, "config.skinurl", 4, 168, 10, 100).setVisible(!data.usePlayerSkin);
            ((CustomGuiComponentWrapper)(gui.addTextField(27, 4, 178, 220, 20).setText(data.url).setOnFocusLost((gui2, text) -> {
                data.setUrl(text.getText());
                this.data.refreshParts();
                this.save();
                screen.init();
            }))).setVisible(!data.usePlayerSkin).setDisablePackets();
        }
        screen.setGuiWrapper(gui);
        this.openSubgui(parent, screen);
    }

    public GuiCustom openTextureBasic(String resource, AssetsGui.SelectionCallback callback) {
        GuiCustom screen = new GuiCustom((ContainerCustomGui)this.parent.getScreenHandler(), this.parent.inv, (Text)Text.empty());
        CustomGuiWrapper gui = screen.guiWrapper = new CustomGuiWrapper(null);
        gui.setBackgroundTexture("customnpcs:textures/gui/components.png");
        gui.setSize(308, 214);
        gui.getBackgroundRect().setTextureOffset(0, 0);
        gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
        CustomGuiButtonWrapper b = gui.addTexturedButton(666, "X", 290, -4, 14, 14, "customnpcs:textures/gui/components.png", 0, 64);
        b.getTextureRect().setRepeatingTexture(64, 22, 3).setHoverText("gui.close");
        b.setTextureHoverOffset(22).setOnPress((guii, bb) -> screen.close());
        b.setDisablePackets();
        ((CustomGuiComponentWrapper)(((CustomGuiAssetsSelectorWrapper)gui.addAssetsSelector(11, 4, 4, 300, 204).setSelected(resource).setOnPress((gui2, assets) -> screen.close())).setOnChange((gui2, assets) -> callback.call(assets.getSelected())))).setDisablePackets();
        screen.setGuiWrapper(gui);
        return screen;
    }

    public void openEyesSubgui(GuiCustom parent, ModelEyeData data, MpmPartEyes part) {
        EyesPart screen = new EyesPart(data, part);
        CustomGuiWrapper gui = screen.guiWrapper = new CustomGuiWrapper(null);
        gui.setBackgroundTexture("customnpcs:textures/gui/components.png");
        gui.setSize(310, 200);
        gui.getBackgroundRect().setTextureOffset(0, 0);
        gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
        int y = 8;
        gui.addLabel(21, "part.eyes", 56, y + 5, 10, 100);
        gui.addButtonList(22, 110, y, 110, 20).setValues("gui.playerskin", "gui.normal", "gui.texture").setSelected(data.skinType).setOnPress((gui2, button) -> {
            data.skinType = ((CustomGuiButtonListWrapper)button).getSelected();
            gui2.getComponent(23).setVisible(data.skinType == 1);
            gui2.getComponent(24).setVisible(data.skinType == 2);
            gui2.getComponent(25).setVisible(data.skinType == 2);
            gui2.getComponent(27).setVisible(data.glint || data.skinType == 1 || data.skinType == 2);
            screen.init();
        }).setDisablePackets();
        ((CustomGuiComponentWrapper)(gui.addButton(23, "", 230, y, 50, 20).setOnPress((gui2, button) -> this.openSubgui(screen, new GuiModelColor(screen, ColorUtil.rgbToColor(data.color), color -> {
            data.color = ColorUtil.colorToRgb(color);
            screen.init();
        }))))).setVisible(data.skinType == 1).setDisablePackets();
        gui.addLabel(24, "config.skinurl", 56, (y += 25) + 5, 10, 100).setVisible(data.skinType == 2);
        ((CustomGuiComponentWrapper)(gui.addTextField(25, 110, y, 195, 20).setText(data.url).setOnFocusLost((gui2, text) -> data.setUrl(text.getText())))).setVisible(data.skinType == 2).setDisablePackets();
        gui.addButtonList(26, 54, y += 25, 100, 20).setValues("gui.normal", "gui.big").setSelected(data.eyeSize).setOnPress((gui2, button) -> {
            data.eyeSize = ((CustomGuiButtonListWrapper)button).getSelected();
            screen.init();
        }).setDisablePackets();
        gui.addButtonList(27, 156, y, 100, 20).setValues("gui.normal", "gui.mirror").setSelected(data.mirror ? 1 : 0).setOnPress((gui2, button) -> {
            data.mirror = ((CustomGuiButtonListWrapper)button).getSelected() == 1;
            screen.init();
        }).setVisible(data.glint || data.skinType == 1 || data.skinType == 2).setDisablePackets();
        gui.addLabel(28, "eye.pupil", 4, y + 5, 10, 100);
        gui.addButtonList(29, 54, y += 25, 100, 20).setValues(I18n.translate((String)"gui.down", (Object[])new Object[0]) + "x2", "gui.down", "gui.normal", "gui.up", I18n.translate((String)"gui.up", (Object[])new Object[0]) + "x2").setSelected(data.eyePos.y + 2).setOnPress((gui2, button) -> {
            data.eyePos = new NopVector2i(data.eyePos.x, ((CustomGuiButtonListWrapper)button).getSelected() - 2);
        }).setDisablePackets();
        gui.addButtonList(30, 156, y, 100, 20).setValues("gui.inward", "gui.normal", "gui.outward").setSelected(data.eyePos.x + 1).setOnPress((gui2, button) -> {
            data.eyePos = new NopVector2i(((CustomGuiButtonListWrapper)button).getSelected() - 1, data.eyePos.y);
        }).setDisablePackets();
        gui.addLabel(31, "gui.position", 4, y + 5, 10, 100);
        gui.addButtonList(32, 54, y += 25, 50, 20).setValues("gui.no", "gui.yes").setSelected(data.glint ? 1 : 0).setOnPress((gui2, button) -> {
            data.glint = ((CustomGuiButtonListWrapper)button).getSelected() == 1;
            gui2.getComponent(27).setVisible(data.glint || data.skinType == 1 || data.skinType == 2);
        }).setDisablePackets();
        gui.addLabel(33, "eye.glint", 4, y + 5, 10, 100);
        ((CustomGuiComponentWrapper)(gui.addButton(34, "", 162, y, 50, 20).setOnPress((gui2, button) -> this.openSubgui(screen, new GuiModelColor(screen, ColorUtil.rgbToColor(data.browColor), color -> {
            data.browColor = ColorUtil.colorToRgb(color);
            screen.init();
        }))))).setDisablePackets();
        gui.addButtonList(35, 214, y, 70, 20).setValues("gui.disabled", "1", "2", "3", "4", "5", "6", "7", "8").setSelected((int)(data.browThickness.y * 10.0f)).setOnPress((gui2, button) -> {
            data.browThickness = new NopVector3f(1.0f, (float)((CustomGuiButtonListWrapper)button).getSelected() / 10.0f, 1.0f);
        }).setDisablePackets();
        gui.addLabel(36, "eye.lash", 112, y + 5, 10, 100);
        gui.addButtonList(37, 54, y += 25, 50, 20).setValues("gui.no", "gui.yes").setSelected(data.disableBlink ? 0 : 1).setOnPress((gui2, button) -> {
            data.disableBlink = ((CustomGuiButtonListWrapper)button).getSelected() == 0;
            gui2.getComponent(39).setVisible(!data.disableBlink);
            gui2.getComponent(40).setVisible(!data.disableBlink);
            screen.init();
        }).setDisablePackets();
        gui.addLabel(38, "eye.blink", 4, y + 5, 10, 100);
        gui.addLabel(39, "eye.lid", 112, y + 5, 10, 100).setVisible(!data.disableBlink);
        ((CustomGuiComponentWrapper)(gui.addButton(40, "", 162, y, 50, 20).setOnPress((gui2, button) -> this.openSubgui(screen, new GuiModelColor(screen, ColorUtil.rgbToColor(data.lidColor), color -> {
            data.lidColor = ColorUtil.colorToRgb(color);
            screen.init();
        }))))).setVisible(!data.disableBlink).setDisablePackets();
        ((CustomGuiComponentWrapper)(gui.addButton(66, "x", 288, 4, 20, 20).setOnPress((gui2, button) -> screen.close()))).setDisablePackets();
        screen.setGuiWrapper(gui);
        this.openSubgui(parent, screen);
    }

    static {
        buttonsResource = new Identifier("moreplayermodels", "textures/gui/arrowbuttons.png");
        colorWheel = new Identifier("moreplayermodels", "textures/gui/colorwheel.png");
    }

    class GuiMpmPart
    extends ClickableWidget
    implements IGuiComponent {
        public static final int SIZE = 70;
        public boolean basic;
        private List<MpmPart> all;
        private MpmPart part;
        private MpmPartData data;
        private boolean selected;
        private GuiCustom parent;
        boolean colorPickerHovered;
        boolean infoHovered;
        boolean settingsHovered;
        boolean hoverL;
        boolean hoverR;
        int zPos;
        int id;

        public GuiMpmPart(GuiCustom parent, int id, int x, int y, MpmPart part) {
            super(x, y, 70, 70, (Text)Text.empty());
            this.basic = false;
            this.all = new ArrayList<MpmPart>();
            this.selected = true;
            this.colorPickerHovered = false;
            this.infoHovered = false;
            this.settingsHovered = false;
            this.hoverL = false;
            this.hoverR = false;
            this.zPos = 0;
            this.parent = parent;
            this.id = id;
            this.part = part;
            this.all.add(part);
            for (Map.Entry<Identifier, MpmPart> entry : MpmPartReader.PARTS.entrySet()) {
                if (entry.getValue().parentId == null || !entry.getValue().parentId.equals(part.id)) continue;
                this.all.add(entry.getValue());
            }
            for (MpmPart p : this.all) {
                this.data = GuiCreationNewParts.this.data.mpmParts.stream().filter(t -> t.partId.equals(p.id)).findFirst().orElse(null);
                if (this.data == null) continue;
                this.part = p;
                break;
            }
            this.all = this.all.stream().sorted(Comparator.comparing(t -> t.id)).collect(Collectors.toList());
            if (this.data == null) {
                this.data = part.id.equals(ModelEyeData.RESOURCE) || part.id.equals(ModelEyeData.RESOURCE_RIGHT) || part.id.equals(ModelEyeData.RESOURCE_LEFT) ? new ModelEyeData() : new MpmPartData();
                this.data.partId = part.id;
                this.data.usePlayerSkin = part.defaultUsePlayerSkins;
                this.selected = false;
            }
        }

        public void renderModel(DrawContext graphics, int xMouse, int yMouse, float tick) {
            int x1 = this.getX();
            int x2 = this.getX() + 70;
            int y1 = this.getY();
            int y2 = this.getY() + 70 - 1;
            graphics.fill(x1, y1, x2, y2, -3750202);
            GuiCreationNewParts.this.renderData.mpmParts = GuiCreationNewParts.this.data.mpmParts;
            MatrixStack posestack = RenderSystem.getModelViewStack();
            posestack.push();
            posestack.translate(0.0, 0.0, 100.0 + (double)this.zPos);
            posestack.scale(1.0f, 1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.translate((float)this.getX(), (float)(this.getY() - this.parent.scrollingPanel.comps.scrollAmount), 1.0f);
            matrixstack.push();
            EntityRenderDispatcher entityrenderermanager = GuiCreationNewParts.this.minecraft.getEntityRenderDispatcher();
            entityrenderermanager.setRenderShadows(false);
            VertexConsumerProvider.Immediate irendertypebuffer$impl = GuiCreationNewParts.this.minecraft.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer ivertex = irendertypebuffer$impl.getBuffer(RenderLayer.getEntityCutoutNoCull((Identifier)GuiCreationNewParts.this.npc.textureLocation));
            DiffuseLighting.disableGuiDepthLighting();
            RenderSystem.runAsFancy(() -> {
                ModelPartWrapper modelPart;
                GuiCreationNewParts.biped.leftLeg.visible = !this.part.hiddenParts.contains(BodyPart.LEFT_LEG) && !this.part.hiddenParts.contains(BodyPart.LEGS);
                GuiCreationNewParts.biped.leftPants.visible = GuiCreationNewParts.biped.leftPants.visible && GuiCreationNewParts.biped.leftLeg.visible;
                GuiCreationNewParts.biped.rightLeg.visible = !this.part.hiddenParts.contains(BodyPart.RIGHT_LEG) && !this.part.hiddenParts.contains(BodyPart.LEGS);
                GuiCreationNewParts.biped.rightPants.visible = GuiCreationNewParts.biped.rightPants.visible && GuiCreationNewParts.biped.rightLeg.visible;
                GuiCreationNewParts.biped.leftArm.visible = !this.part.hiddenParts.contains(BodyPart.LEFT_ARM) && !this.part.hiddenParts.contains(BodyPart.ARMS);
                GuiCreationNewParts.biped.leftSleeve.visible = GuiCreationNewParts.biped.leftSleeve.visible && GuiCreationNewParts.biped.leftArm.visible;
                GuiCreationNewParts.biped.rightArm.visible = !this.part.hiddenParts.contains(BodyPart.RIGHT_ARM) && !this.part.hiddenParts.contains(BodyPart.ARMS);
                GuiCreationNewParts.biped.rightSleeve.visible = GuiCreationNewParts.biped.rightSleeve.visible && GuiCreationNewParts.biped.rightArm.visible;
                GuiCreationNewParts.biped.body.visible = !this.part.hiddenParts.contains(BodyPart.BODY);
                GuiCreationNewParts.biped.jacket.visible = GuiCreationNewParts.biped.jacket.visible && GuiCreationNewParts.biped.body.visible;
                GuiCreationNewParts.biped.head.visible = !this.part.hiddenParts.contains(BodyPart.HEAD);
                boolean bl = GuiCreationNewParts.biped.hat.visible = GuiCreationNewParts.biped.hat.visible && GuiCreationNewParts.biped.head.visible;
                if (this.part.bodyPart == BodyPart.HEAD) {
                    matrixstack.translate(32.0f, 46.0f, 25.0f);
                    matrixstack.scale(36.0f, 36.0f, 36.0f);
                    matrixstack.multiply(RotationAxis.POSITIVE_X.rotation(0.3926991f));
                    matrixstack.multiply(RotationAxis.POSITIVE_Y.rotation((float)this.part.previewRotation * ((float)Math.PI / 180)));
                    GuiCreationNewParts.biped.head.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                }
                if (this.part.bodyPart == BodyPart.LEGS) {
                    matrixstack.translate(18.0f, 12.0f, 25.0f);
                    matrixstack.scale(36.0f, 36.0f, 36.0f);
                    matrixstack.multiply(RotationAxis.POSITIVE_X.rotation(0.3926991f));
                    matrixstack.multiply(RotationAxis.POSITIVE_Y.rotation((float)this.part.previewRotation * ((float)Math.PI / 180)));
                    GuiCreationNewParts.biped.body.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                    if (this.part.animationType == PartBehaviorType.LEGS) {
                        modelPart = this.part.getPart("right_leg");
                        if (modelPart != null) {
                            modelPart.setRot(new NopVector3f(GuiCreationNewParts.biped.rightLeg.pitch, GuiCreationNewParts.biped.rightLeg.yaw, GuiCreationNewParts.biped.rightLeg.roll));
                            modelPart.setPos(new NopVector3f(GuiCreationNewParts.biped.rightLeg.pivotX, GuiCreationNewParts.biped.rightLeg.pivotY, GuiCreationNewParts.biped.rightLeg.pivotZ));
                        }
                        if ((modelPart = this.part.getPart("left_leg")) != null) {
                            modelPart.setRot(new NopVector3f(GuiCreationNewParts.biped.leftLeg.pitch, GuiCreationNewParts.biped.leftLeg.yaw, GuiCreationNewParts.biped.leftLeg.roll));
                            modelPart.setPos(new NopVector3f(GuiCreationNewParts.biped.leftLeg.pivotX, GuiCreationNewParts.biped.leftLeg.pivotY, GuiCreationNewParts.biped.leftLeg.pivotZ));
                        }
                    }
                    GuiCreationNewParts.biped.rightLeg.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                    GuiCreationNewParts.biped.leftLeg.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                }
                if (this.part.bodyPart == BodyPart.ARMS) {
                    matrixstack.translate(18.0f, 12.0f, 25.0f);
                    matrixstack.scale(36.0f, 36.0f, 36.0f);
                    matrixstack.multiply(RotationAxis.POSITIVE_X.rotation(0.3926991f));
                    matrixstack.multiply(RotationAxis.POSITIVE_Y.rotation((float)this.part.previewRotation * ((float)Math.PI / 180)));
                    GuiCreationNewParts.biped.body.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                    if (this.part.animationType == PartBehaviorType.ARMS) {
                        modelPart = this.part.getPart("right_arm");
                        if (modelPart != null) {
                            modelPart.setRot(new NopVector3f(GuiCreationNewParts.biped.rightArm.pitch, GuiCreationNewParts.biped.rightArm.yaw, GuiCreationNewParts.biped.rightArm.roll));
                            modelPart.setPos(new NopVector3f(GuiCreationNewParts.biped.rightArm.pivotX, GuiCreationNewParts.biped.rightArm.pivotY, GuiCreationNewParts.biped.rightArm.pivotZ));
                        }
                        if ((modelPart = this.part.getPart("left_arm")) != null) {
                            modelPart.setRot(new NopVector3f(GuiCreationNewParts.biped.leftArm.pitch, GuiCreationNewParts.biped.leftArm.yaw, GuiCreationNewParts.biped.leftArm.roll));
                            modelPart.setPos(new NopVector3f(GuiCreationNewParts.biped.leftArm.pivotX, GuiCreationNewParts.biped.leftArm.pivotY, GuiCreationNewParts.biped.leftArm.pivotZ));
                        }
                    }
                    GuiCreationNewParts.biped.leftArm.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                    GuiCreationNewParts.biped.rightArm.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                }
                if (this.part.bodyPart == BodyPart.BODY) {
                    matrixstack.translate(18.0f, 18.0f, 25.0f);
                    matrixstack.scale(36.0f, 36.0f, 36.0f);
                    matrixstack.multiply(RotationAxis.POSITIVE_X.rotation(0.3926991f));
                    matrixstack.multiply(RotationAxis.POSITIVE_Y.rotation((float)this.part.previewRotation * ((float)Math.PI / 180)));
                    GuiCreationNewParts.biped.body.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                }
                if (this.part.renderType != PartRenderType.NONE) {
                    MpmPartAbstractClient partc = (MpmPartAbstractClient)this.part;
                    partc.pos = NopVector3f.ZERO;
                    partc.rot = NopVector3f.ZERO;
                    LayerParts.renderPart(this.data, partc, matrixstack, (VertexConsumerProvider)irendertypebuffer$impl, 0xF000F0, GuiCreationNewParts.this.npc, (BipedEntityModel)biped, GuiCreationNewParts.this.renderData);
                }
            });
            irendertypebuffer$impl.draw();
            matrixstack.pop();
            posestack.pop();
            entityrenderermanager.setRenderShadows(true);
            RenderSystem.applyModelViewMatrix();
        }

        public void renderButton(DrawContext graphics, int xMouse, int yMouse, float tick) {
            if (this.parent.subgui == null) {
                // empty if block
            }
        }

        public void renderIcons(DrawContext graphics, int xMouse, int yMouse, float tick) {
            int color = -1;
            if (!this.basic) {
                if (this.hovered) {
                    color = -65536;
                }
                int x1 = this.getX();
                int x2 = this.getX() + 70;
                int y1 = this.getY();
                int y2 = this.getY() + 70 - 1;
                graphics.drawHorizontalLine(x1, x2, y1, color);
                graphics.drawHorizontalLine(x1, x2, y2, color);
                graphics.drawHorizontalLine(x1, y1, y2, color);
                graphics.drawHorizontalLine(x2, y1, y2, color);
                x1 = this.getX() + 70 - 16;
                x2 = this.getX() + 70;
                y1 = this.getY() + 1;
                y2 = this.getY() + 70 - 1;
                graphics.fill(x1, y1, x2, y2, -3750202);
                color = -1;
                x1 = this.getX() + 70 - 14;
                x2 = this.getX() + 70 - 2;
                y1 = this.getY() + 2;
                y2 = this.getY() + 14;
                graphics.fill(x1, y1, x2, y2, -16777216);
                graphics.drawHorizontalLine(x1, x2, y1, color);
                graphics.drawHorizontalLine(x1, x2, y2, color);
                graphics.drawHorizontalLine(x1, y1, y2, color);
                graphics.drawHorizontalLine(x2, y1, y2, color);
                if (!this.part.isEnabled) {
                    graphics.drawTextWithShadow(GuiCreationNewParts.this.minecraft.textRenderer, (Text)Text.literal((String)"X").formatted(Formatting.BOLD), x1 + 4, y1 + 3, 0xFF0000);
                } else if (this.selected) {
                    char c = (char)Integer.parseInt("2713", 16);
                    graphics.drawTextWithShadow(GuiCreationNewParts.this.minecraft.textRenderer, (Text)Text.literal((String)("" + c)).formatted(Formatting.BOLD), x1 + 3, y1 + 2, 65280);
                }
            }
            int guiY = this.getY() + 16;
            RenderSystem.setShaderTexture((int)0, (Identifier)colorWheel);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            int size = 14;
            int x1 = this.getX() + 70 - 15;
            int x2 = x1 + size;
            int y1 = guiY;
            int y2 = y1 + size;
            boolean bl = this.colorPickerHovered = xMouse >= x1 && yMouse >= y1 && xMouse < x2 && yMouse < y2;
            if (this.colorPickerHovered) {
                --x1;
                --y1;
                size = 16;
            }
            graphics.drawTexture(colorWheel, x1, y1, 0, 0.0f, 0.0f, size, size, size, size);
            guiY += 15;
            RenderSystem.setShaderTexture((int)0, (Identifier)buttonsResource);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            if (this.all.size() > 1) {
                x1 = this.getX() + 70 - 17;
                x2 = x1 + 6;
                y1 = guiY;
                y2 = y1 + 8;
                RenderSystem.setShaderTexture((int)0, (Identifier)buttonsResource);
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                this.hoverL = xMouse >= x1 && yMouse >= y1 && xMouse < x2 && yMouse < y2;
                graphics.drawTexture(buttonsResource, x1, y1, 0, this.hoverL ? 76 : 60, 6, 8);
                String s = "" + this.all.indexOf(this.part);
                graphics.drawTextWithShadow(GuiCreationNewParts.this.minecraft.textRenderer, s, (int)((float)x1 + 9.5f - (float)GuiCreationNewParts.this.minecraft.textRenderer.getWidth(s) / 2.0f), (int)((float)y1 + 0.5f), 0);
                RenderSystem.setShaderTexture((int)0, (Identifier)buttonsResource);
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                x1 = this.getX() + 70 - 5;
                x2 = x1 + 6;
                y1 = guiY;
                y2 = y1 + 8;
                this.hoverR = xMouse >= x1 && yMouse >= y1 && xMouse < x2 && yMouse < y2;
                graphics.drawTexture(buttonsResource, x1, y1, 6, this.hoverR ? 76 : 60, 6, 8);
                guiY += 11;
            }
            if (!this.basic) {
                if (this.selected) {
                    x1 = this.getX() + 70 - 15;
                    x2 = x1 + 14;
                    y1 = guiY;
                    y2 = y1 + 14;
                    this.settingsHovered = xMouse >= x1 && yMouse >= y1 && xMouse < x2 && yMouse < y2;
                    graphics.drawTexture(buttonsResource, x1, y1, 0, this.settingsHovered ? 140 : 126, 14, 14);
                }
                size = 8;
                x1 = this.getX() + 70 - 10;
                x2 = x1 + size;
                y1 = this.getY() + 70 - 12;
                y2 = y1 + size;
                this.infoHovered = xMouse >= x1 && yMouse >= y1 && xMouse < x2 && yMouse < y2;
                MutableText text = Text.literal((String)"i").formatted(Formatting.BOLD);
                if (this.infoHovered) {
                    text = text.formatted(Formatting.UNDERLINE);
                }
                graphics.drawTextWithShadow(GuiCreationNewParts.this.minecraft.textRenderer, (Text)text, x1 + 3, y1 + 2, 0);
            }
        }

        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (!super.clicked(mouseX, mouseY)) {
                return false;
            }
            if (this.colorPickerHovered) {
                GuiCreationNewParts.this.openSubgui(this.parent, new GuiModelColor(this.parent, this.data.getColor(), color -> {
                    this.data.setColor(color);
                    GuiCreationNewParts.this.save();
                }));
            } else if (this.hoverL) {
                int index = (this.all.indexOf(this.part) + this.all.size() - 1) % this.all.size();
                this.part = this.all.get(index);
                this.data.partId = this.part.id;
            } else if (this.hoverR) {
                int index = (this.all.indexOf(this.part) + 1) % this.all.size();
                this.part = this.all.get(index);
                this.data.partId = this.part.id;
            } else if (this.settingsHovered) {
                if (this.data instanceof ModelEyeData) {
                    GuiCreationNewParts.this.openEyesSubgui(this.parent, (ModelEyeData)this.data, (MpmPartEyes)this.part);
                } else {
                    GuiCreationNewParts.this.openTextureSubgui(this.parent, this.data, this.part);
                }
            } else if (this.part.isEnabled && !this.basic) {
                boolean bl = this.selected = !this.selected;
                if (this.selected) {
                    GuiCreationNewParts.this.data.mpmParts.add(this.data);
                } else {
                    GuiCreationNewParts.this.data.mpmParts.removeIf(t -> t.partId.equals(this.data.partId));
                }
            }
            GuiCreationNewParts.this.data.refreshParts();
            GuiCreationNewParts.this.save();
            return true;
        }

        protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
        }

        @Override
        public int getID() {
            return this.id;
        }

        @Override
        public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
            if (this.parent.subgui == null) {
                this.renderModel(graphics, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void onRenderPost(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
            if (this.parent.subgui == null) {
                this.renderIcons(graphics, mouseX, mouseY, partialTicks);
                if (this.infoHovered) {
                    List<Text> text = Arrays.asList(Text.translatable((String)this.part.name), Text.translatable((String)"message.madeby", (Object[])new Object[]{this.part.author}));
                    if (!this.part.isEnabled) {
                        text = new ArrayList<Text>();
                        text.add((Text)Text.translatable((String)"gui.disabled", (Object[])new Object[]{this.part.author}));
                    }
                    this.parent.hoverText = text;
                }
            }
        }

        @Override
        public void init() {
        }

        @Override
        public ICustomGuiComponent component() {
            return null;
        }
    }

    public class PartsWrapper
    implements ICustomGuiComponent {
        private GuiMpmPart part;

        public PartsWrapper(GuiMpmPart part) {
            this.part = part;
        }

        @Override
        public int getID() {
            return this.part.getID();
        }

        @Override
        public ICustomGuiComponent setID(int id) {
            return this;
        }

        @Override
        public UUID getUniqueID() {
            return null;
        }

        @Override
        public int getPosX() {
            return this.part.getX();
        }

        @Override
        public int getPosY() {
            return this.part.getY();
        }

        @Override
        public ICustomGuiComponent setPos(int x, int y) {
            return this;
        }

        @Override
        public int getWidth() {
            return this.part.getWidth();
        }

        @Override
        public int getHeight() {
            return this.part.getHeight();
        }

        @Override
        public ICustomGuiComponent setSize(int width, int height) {
            return null;
        }

        @Override
        public boolean hasHoverText() {
            return false;
        }

        @Override
        public String[] getHoverText() {
            return new String[0];
        }

        @Override
        public ICustomGuiComponent setHoverText(String text) {
            return null;
        }

        @Override
        public ICustomGuiComponent setHoverText(String[] text) {
            return null;
        }

        @Override
        public ICustomGuiComponent setEnabled(boolean bo) {
            return this;
        }

        @Override
        public boolean getVisible() {
            return true;
        }

        @Override
        public ICustomGuiComponent setVisible(boolean bo) {
            return null;
        }

        @Override
        public boolean getEnabled() {
            return true;
        }

        @Override
        public int getType() {
            return -1;
        }
    }

    class TexturePart
    extends GuiCustom {
        private MpmPart part;
        private MpmPartData data;
        private GuiMpmPart partGui;

        public TexturePart(MpmPartData data, MpmPart part) {
            super((ContainerCustomGui)GuiCreationNewParts.this.parent.getScreenHandler(), GuiCreationNewParts.this.parent.inv, (Text)Text.empty());
            this.data = data;
            this.part = part;
            this.partGui = new GuiMpmPart(this, 70, 2, 2, part);
            this.partGui.zPos = 250;
            this.partGui.basic = true;
            this.guiWrapper = new CustomGuiWrapper(null);
            this.guiWrapper.addComponent(new PartsWrapper(this.partGui));
        }

        @Override
        public void init() {
            super.init();
            this.add(this.partGui);
        }

        @Override
        public void close() {
            super.close();
            GuiCreationNewParts.this.save();
        }
    }

    class EyesPart
    extends GuiCustom {
        private MpmPartEyes part;
        private ModelEyeData data;

        public EyesPart(ModelEyeData data, MpmPartEyes part) {
            super((ContainerCustomGui)GuiCreationNewParts.this.parent.getScreenHandler(), GuiCreationNewParts.this.parent.inv, (Text)Text.empty());
            this.data = data;
            this.part = part;
        }

        @Override
        public void init() {
            super.init();
            this.components.components.put(23, new GuiColorButton(this, (CustomGuiButtonWrapper)this.guiWrapper.getComponent(23), ColorUtil.rgbToColor(this.data.color)));
            this.components.components.put(34, new GuiColorButton(this, (CustomGuiButtonWrapper)this.guiWrapper.getComponent(34), ColorUtil.rgbToColor(this.data.browColor)));
            this.components.components.put(40, new GuiColorButton(this, (CustomGuiButtonWrapper)this.guiWrapper.getComponent(40), ColorUtil.rgbToColor(this.data.lidColor)));
        }

        public void renderBackground(DrawContext graphics) {
            super.renderBackground(graphics);
        }

        @Override
        public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
            super.render(graphics, mouseX, mouseY, partialTicks);
            MatrixStack posestack = RenderSystem.getModelViewStack();
            posestack.push();
            posestack.translate((double)(this.x + 10), (double)(this.y + 10), 150.0);
            posestack.scale(1.0f, 1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.push();
            EntityRenderDispatcher entityrenderermanager = this.client.getEntityRenderDispatcher();
            entityrenderermanager.setRenderShadows(false);
            VertexConsumerProvider.Immediate irendertypebuffer$impl = this.client.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer ivertex = irendertypebuffer$impl.getBuffer(RenderLayer.getEntityCutoutNoCull((Identifier)GuiCreationNewParts.this.npc.textureLocation));
            DiffuseLighting.disableGuiDepthLighting();
            RenderSystem.runAsFancy(() -> {
                GuiCreationNewParts.biped.body.visible = !this.part.hiddenParts.contains(BodyPart.BODY);
                GuiCreationNewParts.biped.jacket.visible = GuiCreationNewParts.biped.jacket.visible && GuiCreationNewParts.biped.body.visible;
                GuiCreationNewParts.biped.head.visible = !this.part.hiddenParts.contains(BodyPart.HEAD);
                GuiCreationNewParts.biped.hat.visible = GuiCreationNewParts.biped.hat.visible && GuiCreationNewParts.biped.head.visible;
                matrixstack.translate(19.0f, 43.0f, 25.0f);
                matrixstack.scale(100.0f, 100.0f, 100.0f);
                GuiCreationNewParts.biped.head.render(matrixstack, ivertex, 0xF000F0, OverlayTexture.DEFAULT_UV);
                this.part.pos = NopVector3f.ZERO;
                this.part.rot = NopVector3f.ZERO;
                LayerParts.renderPart(this.data, this.part, matrixstack, (VertexConsumerProvider)irendertypebuffer$impl, 0xF000F0, GuiCreationNewParts.this.npc, (BipedEntityModel)biped, GuiCreationNewParts.this.renderData);
            });
            irendertypebuffer$impl.draw();
            matrixstack.pop();
            posestack.pop();
            entityrenderermanager.setRenderShadows(true);
            RenderSystem.applyModelViewMatrix();
        }

        @Override
        public void close() {
            super.close();
            GuiCreationNewParts.this.save();
        }
    }
}

