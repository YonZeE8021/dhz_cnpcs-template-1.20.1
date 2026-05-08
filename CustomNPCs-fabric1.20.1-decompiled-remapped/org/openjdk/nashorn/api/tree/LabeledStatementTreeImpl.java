/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.LabeledStatementTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.LabelNode;

final class LabeledStatementTreeImpl
extends StatementTreeImpl
implements LabeledStatementTree {
    private final String name;
    private final StatementTree stat;

    LabeledStatementTreeImpl(LabelNode node, StatementTree stat) {
        super(node);
        this.name = node.getLabelName();
        this.stat = stat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.LABELED_STATEMENT;
    }

    @Override
    public String getLabel() {
        return this.name;
    }

    @Override
    public StatementTree getStatement() {
        return this.stat;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitLabeledStatement(this, data);
    }
}

