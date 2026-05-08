/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface BlockTree
extends StatementTree {
    public List<? extends StatementTree> getStatements();
}

