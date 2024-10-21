package it.dmi.utils.thresholds;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public class ThresholdUtils {

    private final String prequel = "Azione di risposta alla configurazione n. %d (nome: %s) eseguita correttamente.\n";

    private final String singleValueCompared = "[Soglia ID: %d; Valore registrato: %d; " +
            "Valore singolo configurato: %d; Operatore: %s; " +
            "Risultato: %s;]\n";

    private final String doubleValueResult = "[Soglia ID: %d; Valori soglia configurati-> soglia minima: %d, soglia massima: %d; " +
            "Valore registrato: %d; Risultato: %s]\n";

    private static final Map<String, BiPredicate<Integer, Integer>> SV_COMPARATORS;

    static {
        Map<String, BiPredicate<Integer, Integer>> tempMap = new HashMap<>();
        tempMap.put("<", (a, b) -> a < b);
        tempMap.put(">", (a, b) -> a > b);
        tempMap.put("<=", (a, b) -> a <= b);
        tempMap.put(">=", (a, b) -> a >= b);
        tempMap.put("<>", (a, b) -> !Objects.equals(a, b));
        tempMap.put("=", Objects::equals);
        tempMap.put("==", Objects::equals);
        SV_COMPARATORS = Collections.unmodifiableMap(tempMap); // Make the map unmodifiable
    }

    public static Map<String, BiPredicate<Integer, Integer>> getSvComparators() {
        return SV_COMPARATORS;
    }

}
