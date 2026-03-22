package com.zeljko.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.zeljko.model.*;
import com.zeljko.model.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassVisitor {

    private final MethodVisitor methodVisitor = new MethodVisitor();

    public Class visit(ClassOrInterfaceDeclaration cls, CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse("");

        List<Variable> classFields = new ArrayList<>();
        for (var field : cls.getFields()) {
            for (var variable : field.getVariables()) {
                String name = variable.getNameAsString();
                String type = variable.getTypeAsString();
                String value = variable.getInitializer()
                        .map(Object::toString)
                        .orElse(null);
                classFields.add(new ObjectVariable(name, type, value));
            }
        }

        var methods = cls.getMethods().stream()
                .map(methodVisitor::visit)
                .toList();

        return new Class(cls.getNameAsString(), packageName, classFields, methods);
    }
}
