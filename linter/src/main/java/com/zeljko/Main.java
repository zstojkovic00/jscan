package com.zeljko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeljko.model.Class;
import com.zeljko.model.Method;
import com.zeljko.parser.JavaProjectParser;
import com.zeljko.report.HtmlReporter;
import com.zeljko.report.MethodResult;
import net.sourceforge.jFuzzyLogic.FIS;

import com.zeljko.report.RuleDictionary;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import org.jfree.chart.JFreeChart;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Wrong number of arguments");
            System.exit(1);
        }

        Path projectToScan = Paths.get(args[0]);

        if (!projectToScan.toFile().exists()) {
            System.err.println("Project does not exist");
            System.exit(1);
        }

        if (!projectToScan.toFile().isDirectory()) {
            System.err.println("Project path is not a directory");
            System.exit(1);
        }

        List<Class> classes = new JavaProjectParser().scan(projectToScan);
//        prettyPrint(classes);

        FIS fis = FIS.load(Main.class.getResourceAsStream("/complexity.fcl"), false);

        List<MethodResult> results = new ArrayList<>();

        for (Class cls : classes) {
            for (Method method : cls.methods()) {
                int paramCount = method.parameters().size();
                int callCount = method.calls().size();

                fis.setVariable("methodLength", method.lineCount());
                fis.setVariable("paramCount", paramCount);
                fis.setVariable("callCount", callCount);
                fis.setVariable("nestingDepth", method.nestingDepth());
                fis.evaluate();

                Variable complexityRisk = fis.getVariable("complexityRisk");
                double score = complexityRisk.getLatestDefuzzifiedValue();
                String risk = riskLabel(score);

                List<String> firedRules = new ArrayList<>();
                RuleBlock rb = fis.getFunctionBlock("methodComplexity").getFuzzyRuleBlock("rules");
                for (Rule rule : rb) {
                    if (rule.getDegreeOfSupport() > 0) {
                        firedRules.add(RuleDictionary.translate(rule.toString(), rule.getDegreeOfSupport()));
                    }
                }

//                System.out.println("[COMPLEXITY] " + cls.name() + "." + method.name()
//                        + " (lines=" + method.lineCount() + ", params=" + paramCount + ", calls=" + callCount + ", nesting=" + method.nestingDepth() + ")"
//                        + " -> risk=" + String.format("%.1f", score) + " (" + risk + ")");


                JFuzzyChartImpl jFuzzyChart = new JFuzzyChartImpl();
                JFreeChart jFreeChart = jFuzzyChart.chart(
                        complexityRisk,
                        complexityRisk.getDefuzzifier()
                );

                BufferedImage image = jFreeChart.createBufferedImage(800, 300);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                String chart = Base64.getEncoder().encodeToString(baos.toByteArray());

                results.add(new MethodResult(
                        cls.name(),
                        method.name(),
                        method.lineCount(),
                        paramCount,
                        callCount,
                        method.nestingDepth(),
                        method.sourceCode(),
                        score,
                        risk,
                        firedRules,
                        chart
                ));
            }
        }

        results.sort(Comparator.comparingDouble(MethodResult::score).reversed());
        double averageScore = results.stream().mapToDouble(MethodResult::score).average().orElse(0);

        Path report = projectToScan.resolve("report.html");
        new HtmlReporter().write(results, averageScore, report);
        System.out.println("Report: " + report.toAbsolutePath());
    }

    private static String riskLabel(double risk) {
        if (risk < 35) return "LOW";
        if (risk < 55) return "MEDIUM";
        if (risk < 75) return "HIGH";
        return "CRITICAL";
    }

    private static void prettyPrint(List<Class> classes) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(classes, System.out);
        System.out.println();
    }
}
