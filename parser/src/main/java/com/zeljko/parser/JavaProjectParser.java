package com.zeljko.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.zeljko.model.Class;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaProjectParser {

    private final ClassVisitor classVisitor = new ClassVisitor();

    public List<Class> scan(Path projectRoot) throws Exception {
        List<Path> files;
        try (var stream = Files.walk(projectRoot)) {
            files = stream.filter(f -> f.toString().endsWith(".java")).toList();
        }

        List<Class> classes = new ArrayList<>();
        for (var file : files) {
            CompilationUnit cu = StaticJavaParser.parse(file);
            for (var cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                classes.add(classVisitor.visit(cls, cu));
            }
        }

        return classes;
    }
}
