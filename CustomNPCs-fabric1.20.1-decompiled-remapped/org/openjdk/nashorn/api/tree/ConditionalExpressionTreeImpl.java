/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ConditionalExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.TernaryNode;

final class ConditionalExpressionTreeImpl
extends ExpressionTreeImpl
implements ConditionalExpressionTree {
    private final ExpressionTree condExpr;
    private final ExpressionTree trueExpr;
    private final ExpressionTree falseExpr;

    ConditionalExpressionTreeImpl(TernaryNode node, ExpressionTree condExpr, ExpressionTree trueExpr, ExpressionTree falseExpr) {
        super(node);
        this.condExpr = condExpr;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.CONDITIONAL_EXPRESSION;
    }

    @Override
    public ExpressionTree getCondition() {
        return this.condExpr;
    }

    @Override
    public ExpressionTree getTrueExpression() {
        return this.trueExpr;
    }

    @Override
    public ExpressionTree getFalseExpression() {
        return this.falseExpr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitConditionalExpression(this, data);
    }
}

