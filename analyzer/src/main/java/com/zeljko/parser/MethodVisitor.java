package com.zeljko.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import com.zeljko.model.*;

import java.util.List;
import java.util.Objects;

public class MethodVisitor {

    public Method visit(MethodDeclaration method) {
        List<Variable> params = method.getParameters().stream()
                .map(p -> toVariable(
                        p.getNameAsString(),
                        p.getTypeAsString(),
                        null,
                        false)
                )
                .toList();

        List<Variable> localVariables = method.findAll(VariableDeclarator.class).stream()
                .map(variable -> toVariable(
                        variable.getNameAsString(),
                        variable.getTypeAsString(),
                        variable.getInitializer()
                                .map(Object::toString)
                                .orElse(null),
                        isTryWithResources(variable)
                ))
                .toList();

        List<MethodCall> calls = method.findAll(MethodCallExpr.class).stream()
                .map(call -> new MethodCall(
                        call.getNameAsString(),
                        call.getScope()
                                .map(Objects::toString)
                                .orElse(null),
                        call.getArguments().stream()
                                .map(Objects::toString)
                                .toList(),
                        isInFinallyBlock(call)
                ))
                .toList();

        return new Method(method.getNameAsString(), method.getTypeAsString(), params, localVariables, calls);
    }

    private Variable toVariable(String name, String type, String value, boolean isInTryWithResources) {
        if (ResourceType.isResource(type)) {
            return new ResourceVariable(name, type, value, isInTryWithResources);
        } else {
            return new ObjectVariable(name, type, value);
        }
    }

    private boolean isTryWithResources(VariableDeclarator vd) {
        return vd.getParentNode()
                .filter(p -> p instanceof VariableDeclarationExpr)
                .flatMap(Node::getParentNode)
                .filter(p -> p instanceof TryStmt)
                .isPresent();
    }

    /* TryStmt
        tryBlock
            finallyBlock
                ExpressionStmt
                    MethodCallExpr example; ps.close()
     */
    private boolean isInFinallyBlock(MethodCallExpr call) {
        Node current = call.getParentNode().orElse(null);
        while (current != null) {
            if (current instanceof TryStmt tryStmt) {
                if (tryStmt.getFinallyBlock().filter(fb -> fb.isAncestorOf(call)).isPresent()) {
                    return true;
                }
            }
            current = current.getParentNode().orElse(null);
        }
        return false;
    }
}
