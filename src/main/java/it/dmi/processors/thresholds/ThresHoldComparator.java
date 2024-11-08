package it.dmi.processors.thresholds;

import it.dmi.data.dto.SogliaDTO;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class ThresHoldComparator {

    public static @NotNull List<String> compareCountTH(final @NotNull Configurazione config, final int result) {
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
                                         @NotNull Map<String, List<Object>> mapToCompare) {
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

