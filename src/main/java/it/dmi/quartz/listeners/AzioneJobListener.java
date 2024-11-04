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
        log.debug("Jobs for Azione {} to be executed.", aID);
    }

    @Override
    public void jobExecutionVetoed (JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted (JobExecutionContext jobExecutionContext, JobExecutionException e) {
        if (e != null) {
            manager.onAzioneJobFail(aID, e);
        } else {
            log.debug("Jobs for Config {} executed.", aID);
            manager.onAzioneJobCompletion(aID);
        }
    }
}
