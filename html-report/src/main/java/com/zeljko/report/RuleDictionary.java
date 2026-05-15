package com.zeljko.report;

import java.util.Map;

public class RuleDictionary {

    private static final Map<Integer, String> RULES = Map.ofEntries(
            Map.entry(1, "Pravilo 1: Metoda je kratka i ima mali broj parametara, sto ukazuje na nizak rizik slozenosti."),
            Map.entry(2, "Pravilo 2: Metoda je kratka ali ima umeren broj parametara, sto ukazuje na nizak rizik slozenosti."),
            Map.entry(3, "Pravilo 3: Metoda je kratka ali ima veliki broj parametara, sto ukazuje na umeren rizik slozenosti."),
            Map.entry(4, "Pravilo 4: Metoda je srednje duzine sa malim brojem parametara, sto predstavlja nizak rizik slozenosti."),
            Map.entry(5, "Pravilo 5: Metoda je srednje duzine sa umerenim brojem parametara, sto ukazuje na umeren rizik slozenosti."),
            Map.entry(6, "Pravilo 6: Metoda je srednje duzine ali ima veliki broj parametara, sto ukazuje na visokim rizikom slozenosti."),
            Map.entry(7, "Pravilo 7: Metoda je dugacka ali ima mali broj parametara, sto ukazuje na umeren rizik slozenosti."),
            Map.entry(8, "Pravilo 8: Metoda je dugacka i ima umeren broj parametara, sto ukazuje na visok rizik slozenosti."),
            Map.entry(9, "Pravilo 9: Metoda je dugacka i ima veliki broj parametara, sto ukazuje na visoko rizican nivo slozenosti."),
            Map.entry(10, "Pravilo 10: Metoda je veoma dugacka, sto samo po sebi predstavlja kritican rizik za odrzavanje."),
            Map.entry(11, "Pravilo 11: Metoda je kratka i ima mali broj poziva, sto ukazuje na nizak rizik slozenosti."),
            Map.entry(12, "Pravilo 12: Metoda je srednje duzine sa umerenim brojem poziva, sto ukazuje na umeren rizik slozenosti."),
            Map.entry(13, "Pravilo 13: Metoda je srednje duzine ali poziva veliki broj drugih metoda, sto predstavlja visok rizik slozenosti."),
            Map.entry(14, "Pravilo 14: Metoda je dugacka i poziva veliki broj drugih metoda, sto ukazuje na visoko rizican nivo slozenosti."),
            Map.entry(15, "Pravilo 15: Metoda ima veliki broj poziva i veliki broj parametara, sto ukazuje na visoko rizican nivo slozenosti."),
            Map.entry(16, "Pravilo 16: Ugnezdjavanje je nepostojece, sto ukazuje na nizak rizik slozenosti."),
            Map.entry(17, "Pravilo 17: Ugnezdjavanje je umereno a metoda je kratka, sto ukazuje na nizak rizik slozenosti."),
            Map.entry(18, "Pravilo 18: Ugnezdjavanje je umereno u metodi srednje duzine, sto ukazuje na umeren rizik slozenosti."),
            Map.entry(19, "Pravilo 19: Ugnezdjavanje je duboko u metodi srednje duzine, sto predstavlja visok rizik citljivosti."),
            Map.entry(20, "Pravilo 20: Ugnezdjavanje je duboko i metoda ima veliki broj parametara,  sto ukazuje na visoko rizican nivo slozenosti."),
            Map.entry(21, "Pravilo 21: Ugnezdjavanje je ekstremno duboko i predstavlja arrow code anti-pattern, sto ukazuje na visoko rizican nivo slozenosti.")
    );

    public static String translate(String ruleToString) {
        try {
            int ruleNumber = Integer.parseInt(ruleToString.trim().split("\\s+")[0]);
            return RULES.getOrDefault(ruleNumber, ruleToString);
        } catch (NumberFormatException e) {
            return ruleToString;
        }
    }
}
