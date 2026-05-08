/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.GotoTree;

public interface BreakTree
extends GotoTree {
    @Override
    public String getLabel();
}

