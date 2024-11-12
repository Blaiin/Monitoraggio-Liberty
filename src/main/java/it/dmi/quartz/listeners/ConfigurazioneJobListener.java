package it.dmi.quartz.listeners;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.quartz.ejb.Manager;
import it.dmi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import java.util.List;
import java.util.Optional;

import static it.dmi.utils.constants.NamingConstants.SOGLIE;

@Slf4j
public class ConfigurazioneJobListener implements JobListener {

    private final String cID;
    private final Manager manager;

    public ConfigurazioneJobListener(Configurazione c, Manager m) {
        this.cID = c.getStrID();
        this.manager = m;
    }
    @Override
    public String getName () {
        return "ConfigJobListener" + cID;
    }

    @Override
    public void jobToBeExecuted (JobExecutionContext jobExecutionContext) {
        log.debug("Job (Config {}) about to start.", cID);
    }

    @Override
    public void jobExecutionVetoed (JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        if (e != null) {
            manager.onConfigJobFail(cID, e);
        }
        log.debug("Job (Config {}) executed.", cID);
        Optional<List<String>> fromCache = AzioneQueueCache.getSoglieIDs(SOGLIE + cID);
        List<String> toSanitize = fromCache.orElseGet(List::of);
        List<String> soglieIDs = Utils.transformAndReturn(toSanitize, String.class);
        if (!soglieIDs.isEmpty()) {
            manager.onConfigJobCompletion(cID, soglieIDs);
        } else {
            log.warn("No retrievable Soglie for Config {}.", cID);
        }
    }
}
