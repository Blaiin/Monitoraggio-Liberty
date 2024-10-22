package it.dmi.utils.thresholds;

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
        SV_COMPARATORS = Map.of("<", (a, b) -> a < b, ">", (a, b) -> a > b, "<=", (a, b) -> a <= b, ">=", (a, b) -> a >= b, "<>", (a, b) -> !Objects.equals(a, b), "=", Objects::equals, "==", Objects::equals); // Make the map unmodifiable
    }

    public static Map<String, BiPredicate<Integer, Integer>> getSvComparators() {
        return SV_COMPARATORS;
    }

}
