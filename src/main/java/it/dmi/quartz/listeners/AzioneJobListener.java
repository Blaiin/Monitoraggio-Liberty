package it.dmi.quartz.listeners;

import it.dmi.data.entities.task.Azione;
import it.dmi.quartz.ejb.ManagerEJB;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class AzioneJobListener implements JobListener {

    private final String aID;
    private final ManagerEJB manager;

    public AzioneJobListener(Azione azione, ManagerEJB managerEJB) {
        this.aID = azione.getStringID();
        this.manager = managerEJB;
    }

    @Override
    public String getName () {
        return "AzioneJobListener" + aID;
    }

    @Override
    public void jobToBeExecuted (JobExecutionContext jobExecutionContext) {
        log.debug("Job for Azione {} to be executed.", aID);
    }

    @Override
    public void jobExecutionVetoed (JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted (JobExecutionContext jobExecutionContext, JobExecutionException e) {
        log.debug("Manager instance: {}", manager);
        if (e == null) {
            log.debug("Job for Config {} executed.", aID);
            manager.onAzioneJobCompletion(aID);
            return;
        }
        log.warn("Job (Azione {}) encountered an error {} during execution.", aID, e.getMessage(), e.getCause());
        manager.onAzioneJobFail(aID);
    }
}
