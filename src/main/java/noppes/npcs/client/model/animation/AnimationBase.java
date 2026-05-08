/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 */
package noppes.npcs.client.model.animation;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public interface AnimationBase {
    public void animatePre(float var1, float var2, float var3, float var4, float var5, Entity var6, BipedEntityModel var7, int var8);

    public void animatePost(float var1, float var2, float var3, float var4, float var5, Entity var6, BipedEntityModel var7, int var8);
}

