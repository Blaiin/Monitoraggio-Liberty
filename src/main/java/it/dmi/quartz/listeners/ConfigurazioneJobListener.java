package it.dmi.quartz.listeners;

import it.dmi.data.entities.task.Configurazione;
import it.dmi.quartz.ejb.ManagerEJB;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class ConfigurazioneJobListener implements JobListener {

    private final Configurazione config;
    private final String cID;
    private final ManagerEJB manager;

    public ConfigurazioneJobListener(Configurazione c, ManagerEJB m) {
        this.config = c;
        this.cID = c.getStringID();
        this.manager = m;
    }
    @Override
    public String getName () {
        return "ConfigJobListener" + config.getStringID();
    }

    @Override
    public void jobToBeExecuted (JobExecutionContext jobExecutionContext) {
        log.debug("Job for Config {} about to start.", cID);
    }

    @Override
    public void jobExecutionVetoed (JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted (JobExecutionContext jobExecutionContext, JobExecutionException e) {
        log.debug("Manager instance: {}", manager);
        if (e == null) {
            log.debug("Job for Config {} executed.", cID);
            manager.onConfigJobCompletion(cID);
            return;
        }
        log.warn("Job encountered an error {} during execution.", e.getMessage());
        manager.onConfigJobFail(cID);
    }
}
