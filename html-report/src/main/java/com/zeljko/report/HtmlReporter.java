package com.zeljko.report;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class HtmlReporter {

    private final TemplateEngine engine;

    public HtmlReporter() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    public void write(List<MethodResult> results, double score, Path output) throws IOException {
        Context ctx = new Context();
        ctx.setVariable("results", results);
        ctx.setVariable("score", String.format("%.2f", score));

        try (FileWriter writer = new FileWriter(output.toFile())) {
            engine.process("report", ctx, writer);
        }

        try (InputStream css = HtmlReporter.class.getResourceAsStream("/templates/report.css")) {
            Files.copy(
                    Objects.requireNonNull(css),
                    output.getParent().resolve("report.css"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}
