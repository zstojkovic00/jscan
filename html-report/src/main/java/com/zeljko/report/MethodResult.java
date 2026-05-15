package com.zeljko.report;

import java.util.List;

public record MethodResult(
        String className,
        String methodName,
        int lineCount,
        int paramCount,
        int callCount,
        int nestingDepth,
        String sourceCode,
        double score,
        String risk,
        List<String> firedRules
) {}
