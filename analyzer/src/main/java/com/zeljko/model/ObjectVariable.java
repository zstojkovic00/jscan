package com.zeljko.model;

public record ObjectVariable(
        String name,
        String type,
        String value
) implements Variable {}
