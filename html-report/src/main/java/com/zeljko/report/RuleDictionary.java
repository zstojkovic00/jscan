package com.zeljko.report;

import java.util.Map;

public class RuleDictionary {

    private static final Map<Integer, String> RULES = Map.ofEntries(
            Map.entry(1, "Ako je metoda kratka i ima mali broj parametara to ukazuje na nizak rizik slozenosti"),
            Map.entry(2, "Ako je metoda kratka i ima osrednji broj parametara to ukazuje na nizak rizik slozenosti."),
            Map.entry(3, "Ako je metoda kratka i ima veliki broj parametara to ukazuje na srednji rizik slozenosti"),
            Map.entry(4, "Ako je metoda srednje duzine i ima mali broj parametara to ukazuje na nizak rizik slozenosti"),
            Map.entry(5, "Ako je metoda srednje duzine i ima osrednji broj parametara to ukazuje na srednji rizik slozenosti"),
            Map.entry(6, "Ako je metoda srednje duzine i ima veliki broj parametara to ukazuje na visok rizik slozenosti"),
            Map.entry(7, "Ako je metoda dugacka i ima mali broj parametara to ukazuje na srednji rizik slozenosti"),
            Map.entry(8, "Ako je metoda dugacka i ima osrednji broj parametara to ukazuje na visiok rizik slozenosti"),
            Map.entry(9, "Ako je metoda dugacka i ima veliki broj parametara to ukazuje na kritican rizik slozenosti"),
            Map.entry(10, "Ako je metoda veoma dugacka ili ima ekstremno ugnjezdavanje to ukazuje na kritican rizik slozenosti"),
            Map.entry(11, "Ako je metoda kratka i ima mali broj poziva to ukazuje na nizak rizik slozenosti"),
            Map.entry(12, "Ako je metoda srednje duzine i ima osrednji broj poziva to ukazuje na srednji rizik slozenosti"),
            Map.entry(13, "Ako je metoda srednje duzine i ima veliki broj poziva to ukazuje na visok rizik slozenosti"),
            Map.entry(14, "Ako je metoda dugacka i ima veliki broj poziva to ukazuje na kritican rizik slozenosti"),
            Map.entry(15, "Ako metoda ima veliki broj poziva i veliki broj parametara to ukazuje na kritican rizik slozenosti"),
            Map.entry(16, "Ako je ugnjezdavanje nepostojece to ukazuje na nizak rizik slozenosti"),
            Map.entry(17, "Ako je ugnjezdavanje umereno i metoda je kratka to ukazuje na nizak rizik slozenosti"),
            Map.entry(18, "Ako je ugnjezdavanje umereno i metoda je srednje duzine to ukazuje na srednji rizik slozenosti"),
            Map.entry(19, "Ako je ugnjezdavanje duboko i metoda je srednje duzine to ukazuje na visok rizik slozenosti"),
            Map.entry(20, "Ako je ugnjezdavanje duboko i metoda nema mali broj parametara to ukazuje na kritican rizik slozenosti"),
            Map.entry(21, "Ako je metoda veoma dugacka i ima umereno ugnjezdavanje to ukazuje na visok rizik slozenosti"),
            Map.entry(22, "Ako je metoda dugacka i ima mali broj parametara ali veliki broj poziva to ukazuje na visok rizik slozenosti")
    );

    public static String translate(String ruleToString, double degreeOfSupport) {
        try {
            int ruleNumber = Integer.parseInt(ruleToString.trim().split("\\s+")[0]);
            String s1 = RULES.getOrDefault(ruleNumber, ruleToString);
            String s2 = String.format("(stepen aktivacije: %.2f)", degreeOfSupport);
            return s1.replaceFirst(":", " " + s2 + ":");
        } catch (NumberFormatException e) {
            return ruleToString;
        }
    }
}
