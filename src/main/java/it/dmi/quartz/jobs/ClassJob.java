package it.dmi.quartz.jobs;

import it.dmi.quartz.jobs.sql.ISQLJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ClassJob implements ISQLJob {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
