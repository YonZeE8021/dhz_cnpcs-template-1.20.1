/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface ReturnTree
extends StatementTree {
    public ExpressionTree getExpression();
}

