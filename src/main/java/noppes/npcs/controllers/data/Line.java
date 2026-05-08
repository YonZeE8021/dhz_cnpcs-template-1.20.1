/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 */
package noppes.npcs.controllers.data;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import noppes.npcs.api.entity.data.ILine;

public class Line
implements ILine {
    protected String text = "";
    protected String sound = "";
    private boolean showText = true;

    public Line() {
    }

    public Line(String text) {
        this.text = text;
    }

    public Line copy() {
        Line line = new Line(this.text);
        line.sound = this.sound;
        line.showText = this.showText;
        return line;
    }

    public static Line formatTarget(Line line, LivingEntity entity) {
        if (entity == null) {
            return line;
        }
        Line line2 = line.copy();
        line2.text = entity instanceof PlayerEntity ? line2.text.replace("@target", ((PlayerEntity)entity).getDisplayName().getString()) : line2.text.replace("@target", entity.getName().getString());
        return line;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
    }

    @Override
    public String getSound() {
        return this.sound;
    }

    @Override
    public void setSound(String sound) {
        if (sound == null) {
            sound = "";
        }
        this.sound = sound;
    }

    @Override
    public boolean getShowText() {
        return this.showText;
    }

    @Override
    public void setShowText(boolean show) {
        this.showText = show;
    }
}

