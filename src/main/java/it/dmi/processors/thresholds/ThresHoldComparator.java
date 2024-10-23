package it.dmi.processors.thresholds;

import it.dmi.data.entities.Azione;
import it.dmi.data.entities.Configurazione;
import it.dmi.data.entities.Soglia;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Slf4j
public class ThresHoldComparator {

    private final Map<String, List<String>> comparationMessages = new ConcurrentHashMap<>();

    public void compareCountThresholds(Configurazione config, Map<String, Integer> results) {
        config.getSoglie().forEach(s -> {
            int value = results.get("count");
            if (isSogliaMultivalue(s)) {
                boolean result = isInRange(value, s);
                if (result) {
                    log.info("Enabling actions for C: {}, S: {}", config.getId(), s.getId());
                    var azioni = s.getAzioniOrdered();
                    log.info("Azioni fetched: {}", azioni.size());
                    azioni.forEach(Azione::queue);
                } else {
                    log.warn("No actions scheduled for Configurazione n. {}, Soglia n. {}, value outside range.",
                            config.getId(), s.getId());
                }
            } else {
                log.error("Could not compare thresholds for Configurazione n. {}, Soglia n. {}. " +
                        "Invalid or missing count value.", config.getId(), s.getId());
            }
        });
    }

    public void compareSelectThresholds (Configurazione config,
                                         Map<String, List<Object>> mapToCompare) {
        mapToCompare.forEach((k, v) -> {
            List<String> messages = new ArrayList<>();
            config.getSoglie().forEach(s -> {
                if(!v.isEmpty()) {
                    if (isSogliaMultivalue(s)) {
                        Object value = v.getFirst();
                        if (value instanceof Integer) {
                            boolean result = isInRange((Integer) value, s);
                            if(result) {
                                s.getAzioni().forEach(Azione::queue);
                            } else log.error("No actions queued for Configurazione n. {}, Soglia n. {}",
                                    config.getId(), s.getId());
                        } else if (value instanceof String sValue) {
                            if(isNumericValue(sValue)) {
                                boolean result = isInRange(Integer.parseInt(sValue), s);
                                if(result) {
                                    s.getAzioni().forEach(Azione::queue);
                                } else log.error("No actions queued for Configurazione n. {}, Soglia n. {}",
                                        config.getId(), s.getId());
                            }
                        }
                    } else {
                        for (Object value : v) {
                            boolean result = evaluate(value, s);
                            if (result) {
                                log.info("Enabling action for Configurazione n. {}, Soglia n. {}",
                                        config.getId(), s.getId());
                                s.getAzioni().forEach(Azione::queue);
                            }
                        }
                    }

                } else {
                    log.error("Could finish Configurazione n. {}, output was null.", config.getId());
                }

            });
            comparationMessages.put(k, messages);
        });
    }

    public static boolean evaluate(int toEvaluate, Soglia soglia) {
        if (ThresholdUtils.getSvComparators().get(soglia.getOperatore()) == null) {
            throw new IllegalArgumentException("Invalid or null operator.");
        }
        if (isNumericValue(soglia.getValore())) {
            return ThresholdUtils.getSvComparators().get(soglia.getOperatore())
                    .test(toEvaluate, Integer.valueOf(soglia.getValore()));
        } else return false;

    }

    public static boolean evaluate(Object toEvaluate, Soglia soglia) {
        if (toEvaluate instanceof String) {
            return ((String) toEvaluate).contains(soglia.getValore());
        }
        return false;
    }

    private static boolean isNumericValue(String valore) {
        try {
            Integer.parseInt(valore);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean isInRange(int toBeCompared, Soglia soglia) {
        return (soglia.getSogliaInferiore().intValue() <= toBeCompared)
                && (toBeCompared <= soglia.getSogliaSuperiore().intValue());
    }

    static boolean isSogliaMultivalue(Soglia soglia) throws IllegalArgumentException {
        if(soglia.getValore() != null && (soglia.getSogliaInferiore() != null && soglia.getSogliaSuperiore() != null))
            throw new IllegalArgumentException("Both single threshold and multi threshold for comparison had values, " +
                    "cannot determine logic.");
        if(soglia.getValore() == null && (soglia.getSogliaInferiore() == null && soglia.getSogliaSuperiore() == null))
            throw new IllegalArgumentException("Invalid configuration for Soglia: " + soglia.getId() + ", at least" +
                    "one value must be present(Valore OR (both) Soglia Inferiore AND Soglia Superiore).");
        return soglia.getValore() == null;
    }


}

