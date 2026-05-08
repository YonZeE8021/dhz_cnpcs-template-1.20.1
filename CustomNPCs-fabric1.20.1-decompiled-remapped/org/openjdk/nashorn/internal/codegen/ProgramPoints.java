/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.codegen;

import java.util.HashSet;
import java.util.Set;
import org.openjdk.nashorn.internal.IntDeque;
import org.openjdk.nashorn.internal.ir.AccessNode;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.CallNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.IndexNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Optimistic;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.ir.VarNode;
import org.openjdk.nashorn.internal.ir.visitor.SimpleNodeVisitor;

class ProgramPoints
extends SimpleNodeVisitor {
    private final IntDeque nextProgramPoint = new IntDeque();
    private final Set<Node> noProgramPoint = new HashSet<Node>();

    ProgramPoints() {
    }

    private int next() {
        int next = this.nextProgramPoint.getAndIncrement();
        if (next > 131071) {
            throw new AssertionError((Object)"Function has more than 131071 program points");
        }
        return next;
    }

    @Override
    public boolean enterFunctionNode(FunctionNode functionNode) {
        this.nextProgramPoint.push(1);
        return true;
    }

    @Override
    public Node leaveFunctionNode(FunctionNode functionNode) {
        this.nextProgramPoint.pop();
        return functionNode;
    }

    private Expression setProgramPoint(Optimistic optimistic) {
        if (this.noProgramPoint.contains(optimistic)) {
            return (Expression)((Object)optimistic);
        }
        return (Expression)((Object)(optimistic.canBeOptimistic() ? optimistic.setProgramPoint(this.next()) : optimistic));
    }

    @Override
    public boolean enterVarNode(VarNode varNode) {
        this.noProgramPoint.add(varNode.getName());
        return true;
    }

    @Override
    public boolean enterIdentNode(IdentNode identNode) {
        if (identNode.isInternal()) {
            this.noProgramPoint.add(identNode);
        }
        return true;
    }

    @Override
    public Node leaveIdentNode(IdentNode identNode) {
        if (identNode.isPropertyName()) {
            return identNode;
        }
        return this.setProgramPoint(identNode);
    }

    @Override
    public Node leaveCallNode(CallNode callNode) {
        return this.setProgramPoint(callNode);
    }

    @Override
    public Node leaveAccessNode(AccessNode accessNode) {
        return this.setProgramPoint(accessNode);
    }

    @Override
    public Node leaveIndexNode(IndexNode indexNode) {
        return this.setProgramPoint(indexNode);
    }

    @Override
    public Node leaveBinaryNode(BinaryNode binaryNode) {
        return this.setProgramPoint(binaryNode);
    }

    @Override
    public Node leaveUnaryNode(UnaryNode unaryNode) {
        return this.setProgramPoint(unaryNode);
    }
}

