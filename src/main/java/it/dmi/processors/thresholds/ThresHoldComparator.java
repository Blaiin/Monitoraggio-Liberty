package it.dmi.processors.thresholds;

import it.dmi.data.dto.SogliaDTO;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class ThresHoldComparator {

    public static List<String> compareCountThresholds(Configurazione config, final int result) {
        List<String> soglieIDs = new ArrayList<>();
        config.getSoglieDTOAsStream()
            .filter(SogliaDTO::isMultiValue)
            .forEach(s -> {
                var sID = s.getStrID();
                if (s.range(result, true)) {
                    var azioni = s.getAzioniOrdered();
                    log.debug("Enabling {} actions for C: {}, S: {}", azioni.size(), config.getId(), sID);
                    soglieIDs.add(sID);
                    azioni.forEach(Azione::queue);
                } else log.warn("S: {} not applicable for Config {} result, value outside range.",
                            sID, config.getId());
            });
        log.info("Active Soglie detected: {} Values: {}", soglieIDs.size(), soglieIDs);
        return soglieIDs;
    }

    public static List<String> compareCountTH(final Configurazione config, final int result) {
        var cID = config.getStrID();
        List<String> activeSoglieIDs = config.getSoglieDTOAsStream()
                .filter(SogliaDTO::isMultiValue)
                .filter(s -> s.resultWithinRange(result, cID))
                .peek(s -> s.queueActions(cID))
                .map(SogliaDTO::getStrID)
                .toList();
        log.info("Active Soglie: {} (Config {}), Values: {}", activeSoglieIDs.size(), cID, activeSoglieIDs);
        return activeSoglieIDs;
    }

    //TODO reactivate method usage and SELECT functionality
    @SuppressWarnings("unused")
    public void compareSelectThresholds (Configurazione config,
                                         Map<String, List<Object>> mapToCompare) {
        mapToCompare.forEach((k, v) ->
                config.getSoglieDTOAsStream().forEach(s -> {
            if (v.isEmpty()) {
                log.error("Could not finish Config {}, output was null.", config.getId());
                return;
            }
            if (!s.singleValue()) {
                return;
            }
            for (Object value : v) {
                if (s.compare((String) value)) {
                    log.info("Queueing Azione for Config {} (Soglia {})",
                            config.getId(), s.getId());
                    s.getAzioni().forEach(Azione::queue);
                }
            }
        }));
    }


}

