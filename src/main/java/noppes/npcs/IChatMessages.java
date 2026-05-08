/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 */
package noppes.npcs;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import noppes.npcs.entity.EntityNPCInterface;

public interface IChatMessages {
    public void addMessage(String var1, EntityNPCInterface var2);

    public void renderMessages(MatrixStack var1, VertexConsumerProvider var2, float var3, boolean var4, int var5);
}

