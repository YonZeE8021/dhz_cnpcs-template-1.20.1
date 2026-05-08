/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface ExportEntryTree
extends Tree {
    public IdentifierTree getExportName();

    public IdentifierTree getModuleRequest();

    public IdentifierTree getImportName();

    public IdentifierTree getLocalName();
}

