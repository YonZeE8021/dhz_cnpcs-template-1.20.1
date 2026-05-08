/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface FunctionDeclarationTree
extends StatementTree {
    public IdentifierTree getName();

    public List<? extends ExpressionTree> getParameters();

    public BlockTree getBody();

    public boolean isStrict();

    public boolean isGenerator();
}

