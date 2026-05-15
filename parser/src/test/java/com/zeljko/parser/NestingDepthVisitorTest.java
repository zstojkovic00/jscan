package com.zeljko.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NestingDepthVisitorTest {
    private NestingDepthVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new NestingDepthVisitor();
    }

    private MethodDeclaration parse(String body) {
        CompilationUnit cu = StaticJavaParser.parse(
                "class Klasa { " + body + " }"
        );
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @Test
    void withoutDepth_shouldReturnZero() {
        MethodDeclaration method = parse("""
                void test1() {
                int z = 0;
                return;
                }
                """);

        assertEquals(0, visitor.calculate(method));
    }

    @Test
    void nestedDepth_shouldReturnTwo() {
        MethodDeclaration method = parse("""
                void test2() {
                int z = 1;
                int z2 = 2;
                if(z > 0){
                    while(z2 > 0) {
                        return;
                    }
                }
                return;
                }
                """);

        assertEquals(2, visitor.calculate(method));
    }

}