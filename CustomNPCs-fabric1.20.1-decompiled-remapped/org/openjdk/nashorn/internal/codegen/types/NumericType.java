/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.codegen.types;

import org.openjdk.nashorn.internal.codegen.types.BytecodeNumericOps;
import org.openjdk.nashorn.internal.codegen.types.Type;

public abstract class NumericType
extends Type
implements BytecodeNumericOps {
    private static final long serialVersionUID = 1L;

    protected NumericType(String name, Class<?> clazz, int weight, int slots) {
        super(name, clazz, weight, slots);
    }
}

