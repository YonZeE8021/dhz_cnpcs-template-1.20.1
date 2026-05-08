/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface BinaryTree
extends ExpressionTree {
    public ExpressionTree getLeftOperand();

    public ExpressionTree getRightOperand();
}

