package com.zeljko.model;

public record ResourceVariable(
        String name,
        String type,
        String value,
        boolean isInTryWithResources
) implements Variable {}
