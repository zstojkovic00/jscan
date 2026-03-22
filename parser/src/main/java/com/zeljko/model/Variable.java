package com.zeljko.model;

public sealed interface Variable permits ObjectVariable, ResourceVariable {
    String name();
    String type();
    String value();
}
