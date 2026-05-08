/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.FunctionCall;
import org.openjdk.nashorn.internal.ir.Optimistic;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.parser.TokenType;

@Immutable
public abstract class BaseNode
extends Expression
implements FunctionCall,
Optimistic {
    private static final long serialVersionUID = 1L;
    protected final Expression base;
    private final boolean isFunction;
    protected final Type type;
    protected final int programPoint;
    private final boolean isSuper;

    public BaseNode(long token, int finish, Expression base, boolean isFunction, boolean isSuper) {
        super(token, base.getStart(), finish);
        this.base = base;
        this.isFunction = isFunction;
        this.type = null;
        this.programPoint = -1;
        this.isSuper = isSuper;
    }

    protected BaseNode(BaseNode baseNode, Expression base, boolean isFunction, Type callSiteType, int programPoint, boolean isSuper) {
        super(baseNode);
        this.base = base;
        this.isFunction = isFunction;
        this.type = callSiteType;
        this.programPoint = programPoint;
        this.isSuper = isSuper;
    }

    public Expression getBase() {
        return this.base;
    }

    @Override
    public boolean isFunction() {
        return this.isFunction;
    }

    @Override
    public Type getType() {
        return this.type == null ? this.getMostPessimisticType() : this.type;
    }

    @Override
    public int getProgramPoint() {
        return this.programPoint;
    }

    @Override
    public Type getMostOptimisticType() {
        return Type.INT;
    }

    @Override
    public Type getMostPessimisticType() {
        return Type.OBJECT;
    }

    @Override
    public boolean canBeOptimistic() {
        return true;
    }

    public boolean isIndex() {
        return this.isTokenType(TokenType.LBRACKET);
    }

    public abstract BaseNode setIsFunction();

    public boolean isSuper() {
        return this.isSuper;
    }

    public abstract BaseNode setIsSuper();
}

