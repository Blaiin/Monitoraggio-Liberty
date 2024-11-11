package it.dmi.quartz.listeners;

import it.dmi.data.entities.task.Azione;
import it.dmi.quartz.ejb.Manager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class AzioneJobListener implements JobListener {

    private final String aID;
    private final Manager manager;

    public AzioneJobListener(@NotNull Azione azione, @NotNull Manager manager) {
        this.aID = azione.getStrID();
        this.manager = manager;
    }

    @Override
    public String getName() {
        return "AzioneJobListener" + aID;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        log.debug("Job (Azione {}) to be executed.", aID);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        if (e != null) {
            manager.onAzioneJobFail(aID, e);
            return;
        }
        log.debug("Job (Azione {}) executed.", aID);
        manager.onAzioneJobCompletion(aID);
    }
}
