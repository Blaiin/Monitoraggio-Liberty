package it.dmi.processors.thresholds;

import it.dmi.data.entities.Soglia;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
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

    public List<String> compareCountThresholds(Configurazione config, Map<String, Integer> results) {
        List<String> soglieIDs = new ArrayList<>();
        config.getSoglie().forEach(s -> {
            int value = results.get("count");
            var sID = s.getStringID();
            if (isSogliaMultivalue(s)) {
                boolean result = isInRange(value, s);
                if (result) {
                    log.debug("Enabling actions for C: {}, S: {}", config.getId(), sID);
                    var azioni = s.getAzioniOrdered();
                    log.debug("Azioni fetched: {}", azioni.size());
                    soglieIDs.add(sID);
                    azioni.forEach(Azione::queue);
                } else {
                    log.warn("No actions scheduled for Configurazione n. {}, Soglia n. {}, value outside range.",
                            config.getId(), sID);
                }
            } else {
                log.error("Could not compare thresholds for Configurazione n. {}, Soglia n. {}. " +
                        "Invalid or missing count value.", config.getId(), sID);
            }
        });
        return soglieIDs;
    }

    //TODO reactivate method usage and SELECT functionality
    @SuppressWarnings("unused")
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
                            } else log.error("No actions queued for Config n. {}, Soglia n. {}",
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

    //TODO check for method usage or delete
    @SuppressWarnings("unused")
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
        if (toEvaluate instanceof String s) {
            return s.contains(soglia.getValore());
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

