package com.zeljko.model;

import java.util.List;

public record Class(
        String name,
        String packageName,
        List<Variable> variables,
        List<Method> methods
) {}
