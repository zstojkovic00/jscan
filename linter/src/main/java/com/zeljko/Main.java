package com.zeljko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeljko.model.Class;
import com.zeljko.model.Method;
import com.zeljko.parser.JavaProjectParser;
import net.sourceforge.jFuzzyLogic.FIS;

import java.nio.file.Path;
import java.nio.file.Paths;
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
        prettyPrint(classes);

        FIS fis = FIS.load(Main.class.getResourceAsStream("/complexity.fcl"), false);

        for (Class cls : classes) {
            for (Method method : cls.methods()) {
                int paramCount = method.parameters().size();
                int callCount = method.calls().size();

                fis.setVariable("methodLength", method.lineCount());
                fis.setVariable("paramCount", paramCount);
                fis.setVariable("callCount", callCount);
                fis.evaluate();

                double risk = fis.getVariable("complexityRisk").defuzzify();

                System.out.println("[COMPLEXITY] " + cls.name() + "." + method.name()
                        + " (lines=" + method.lineCount() + ", params=" + paramCount + ", calls=" + callCount + ")"
                        + " -> risk=" + String.format("%.1f", risk) + " (" + riskLabel(risk) + ")");
            }
        }

        /*for (Rule r : fis.getFunctionBlock("methodComplexity").getFuzzyRuleBlock("rules")) {
            System.out.println(r);
        }*/
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
