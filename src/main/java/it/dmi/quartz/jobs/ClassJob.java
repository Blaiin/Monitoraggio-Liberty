package it.dmi.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static it.dmi.utils.constants.NamingConstants.ID;

public class ClassJob implements Job {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        var identity = dataMap.get("CLASSE" + ID);
        if (identity == null) throw new JobExecutionException("Could not execute CLASSE job");
    }
}
