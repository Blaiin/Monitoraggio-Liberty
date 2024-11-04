package it.dmi.quartz.listeners;

import it.dmi.data.entities.task.Configurazione;
import it.dmi.quartz.ejb.ManagerEJB;
import it.dmi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import java.util.List;

import static it.dmi.utils.constants.NamingConstants.SOGLIE;

@Slf4j
public class ConfigurazioneJobListener implements JobListener {

    private final String cID;
    private final ManagerEJB manager;

    public ConfigurazioneJobListener(Configurazione c, ManagerEJB m) {
        this.cID = c.getStringID();
        this.manager = m;
    }
    @Override
    public String getName () {
        return "ConfigJobListener" + cID;
    }

    @Override
    public void jobToBeExecuted (JobExecutionContext jobExecutionContext) {
        log.debug("Jobs for Config {} about to start.", cID);
    }

    @Override
    public void jobExecutionVetoed (JobExecutionContext jobExecutionContext) {

    }

    //TODO resolve Soglie not retrievable from jobDataMap
    @Override
    public void jobWasExecuted (JobExecutionContext jobExecutionContext, JobExecutionException e) {
        if (e != null) {
            manager.onConfigJobFail(cID, e);
        }
        log.debug("Jobs for Config {} executed.", cID);
        var fromJobDataMap = jobExecutionContext.getJobDetail().getJobDataMap().get(SOGLIE + cID);
        List<String> soglieIDs = Utils.transformAndReturn(fromJobDataMap, String.class);
        if (!soglieIDs.isEmpty()) {
            manager.onConfigJobCompletion(cID, soglieIDs);
        } else {
            log.warn("No retrievable Soglie for Config {}.", cID);
        }
    }
}
