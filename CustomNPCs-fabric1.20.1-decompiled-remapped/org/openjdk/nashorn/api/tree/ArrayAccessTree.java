/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface ArrayAccessTree
extends ExpressionTree {
    public ExpressionTree getExpression();

    public ExpressionTree getIndex();
}

