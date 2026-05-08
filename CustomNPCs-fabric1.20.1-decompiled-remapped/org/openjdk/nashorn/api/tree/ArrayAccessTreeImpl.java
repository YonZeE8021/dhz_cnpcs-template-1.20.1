/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ArrayAccessTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Expression;

final class ArrayAccessTreeImpl
extends ExpressionTreeImpl
implements ArrayAccessTree {
    private final ExpressionTree base;
    private final ExpressionTree index;

    ArrayAccessTreeImpl(Expression node, ExpressionTree base, ExpressionTree index) {
        super(node);
        this.base = base;
        this.index = index;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.ARRAY_ACCESS;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.base;
    }

    @Override
    public ExpressionTree getIndex() {
        return this.index;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitArrayAccess(this, data);
    }
}

