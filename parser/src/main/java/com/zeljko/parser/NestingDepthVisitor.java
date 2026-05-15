package com.zeljko.parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

public class NestingDepthVisitor {

    public int calculate(MethodDeclaration method) {
        return method.getBody()
                .map(body -> maxDepth(body, 0))
                .orElse(0);
    }

    private int maxDepth(Statement stmt, int current) {
        int max = current;

        if (stmt instanceof IfStmt s) {
            max = Math.max(max, maxDepth(s.getThenStmt(), current + 1));
            if (s.getElseStmt().isPresent()) {
                max = Math.max(max, maxDepth(s.getElseStmt().get(), current + 1));
            }

        } else if (stmt instanceof ForStmt s) {
            max = Math.max(max, maxDepth(s.getBody(), current + 1));

        } else if (stmt instanceof ForEachStmt s) {
            max = Math.max(max, maxDepth(s.getBody(), current + 1));

        } else if (stmt instanceof WhileStmt s) {
            max = Math.max(max, maxDepth(s.getBody(), current + 1));

        } else if (stmt instanceof DoStmt s) {
            max = Math.max(max, maxDepth(s.getBody(), current + 1));

        } else if (stmt instanceof TryStmt s) {
            max = Math.max(max, maxDepth(s.getTryBlock(), current + 1));

        } else if (stmt instanceof SwitchStmt s) {
            for (SwitchEntry entry : s.getEntries()) {
                for (Statement child : entry.getStatements()) {
                    max = Math.max(max, maxDepth(child, current + 1));
                }
            }
        } else if (stmt instanceof BlockStmt s) {
            for (Statement child : s.getStatements()) {
                max = Math.max(max, maxDepth(child, current));
            }
        }

        return max;
    }
}
