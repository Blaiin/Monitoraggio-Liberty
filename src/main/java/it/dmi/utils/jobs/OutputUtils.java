package it.dmi.utils.jobs;

import it.dmi.caches.JobDataCache;
import it.dmi.data.dto.OutputDTO;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static it.dmi.structure.internal.Esito.NEGATIVE;
import static it.dmi.structure.internal.Esito.POSITIVE;
import static it.dmi.utils.constants.NamingConstants.OUTPUT;

@Slf4j
public class OutputUtils {

    public static OutputDTO initializeOutputDTO (QuartzTask task) {
        OutputDTO out = new OutputDTO();
        out.setInizio(TimeUtils.now());
        switch (task) {
            case Azione a -> out.setAzioneId(a.getId());
            case Configurazione c -> out.setConfigurazioneId(c.getId());
        }
        return out;
    }

    public static void finalizeOutputDTO(OutputDTO out, Map<String, ?> results) {
        if (out.getConfigurazioneId() != null)
            log.debug("Finalizing (select) output for Config {}", out.getConfigurazioneId());
        if (out.getAzioneId() != null)
            log.debug("Finalizing (select) output for Azione {}", out.getAzioneId());
        if(results.isEmpty()) {
            if (out.getConfigurazioneId() != null)
                log.warn("Esito (select) was negative for Config {}", out.getConfigurazioneId());
            if (out.getAzioneId() != null)
                log.warn("Esito (select) was negative for Azione {}", out.getAzioneId());
            out.setEsito(NEGATIVE.getValue());
        }
        else out.setEsito(POSITIVE.getValue());
        out.setContenuto(results);
        var fine = TimeUtils.now();
        out.setFine(fine);
        out.setDurata(TimeUtils.duration(out.getInizio(), fine));
    }

    public static void finalizeOutputDTO(OutputDTO out, int result) {
        if (out.getConfigurazioneId() != null)
            log.debug("Finalizing (select count) output for Config {}", out.getConfigurazioneId());
        if (out.getAzioneId() != null)
            log.debug("Finalizing (select count) output for Azione {}", out.getAzioneId());
        if(result == 0) {
            if (out.getConfigurazioneId() != null)
                log.warn("Esito (select count) was negative for Config {}", out.getConfigurazioneId());
            if (out.getAzioneId() != null)
                log.warn("Esito (select count) was negative for Azione {}", out.getAzioneId());
            out.setEsito(NEGATIVE.getValue());
        }
        else out.setEsito(POSITIVE.getValue());
        out.setContenuto(Map.of("count", result));
        var fine = TimeUtils.now();
        out.setFine(fine);
        out.setDurata(TimeUtils.duration(out.getInizio(), fine));
    }

    public static void cacheOutputDTO(String id, OutputDTO out) {
        if (out.getAzioneId() == null && out.getConfigurazioneId() == null) {
            log.error("Invalid generated output, both id fields were null");
            return;
        }
        if (out.getConfigurazioneId() != null) {
            log.debug("Caching output for Config {}", out.getConfigurazioneId());
            JobDataCache.put(OUTPUT + id, out);
            return;
        }
        log.debug("Caching output for Azione {}", out.getAzioneId());
        JobDataCache.put(OUTPUT + id, out);
    }
}
