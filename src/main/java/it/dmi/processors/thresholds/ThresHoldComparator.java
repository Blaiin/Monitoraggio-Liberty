package it.dmi.processors.thresholds;

import it.dmi.data.entities.Soglia;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class ThresHoldComparator {

    public static List<String> compareCountThresholds(Configurazione config, final int result) {
        final List<String> soglieIDs = new ArrayList<>();
        config.getSoglie().stream()
            .filter(Soglia::isMultiValue)
            .forEach(s -> {
                var sID = s.getStringID();
                if (s.range(result, true)) {
                    var azioni = s.getAzioniOrdered();
                    log.debug("Enabling {} actions for C: {}, S: {}", azioni.size(), config.getId(), sID);
                    soglieIDs.add(sID);
                    azioni.forEach(Azione::queue);
                } else log.warn("S: {} not applicable for Config {} result, value outside range.",
                            sID, config.getId());
            });
        log.info("Active Soglie detected: {}", soglieIDs.size());
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
                    if (s.isMultiValue()) {
                        Object value = v.getFirst();
                        if (value instanceof Integer integer) {
                            if(s.range(integer, true)) {
                                s.getAzioni().forEach(Azione::queue);
                            } else log.error("No actions queued for Config n. {}, Soglia n. {}",
                                    config.getId(), s.getId());
                        } else if (value instanceof String sValue) {
                            if(Utils.TH.isNumeric(sValue)) {
                                if(s.range(Integer.parseInt(sValue), true)) {
                                    s.getAzioni().forEach(Azione::queue);
                                } else log.error("No actions queued for Configurazione n. {}, Soglia n. {}",
                                        config.getId(), s.getId());
                            }
                        }
                    } else {
                        for (Object value : v) {
                            if (s.compare(value)) {
                                log.info("Enabling action for Configurazione n. {}, Soglia n. {}",
                                        config.getId(), s.getId());
                                s.getAzioni().forEach(Azione::queue);
                            }
                        }
                    }
                } else {
                    log.error("Could not finish Configurazione n. {}, output was null.", config.getId());
                }

            });
        });
    }


}

