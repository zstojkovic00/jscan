package com.zeljko.model;

import java.util.List;

public record Method(
        String name,
        String returnType,
        List<Variable> parameters,
        List<Variable> localVariables,
        List<MethodCall> calls
) {
}
