package com.zeljko.model;

import java.util.List;

public record MethodCall(
        String name,
        String calledOnVariable,
        List<String> args
) {
}
