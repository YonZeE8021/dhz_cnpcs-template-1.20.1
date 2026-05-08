/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.Diagnostic;

@FunctionalInterface
public interface DiagnosticListener {
    public void report(Diagnostic var1);
}

